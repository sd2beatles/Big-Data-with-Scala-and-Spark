Spark Real Example: Finding the Most Popular Movies
====================

## 1. Objective

You are given a dataset called movies,whose format is comprised of UserID,MovieID,Rating,and TimeStamp. All the columns are typed in intger. 
In this section, your aim is to find the most popular movie from the dataset. 

Before diving in the task, there are a couple of notions you should bear in mind. 

First, The defintion of popularity seems to vary from one to another. In our case only,we define the "popular" movive as the one most often shown in  this set. 

Second, as metioned previously, movie ids are given only to find that they are not readable to many of us.Fortunately,we have another dataset to store the information.
       Here is a suggested approach to obtain the names; laod the movies dataset and call map operations to look up the name using the movie id.
       This is where a concept of variable kicks in. See the following section to understand how the broadcast works. 
 

### Questioins to Answer 

#### _1) What is Broadcast Variable?

- Broadcast variables allow the programmer to keep a readable only variable cached on each machine rather than shipping a cop of it with tasks.

- They can bed used,for example,to give everynode a copy of a large input dataset in an efficient manner

- All broadcast variables will be kept at all the worker nodes for use in one or more spark operations. 

2)What problems you might encouter if you were not using broadcast variables?

- Spark automatically sends all variables referenced in our closures to the worker nodes.While this is convenient, it can also be inefficient beacasue 
  the default tasks launching mechanism is optimized for small task sizes
  
- We can potentially use the same variable in mutiple parallel operations, but spark will send it separately for each opertion. This can lead to a 
 serious performance issue if the size of data to transfer is significant. For example, let's say we had a large dictionary containing information on
 region and address. The size of the dicitonary is normally at least mega byte or even greater. To transfer it from master alongside the task is undobtedly expensieve.
 Also, if we wish to use the same dictionary later, the same dictionary would be sent again to each node. 
 
 _"The bottom line is that we can use forward(ship off) an object to every executor within our cluster by using broadcast variable and retrive and use it
   as needed!"_


 ## 2.code
```scala

import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.log4j._
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
//import org.apache.spark.sql.types.{FloatType,IntegerType,StringType,StructType}
import org.apache.spark.sql.types.{FloatType,IntegerType,StringType,StructType}
import org.apache.spark.sql.types.{DoubleType,IntegerType,StringType,StructType}

//stationID,itemID,value

object RatingsCounter {
  case class Customer(stationID:Int,itemID:Int,amount:Float)
 
  def main(args:Array[String]){
    Logger.getLogger("org").setLevel(Level.ERROR)
    val spark=SparkSession
              .builder
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
     val purchase=ds.groupBy("stationID").agg(round(sum("amount"),2).alias("total"))
     val sorted=purchase.sort("total")
     sorted.show()
    
   }
   
}
  

```







## 3.Useful Tips

1) UDF and BroadCast
Right after couting the number of frequencies for each movieID,now we need a way to  actually transform those movie ids into movie names within our Dataset.
This could be all done at the output state;We could possibly iterate the resulting Dataset that we got back and look up those movie names back on the driver 
script locally when are actually printing the results.

However, we wish to illustrate the use of UDFs and broadCast here we want to distribute within the cluster itself. So for every excutor, they are responbsible for some
subset of the data. They will individually be looking ta the movie titles for movie ids they are resoponsible for.

First, we need to set up a Dataset to create a new column using this function to generate that column's data.We are basically defning an inline function here. 

```scala
val lookupName :Int=>String=(movieID:Int)=>{nameDict.value(movieID)}

```
We first define the name of function, lookupName. Take an integer and return the lookup of the name dictionary given that movie id
Note that nameDict is a broadCast variable not the actual map.To get a map object, we have to call .value on it to retrieve the nameDict's object. That is,
to get a content, we ned to call value on it. 

Second, we nned to wrap it with a udf

```scala
val lookupNameUDF=udf(lookupName)
```
This user defnied function can be used in a SQL setting.


2)truncate 

we set up the trunacte argument  to false toprevent it from actually truncating the length of each individual row. Otherwise,it would chop off some of length
of movie name to fit within a given row width.







