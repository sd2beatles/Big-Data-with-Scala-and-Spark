# 1. Introduction. 

In this section, we plan tp model superheros as network of superheros by looking at other super heros each superhero either has teamed up with or appeared with in the comic book.
For example,if super man appears in a single comic book with batman,then we can say there is a connection between them. 
Frankly,there are a lot of con-currence of heros in comic books,and this gives a chance to construct an interseting social network.


# 2. Data Format
![image](https://user-images.githubusercontent.com/53164959/96327129-aaedeb80-1071-11eb-9054-88cf8fe9cbff.png)

We will be given two data,marvel-graph.txt and marvel-names.txt,respectively. The former dataset consists of multiple lines with the first
number appearing in each line is the chracter we are talking about and followed by a list of all the character IDs that the character had
appeared with in other comic books. 

The other dataset is comprised of herodIDs followed by a quotation mark enclosed name that coresponds to that hero ID.


# 3. Steps to achieve the goal

- Split off hero ID from begining of line

- Count how many space-spearated numbers are in the line

- Group by hero ID's to add up connections split into multiple lines

- Sort by total connections

- Filter name lookup dataset by the most popular hero ID to look up the name 


# 4. code
### 1) Dataset Approach

```scala
import org.apache.spark.sql.SparkSession
import org.apache.log4j._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{IntegerType,StringType,StructType}



object PopularMovie {
 case class SuperHeroName(id:Int,name:String)
 case class SuperHero(value:String)
  
  def main(args:Array[String]){
    Logger.getLogger("org").setLevel(Level.ERROR)
    val spark=SparkSession
              .builder
              .appName("Most PopularSuperHero")
              .master("local[*]")
              .getOrCreate()
    val schema=new StructType()
               .add("id",IntegerType,nullable=true)
               .add("name",StringType,nullable=true)
    
   import spark.implicits._
   val names=spark.read
               .schema(schema)
               .option("sep"," ")
               .csv("../Marvel-names.txt")
               .as[SuperHeroName]
   
    val lines=spark.read
             .text("../Marvel-graph.txt")
             .as[SuperHero]
    
    val connections=lines
                    .withColumn(colName="id",split(col("value")," ")(0))
                    .withColumn("connection",size(split(col("value")," "))-1)
                    .groupBy("id").agg(sum("connection").alias("connection"))
    
    val mostPopular=connections
                    .sort($"connection".desc)
                    .first()
    
     val mostPopularName=names.filter($"id"===mostPopular(0))
                              .select("name")
                              .first()
     println(s"${mostPopularName(0)} is the most popular superhero with ${mostPopular(1)} co-appearance")
   
}
}
```

### 2) RDD approach

```scala



import org.apache.spark.sql.SparkSession
import org.apache.log4j._
import org.apache.spark.SparkContext
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{IntegerType,StringType,StructType}

//stationID,itemID,value

object RatingsCounter {
  

  def countOccurences(line:String):(Int,Int)={
    val fields=line.split("\\s+")
    (fields(0).toInt,fields.length-1)
  }
 
  def parseNames(line:String):Option[(Int,String)]={
    val fields=line.split('\"')
    if(fields.length>1){
    Some(fields(0).trim().toInt,fields(1))
    }else None
  }


  case class Heros(id:String)
  case class HeroNames(id:Int,name:String)
  
  def main(args:Array[String]){
    Logger.getLogger("org").setLevel(Level.ERROR)
    val sc=new SparkContext("local[*]","mostPopularMovies")
    val names=sc.textFile("../marvel-names.txt")
    val namesRDD=names.flatMap(parseNames)
    
    val lines=sc.textFile("../marvel-graph.txt")
    val pairings=lines.map(countOccurences)
    val totalFriendsByCharacter=pairings.reduceByKey((x,y)=>x+y)
    val flipped=totalFriendsByCharacter.map(x=>(x._2,x._1))
    //find the maxium number based on the first element
    val mostPopular=flipped.max()
    val mostPopularName=namesRDD.lookup(mostPopular._2).head
    val mostPopularId=mostPopular._2
    println(s"$mostPopularName ($mostPopularId)")
  }
  
   }





```

# 5.Output


![image](https://user-images.githubusercontent.com/53164959/96327800-b8a66f80-1077-11eb-8107-a4f170124a1a.png)



