
package retail.db
import scala.collection.mutable._
import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import org.apache.spark.sql._
import org.apache.log4j._
import org.apache.spark.sql.functions._
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.sql.{DataFrame, Dataset}
import org.apache.spark.sql.types.{DoubleType, FloatType, IntegerType, StringType, StructType}
import org.apache.spark.sql.functions.regexp_extract

import scala.util.matching.Regex

trait BaseSetHandler[T]{
  val subPath:String
  def readContents(spark:SparkSession)(implicit enc:Encoder[T]):Dataset[T]
}

object Default{
  val default_enprov=ConfigFactory.load()
  val default_header="dev"
  val assignedHeader=default_enprov.getConfig(default_header)
}


case class Order(order_id:Int,order_date:String,order_customer_id:Int,order_status:String)
object OrderInfo extends BaseSetHandler[Order]{
  override val subPath:String="input.base.dir"
  val fileAdd=Default.assignedHeader.getString(subPath)+"/orders.txt"

  val schema=new StructType()
    .add("order_id",IntegerType)
    .add("order_date",StringType)
    .add("order_customer_id",IntegerType)
    .add("order_status",StringType)

  override def readContents(spark:SparkSession)(implicit enc:Encoder[Order]):Dataset[Order]={
    import spark.implicits._
    val table=spark
      .read
      .option("sep",",")
      .schema(schema)
      .csv(fileAdd)
      .as[Order]

    //do a trim operation in the colum of order date
    val pattern="\\d{4}-\\d{2}-\\d{2}"
    val result=table.withColumn("order_date",regexp_extract(col("order_date"),pattern,0)).as[Order]
    result

  }
}

case class ProductItem(id:Int,order_id:Int,product_id:Int,quantity:Int,subtotal:Float,product_price:Float)
object OrderItem extends BaseSetHandler[ProductItem]{
  override val subPath:String="input.base.dir"
  val fileAdd=Default.default_enprov.getConfig(Default.default_header).getString(subPath)+"/order_items.txt"
  val schema=new StructType()
    .add("id",IntegerType)
    .add("order_id",IntegerType)
    .add("product_id",IntegerType)
    .add("quantity",IntegerType)
    .add("subtotal",FloatType)
    .add("product_price",FloatType)

  override def readContents(spark: SparkSession)(implicit enc: Encoder[ProductItem]): Dataset[ProductItem] = {
    val df=spark
      .read
      .schema(schema)
      .option("sep",",")
      .csv(fileAdd)
      .as[ProductItem]

    df
  }
}


object Merge {
  val outputBaseDir = Default.assignedHeader.getString("output.base.dir")

  def mergeTable(df1: Dataset[Order], df2: Dataset[ProductItem]) = {
    val result = df1.where("order_status in ('CLOSED','Complete')")
      .join(df2, usingColumns = Seq("order_id"), joinType = "inner")
      .groupBy("order_date", "product_id")
      .agg(round(sum("subtotal"), 2).alias("revenue"))
      .orderBy(col("order_date"), col("revenue").desc)
    result

  }

}

object test2 {
  def main(args: Array[String]) = {
    Logger.getLogger("org").setLevel(Level.ERROR)
    @transient lazy val logger = Logger.getLogger(getClass.getName)
    logger.info("Retrive an excution mode.By default,it is an internal device")
    val exMode = Default.default_enprov.getConfig(Default.default_header).getString("execution.mode")

    val spark = SparkSession
      .builder
      .appName("revenueAnalysis")
      .master(exMode)
      .getOrCreate()

    import spark.implicits._
    System.out.println("Now,we are porcessing an assigned task")
    val orderDF = OrderInfo.readContents(spark)
    val itemDF = OrderItem.readContents(spark)
    val data=Merge.mergeTable(orderDF,itemDF)
    System.out.println("Print out the first three rows of the merged table")
    data.show(3)
    data.write.mode("overwrite").json(Default.assignedHeader.getString("output.base.dir"))
  }
}
