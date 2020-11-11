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
