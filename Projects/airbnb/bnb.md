
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
        .withColumn("average_rate",($"average_rate").toCast(FloatType))
        .withColumn("bedrooms",roomStatus($"bedrooms_count"))
        .filter($"city"==="Austin")
        .select("bedrooms","average_rate")
        .filter($"bedrooms"=!="null")
        
  val result=df.groupBy("bedrooms").agg(round(mean("average_rate"),2).alias("average_price_per_night($)"))
  result.show()

```
