# Part 1. Introduction to Core Concepts

## 1. Introudction to Core Spark Concepts

Every spark application consists of a driver program that launches various operations on a  cluster. The driver program
contains the application's main function and defines distributed datasets on the cluster,then applying operations to them. 

#### _1) SparkContext Object_
   Driver programs access Spark through a SparkContext object, which represents a conection to a computing cluster. 
   
  Code Example)
   
   ```spark
   var sc=new SparkContext(name="local[*]",appName="test")
   ```


#### _2) RDD_
   Once you have a SparkContext Object, now you are ready to use it to build RDDs.
   In our preceding example, we call sc.textFile() to create an RDD representing the lines of text in a file
   After loading up each line of the rating data into an RDD, then we are able to apply whatever operation we want to the lines.
   
  Code Example)
  
  ```spark
  var lines=sc.textFile(path=".../replace_me")
  ```
  
#### _3) Executor_

In order to implement the operations, driver programs need to manage a number of nodes so called "executor". For our example, if we were running
the map operation on a cluster, different machines might impose a defined function to each line in different ranges of the file. 
(Since we are running the Spark locally, the program is to execute all its work on the single machie.)

![image](https://user-images.githubusercontent.com/53164959/94415161-30d7ee80-01b8-11eb-9c72-ba8c2547f175.png)


### _4) Cluster URL and Application Name_

- Cluster URL: Its major role is to insruct Spark how to connect to a cluster. In our example, local is one of its kind.
                 ('local' is a special value that runs Spark on one thread on the local machine withouth connecting to a cluster)

- Application Name: This will identify your application on the cluster manager's UI if you connect to a cluster. 




Let's run a simple code to count the number of items in our dataset,RatingsCounter.
```Spark

import org.apache.spark._
import org.apache.log4j._

object RatingsCounter {
  
  def main(args:Array[String]){
    //Set the log level to print errors
    Logger.getLogger("org").setLevel(Level.ERROR)
    
    //Create a SparkContext to access Spark
    val sc=new SparkContext("local[*]","RatingCounter")
    
    //Load each line of words into an RDD
    val lines=sc.textFile("../u.data")
    
    //Convert each line to a string,split it out by tabs,and extract the third field.
    //Note that the columns consist of userID,movieID,rating,timestamp
    var ratings=lines.map(x=>x.split("\t")(2))
    
    //Count up many times each value(rating) occurs
    var results=ratings.countByValue()
    
    //Sort the resulting map of (rating,counts) tuples
    val sortedValues=results.toSeq.sortBy(_._1)
    
    //Print out each result on its own line
    sortedValues.foreach(println)
  }
  
}
```


   
