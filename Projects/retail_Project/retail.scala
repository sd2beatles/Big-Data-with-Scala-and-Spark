package retail.db
import org.apache.spark.sql.types.{DateType, DoubleType, IntegerType, StringType, StructType}
import org.apache.spark.sql.SparkSession
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{DataFrame, Dataset, Encoder}
import org.apache.spark.sql.functions._
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.ml.feature.{StringIndexer,OneHotEncoder,VectorAssembler}
import org.apache.spark.ml.Pipeline
import java.sql.Date


trait BaseSetHandler[T,S]{
  val subPath:String
  val fileName:String
  val schema:StructType
  def readContent(spark:SparkSession)(implicit enc:Encoder[T]):Dataset[S]
}

object Default{
  val default_env=ConfigFactory.load()
  val default_header="dev"
  val assignedHeader=default_env.getConfig(default_header)
  val default_partition_no:Int=5
}


case class Order(orderID:Int,date:String,customerID:Int,orderStatus:String)
case class OrderHistory(orderID:Int,orderDate:Date,day_of_week:String,orderStatus:String)
object OrderRecord extends BaseSetHandler[Order,OrderHistory]{
  override val subPath:String="input.dir"
  override val fileName:String="orders.txt"
  val filePath=Default.assignedHeader.getString(subPath)+fileName
  override val schema=new StructType()
    .add("orderID",IntegerType)
    .add("date",StringType)
    .add("customerID",IntegerType)
    .add("orderStatus",StringType)



  override def readContent(spark:SparkSession)(implicit enc:Encoder[Order]):Dataset[OrderHistory]={
    import spark.implicits._
    val df=spark
      .read
      .options(Map("sep"->",","header"->"true"))
      .schema(schema)
      .csv(filePath)
      .as[Order]

    val modified=timeManagement(spark,df)
    modified
  }

  def timeManagement(spark:SparkSession,data:Dataset[Order])={
    import spark.implicits._
    val modified=data
      .withColumn("orderDate",to_date(col("date"),"yyyy-MM-dd"))
      .withColumn("day_of_week",date_format(col("orderDate"),"EEEE"))
      .select("orderID","orderDate","day_of_week","customerID","orderStatus")
      .as[OrderHistory]
    modified
  }
}

case class OrderInfo(id:Int,orderID:Int,productID:Int,quantity:Int,purchaseValue:Double,unitPrice:Double)
object OrderItem extends BaseSetHandler[OrderInfo,OrderInfo]{
  override val subPath:String="input.dir"
  override val fileName:String=Default.assignedHeader.getString(subPath)+"/order_items.txt"
  override val schema:StructType=new StructType()
    .add("id",IntegerType)
    .add("orderID",IntegerType)
    .add("productID",IntegerType)
    .add("quantity",IntegerType)
    .add("purchaseValue",DoubleType)
    .add("unitPrice",DoubleType)

  override def readContent(spark:SparkSession)(implicit enc:Encoder[OrderInfo]):Dataset[OrderInfo]={
  import spark.implicits._
   val df=spark
    .read
    .option("sep",",")
     .schema(schema)
    .csv(fileName)
    .as[OrderInfo]
    df
  }
}



object Modeling{
  def mergeTable(df1:Dataset[OrderHistory],df2:Dataset[OrderInfo],partitionNo:Int=5)={

    val merged=df1.where("orderStatus in ('CLOSED','Complete')")
      .join(df2,usingColumns=Seq("orderID"),joinType="inner")
      .coalesce(partitionNo)
    merged
  }


  def transformData(data:DataFrame,selectCols:Array[String],category:String): Unit ={
    val features=handlingCategorical(selectCols,category)
    val pipeline=new Pipeline().setStages(features)
    val fittedPipeline=pipeline.fit(data)
    val transformed=fittedPipeline.transform(data)

    transformed
  }

  def handlingCategorical(selectCols:Array[String],category:String)={
    val indexCol=category+"_index"
    val encodedCol=category+"_encoded"
    val totalCols=selectCols:+encodedCol

    val indexer=new StringIndexer()
      .setInputCol(category)
      .setOutputCol(indexCol)

    val encoder=new OneHotEncoder()
      .setInputCol(category+"_index")
      .setOutputCol(encodedCol)

    val vectorAssembler=new VectorAssembler()
      .setInputCols(totalCols)
      .setOutputCol("features")

    Array(indexer,encoder,vectorAssembler)
  }
  
}



object test3{
  def main(args:Array[String]): Unit ={
    Logger.getLogger("org").setLevel(Level.ERROR)


    val basicMode=Default.assignedHeader.getString("execution.mode")
    val spark=SparkSession
      .builder
      .appName("reatialAnalysis")
      .master(basicMode)
      .getOrCreate()

    spark.sql("set spark.sql.legacy.timeParserPolicy=LEGACY")
    import spark.implicits._
    val orderData=OrderRecord.readContent(spark)
    val info=OrderItem.readContent(spark)
    val merged=Modeling.mergeTable(orderData,info)
    merged.show(3)

  }
}



