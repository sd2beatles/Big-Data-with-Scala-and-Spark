### Objective :

Load the data named "fakefriends.csv" and print out all the infromation including uniqueID,age,and number of friends. Make sure that
our focus is centered on those aged between 13 and 19. 

```scala

object RatingsCounter {
  case class Person(id:Int,name:String,age:Int,friends:Int)
  
  def main(args:Array[String]){
     Logger.getLogger("org").setLevel(Level.ERROR)
     val spark=SparkSession
               .builder
               .appName("SparkSQL")
               .master("local[*]")
               .getOrCreate()
     
     import spark.implicits._
     val schemaPeople=spark.read
                     .option("header","true")
                     .option("inferSchema","true")
                     .csv("C:/SparkScalar/fakefriends2.csv")
                     .as[Person]
      
    schemaPeople.createOrReplaceTempView(viewName="People")
    val teenager=spark.sql("select * from people where age>=13 and age<=19")
    val results=teenager.collect()
    results.foreach(println)
    spark.stop()
              
    }
  }
  

```
