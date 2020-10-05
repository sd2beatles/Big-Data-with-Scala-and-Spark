## 1. Objective

Using the samle example we have already done in the previous seciton, counnt the number of friends each age group has. This time, however, 
you are rquired to wirte codes with Datasets instead of RDD.

From this simple example, we will possibly find out some of the ways to avoid wrting the lengthy code using RDD. With the simple lines of code,we 
can actually obatin the exactly same output we had previously. Let's run the code!


## 2. Codes
```scala



import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.log4j._
import org.apache.spark.sql._
import org.apache.spark.sql.functions._


object RatingsCounter {
  case class FakeFriends(id:Int,name:String,age:Int,friends:Long)
  
  def main(args:Array[String]){
    Logger.getLogger("org").setLevel(Level.ERROR)
    val spark=SparkSession
              .builder
              .appName("fakeFriends")
              .master("local[*]")
              .getOrCreate()
    import spark.implicits._
    val ds=spark.read
           .option("header","true")
           .option("inferSchema","true")
           .csv("C:/SparkScalar/fakeFriends2.csv")
           .as[FakeFriends]
    
    //select only age and numFriends Columns
    val friendsByage=ds.select("age","friends")
    //from firendsByage we group by "age" and then compute the average
    friendsByage.groupBy("age").avg("friends").sort("age").show()
    //if you want round off average, you need to write codes as follows
    friendsByage.groupBy("age").agg(round(avg("friends"),2)).sort("age").show()
    //create a custom column name
    friendsByage.groupBy("age").agg(round(avg("friends"),2).alias("friends_avg")).sort("age").show()
    
    }

```

## 3.Results
![image](https://user-images.githubusercontent.com/53164959/95068592-752b3780-0740-11eb-929d-5839f1ba32fe.png)
