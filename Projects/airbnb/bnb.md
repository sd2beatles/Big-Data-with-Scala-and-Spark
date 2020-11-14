```scala

def main(args:Array[String]){
     Logger.getLogger("org").setLevel(Level.ERROR)
     val spark=SparkSession
               .builder
               .appName("airbnb")
               .master("local[*]")
               .getOrCreate()
    import spark.implicits._
    var df=spark.read
          .options(Map("inferSchema"->"true","delimeter"->",","header"->"true"))
          .csv("../airbnb.csv")
    df.printSchema()
    //drop any rows which have null values only in the first two columns
   df.na.drop("all",Seq("average_rate_per_night","bedrooms_count"))
   df=df.withColumn("average_rate",regexp_replace($"average_rate_per_night","\\W+",""))
   df.printSchema()
   val toInt=udf[Int,String](_.toInt)
   df=df.withColumn("average_rate",toInt(col("average_rate")))
   df.printSchema()
   }
 ```
```scala


   def main(args:Array[String]){
    Logger.getLogger("org").setLevel(Level.ERROR)
    val spark=SparkSession
              .builder
              .appName("application")
              .master("local[*]")
              .getOrCreate()
              
              
   import spark.implicits._
   var df=spark.read
            .options(Map("inferSchema"->"true","delimeter"->"true","header"->"true"))
            .csv("../Airbnb_Texas_Rentals2.csv")
   df.na.drop("all",Seq("average_rate_per_night","bedrooms_count"))
   
   
   val toLong=udf[Long,String](_.toLong)
   val roomStatus=udf[String,String]{x:String=> if(x=="Studio") "0" else x}
   df=df.withColumn("average_rate",regexp_replace($"average_rate_per_night","\\W+",""))
        .withColumn("average_rate",toLong($"average_rate"))
        .withColumn("bedrooms",roomStatus($"bedrooms_count"))
        .filter($"city"==="Austin")
    
   val df_s=df.select("average_rate","city","bedrooms")
   


```
