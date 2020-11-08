## 1. Introduction

In this section, we are curious about figuring out "Superhero Degress of Separtion". 
Let's say thjat for the sake of argument, Spiderman appears in the same comic book as the incredible hulk and the incredible hulk
in turn appeared in the same comic book as Thor. In that case, we would say the Spiderman and Thor are two degrees of separation. That is,
there are two steps away from spiderman to Thor. 

## 2. Things to Know

The way to find out the connections sounds to be concerned with a computer science algorithm called "Breadth-first search" that gives us some clues.
The way we are seeking is much more like answering a question; given a superhero in the graph of connections, how many steps away the given hero is from 
any other superheros?


![image](https://user-images.githubusercontent.com/53164959/98439844-f9a21900-2137-11eb-93c5-2f54162a5060.png)

#### What is the breadth-first search?

First, set up all the numbers in the nodes to infinity, which indicates none of the nodes are assigned the level of separation. 
Pick one of the nodes and find the nearest neighbors and give 0 to the starting node.  Find the nearest neighbors who are directly 
connected to the chosen node and increment the counts.  Now, the starting point is going to be colored black, which means we are not 
going to revisit it in the future. We find the connections of the gray(s) and repeat the previous procedure. 


## 3.Strategy











## 4. Tips to brush up Scala Skills

 - Option[type] : 
  
  Option[T] is a container for one element of a given type or None object,which represents a missing value. For instnace, the get Method
  of Scala produces some(value) if the value correspoding to a given key has been found or returns None if the key is not defined in the Map.
  Conveniently,this option can prevent us from making a mistake of retrieving an element with a wrong _data type_ for a corresponding key.  
  
 ```scala
 val list=Map("one"->1,"two"->2)
 val result:Option[Int]=list.get("one") //No error occurs since the value we acquire matches to the type we sepcify
 val result2:Option[Long]=list.get("one") // Error arises when none of the elements in the Map is compatible to the type(Integer)
 val result3:Option[Int]=list.get("three")//The None is assiged to the variable,result3 where no value is found for the corresponding key. 
 
```

- type 

type alias are often used to keep the rest of the code simple. This helps to aovid defning many custom tpyes that are defined on other types.

```scala
type Customers=(String,Int,String,Boolean)=>String
```
we can use the alies in our method as follows

```scala
def doSomeThing(f:Customers)
```

which will be interpreted by the complier as 
```scala
def doSomething(f:(String,Int,String,Boolean)=>String)
```



### 6. Code

```scala

import org.apache.spark._
import org.apache.spark.rdd._
import org.apache.spark.util.LongAccumulator
import org.apache.log4j._
import scala.collection.mutable.ArrayBuffer

object RatingsCounter {
    
   val startCharacterID:Int=5306
   val targetCharacterID:Int=14
   
   var hitCounter:Option[LongAccumulator]=None
   
   //BFSData contains an array of hero ID connections,the distance,and color
   type BFSData=(Array[Int],Int,String)
   //A BFSNode has a heroID and the BFSData associated with it
   type BFSNode=(Int,BFSData)
   
   /* Converts a line of raw input into a BFSNode*/
   def convertToBFS(line:String):BFSNode={
     //Split up the line into fields
     val fields=line.split("\\s+")
     //Extract this hero ID from  the first field
     val heroID=fields(0).toInt
     //Extracting subsequent hero ID's into the connection array
     var connections:ArrayBuffer[Int]=ArrayBuffer()
     for(connection<-1 until (fields.length-1)){
         connections+=fields(connection).toInt
     }
     
     //Default distance and color is 1999 and white,respectively
     var color:String="White"
     var distance:Int=9999
     
     if (heroID==startCharacterID){
          color="Gray"
          distance=0
     }
     
     (heroID,(connections.toArray,distance,color))
     
   }
   







```
 
