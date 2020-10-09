## 1. Objective

Revisit the example of customer-orders. Remind that we should compute the total amount of spendings made by each customer in our list. Also, in this modoule,
you are only required to use Dataset rathern RDD. 

## 2.Code

```scala


import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.log4j._
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{FloatType,IntegerType,StringType,StructType}


object RatingsCounter {

  case class Customer(stationID:Int,itemID:Int,amount:Float)
  def main(args:Array[String]){
    Logger.getLogger("org").setLevel(Level.ERROR)
    val spark=SparkSession
              .builder
              .appName("customer")
              .master("local[*]")
              .getOrCreate()
    val schema=new StructType()
               .add("stationID",IntegerType,nullable=true)
               .add("itemID",IntegerType,nullable=true)
               .add("amount",FloatType,nullable=true)
    import spark.implicits._
    val ds=spark.read
           .schema(schema)
           .csv("../customer-orders.csv")
           .as[Customer]
    
    val purchaseById=ds.groupBy("stationID").agg(round(sum("amount"),2).alias("total"))
    val sortedPurchase=purchaseById.sort("total")
    sortedPurchase.show(sortedPurchase.count.toInt)
    
  }
}
  

  


```




### 3.Output

![image](https://user-images.githubusercontent.com/53164959/95545481-f0d6fe00-0a38-11eb-842c-b1aef6807ea2.png)

