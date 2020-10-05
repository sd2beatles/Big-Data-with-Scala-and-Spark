### 1.SQL functions

a)explode() : explode columns into rows

b)split we will split the words by

c)lower() to convert everything to lowercase

d)Extra tips(Good to Know!)
  - When you are passing a column name as a parameter into these SQL functions, the synax is **dollar sign**.
    This tip is found useful if you decide to use functions like split,filter and so on.
  
  - Note that _eaulity operators in filter_ statements are =!= or ===.
  

### 2.Things  to Consider

Using dataset with unstrcutred text data is not a great fit.Rather, it is worthy noting that Datasets are working best in the case of structured data

Since the data we will deal with is unstructured and we do not have a schema coming in,the schema that ends up getting inferred is kind of arbitrary.
We just end up with a dataframe full of row objects with a column named "value" by default for each line of text becasue there is no schema for every
row is full of text. Therefore, it makes sense to load your data as an **RDD**,then covert to a DataSet for further processing later. 



### 3. Bad Approach VS Good Approach


```scala
<Bad Approach>

case class Book(value:String)
   Logger.getLogger("org").setLevel(Level.ERROR)
   val spark=SparkSession
             .builder
             .appName("wordCounts")
             .master("local[*]")
             .getOrCreate()
   
   import spark.implicits._
   //Read each line of my book into a Dataset
   val input=spark.read.text("../book.txt").as[Book]
  //split using a regular expression that extracts words 
  val words=input.select(explode(split($"value","\\W+")).alias("word")).filter($"word"=!="")
   //normalize evertthing to lowercase
   val lowercaseWords=words.select(lower($"words").alias("word"))
  //Count up the occurrence of each word
   val wordCounts=lowercaseWords.groupBy("word").count()
   //sort by counts
   val wordCountsSorted=wordCounts.sort("counts")
   //show the results
   wordCountsSorted.show(wordCountsSorted.count.toInt)
   ```
   
   ```scala
   <Clean and Concise Way>
   
    def main(args:Array[String]){
    Logger.getLogger("org").setLevel(Level.ERROR)
    val spark=SparkSession
              .builder
              .appName("textFile")
              .master("local[*]")
              .getOrCreate()
   import spark.implicits._
   val bookRDD=spark.sparkContext.textFile("../book.txt")
   val wordRDD=bookRDD.flatMap(x=>x.split("\\W+"))
   val wordDS=wordRDD.toDS()
   
   val lowerCase=wordDS.select(lower($"value").alias("word"))
   val wordsCountDS=lowerCase.groupBy("word").count()
   val wordsSorted=wordsCountDS.sort("count")
   wordsSorted.show(wordsSorted.count.toInt)
   ```
   
   
   
