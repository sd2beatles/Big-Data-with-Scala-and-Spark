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


  
object levelSeparation {
    
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
   
  /*Expands a BFSNode into this node and its children*/
  def bfsMap(node:BFSNode):Array[BFSNode]={
    val characterID=node._1
    val data=node._2
    val connections:Array[Int]=data._1
    val distance:Int=data._2
    var color:String=data._3
    
    //This is called from flatMap,so we return an array
    //of potentially many BFSNodes to add to our new RDD
    var results:ArrayBuffer[BFSNode]=ArrayBuffer()
    if(color=="Gray"){
      for(connection<-connections){
        val newCharacterID=connection
        val newDistance=distance+1
        val newColor="Gray"
        
     //Have we stumbled across the character we are looking for?
     //If so increment our accumulator so the driver script knows
     if(targetCharacterID==connection){
       if(hitCounter.isDefined){
         hitCounter.get.add(1)
       }
     }
        
    val newEntry:BFSNode=(newCharacterID,(Array(),newDistance,newColor))
    results+=newEntry
    }
    //Color this node as black,indicating it has been processed alaready
    color="BLACK"
    }
    //Add the original node back in, so its connections can get merged with
    //the gray node in the reducer
    val thisEntry:BFSNode=(characterID,(connections,distance,color))
    results+=thisEntry
    results.toArray
    }
   
  def createStartingRDD(sc:SparkContext):RDD[BFSNode]={
    val inputFile=sc.textFile("../marvel-graph.txt")
    inputFile.map(convertToBFS)
  }
   
  def bfsReduce(data1:BFSData,data2:BFSData)={
    //Extract data that we are combining
    val edge1:Array[Int]=data1._1
    val edge2:Array[Int]=data2._1
    val distance1:Int=data1._2
    val distance2:Int=data2._2
    val color1:String=data1._3
    val color2:String=data2._3
    
    //Default node values
    var distance:Int=999
    var color:String="White"
    var edges:ArrayBuffer[Int]=ArrayBuffer()
    
    if(edge1.length>0){
      //add elements of edge1 into the edge Array
      edges++=edge1
      }
    if(edge2.length>0){
      edges++=edge2
    }
    if(distance1<distance){
      distance=distance1
    }
    if(distance2<distance){
      distance=distance2
    }
    
    //Preserve Darker Section
    if((color1=="White")&&(color2=="Black"||color2=="Gray")){
      color=color2
    }
    if((color1=="Gray"&&color2=="Black")){
      color=color2
    }
    if((color2=="White")&&(color1=="Black"||color1=="Gray")){
      color=color1
    }
    if((color2=="Gray"&&color1=="Black")){
      color=color1
    }
    
    if(color1=="Gray"&&color2=="Gray"){
      color=color1
    }
    (edges.toArray,distance,color)
    
  }
   
   
  
   def main(args:Array[String]){
     Logger.getLogger("org").setLevel(Level.ERROR)
     //Create a sparkContext
     val sc=new SparkContext("local[*]","DegreeOfSeparation")
     //Our accumulator used to signal when we find the target character in our BFS traversal
     hitCounter=Some(sc.longAccumulator(name="Hit Counter"))
   
    //Create an initial condition of our graph
     var iterationRDD=createStartingRDD(sc)
     //pick up the arbitrary upper bound,says 10 in our case.
     for(iteration<-1 to 10){
       println("Running BFS Iteration#"+iteration)
       //Create new verticies as needed to darken or reduce distances in the reduce range
       //If we encouter the node we are looking for as a Gray Node,increment our accumulator to signal
       //that we are done. 
       val mapped=iterationRDD.flatMap(bfsMap)
       //Note that mapped.count() action here forces the RDD to be evaluated and that 
       //is the only reason our accumulator is actually updated
       println("Processing"+mapped.count()+"values.")
       
       if(hitCounter.isDefined){
        val hitCount=hitCounter.get.value
        if(hitCount>0){
          println("Hit the target Character!From"+hitCount+"different directions(s)")
          return 
        }
       }
       
       //Reducer combines data for each character ID,preserving the darkest color and shortest path
       iterationRDD=mapped.reduceByKey(bfsReduce)
       
     }
   }
}

```
 
 ![image](https://user-images.githubusercontent.com/53164959/98558733-1d589100-22e9-11eb-923e-90ef7af53d33.png)

