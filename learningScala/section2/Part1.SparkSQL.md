```scala
import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.log4j._
import org.apache.spark.sql._


object RatingsCounter {

  case class Person(id:Int,name:String,age:Int,friends:Int)
  
  
  def main(args:Array[String]){
    Logger.getLogger("org").setLevel(Level.ERROR)
    val spark=SparkSession
              .builder
              .appName("SparkSQL")
              .master("local[*]") //we are using our local machine on all cpu cores
              .getOrCreate() //we can either create a session or reuse the existing one
    import spark.implicits._
    val schemaPeople=spark.read
                     .option("header","true")
                     .option("inferSchema","true")//we are going to ask it to initially infer from what's in that header row
                     .csv("../fakefriends2.csv")
                     .as[Person]//converting the dataframe into a dataset by saying dot as[Person]
   
   //to printout the schema to make sure that is what we expected
    schemaPeople.printSchema()
   
   //now we create a DataBase view on it by saying createOrReplaceTempView
   schemaPeople.createOrReplaceTempView("people")
   
   //We can query that table now by saying spark.sql
  val teenagers=spark.sql("select * from people where age>=13 and age<=19")
//bring back all the results to the RDD  
val results=teenagers.collect()
  
  results.foreach(println)
  spark.stop()

   
  }
  }
```
  
