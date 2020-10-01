## Part 3 Real Examples



Objective : Compute the average number of friends by age 

```spark
object RatingsCounter {
  
  def parseLine(line:String):(Int,Int)={
   val fields=line.split(",")
   val age=fields(2).toInt
   val numFriends=fields(3).toInt
   (age,numFriends)
  }

  
  def main(args:Array[String]){
   Logger.getLogger("org").setLevel(Level.ERROR)
   val sc=new SparkContext("local[*]","friendByAge")
   val lines=sc.textFile("../fakeFriends.csv")
   val rdd=lines.map(parseLine)
   rdd.foreach(println)
   //use mapValues to convert each numFriends to  a tuple of (numFriends,1)
   //then we use reduceByKey to sum up the total numFriends and total instances for each age
   //by adding together all the numbFriedns values and 1's respectively
   //remember our objective here is to compute the average number of people for each group 
   //reduceByKey is one of "transfomration" types; when called on the dataset of key,value paired
   //returns the values for each key are aggreated!
   val totalsByAge=rdd.mapValues(x=>(x,1)).reduceByKey((x,y)=>(x._1+y._1,x._2+y._2))
   val averageByAge=totalsByAge.mapValues(x=>x._1/x._2)
   val results=averageByAge.collect()
   results.sorted.foreach(println)
  } 
}



```

