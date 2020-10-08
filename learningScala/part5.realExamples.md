### Objective:

Extrac every word in the following text file and count the number of occureance of it. 

```scala

  def main(args:Array[String]){
    Logger.getLogger("org").setLevel(Level.ERROR)
    val sc=new SparkContext("local[*]","applicaiton")
    val lines=sc.textFile("../book.txt")
    val words=lines.flatMap(x=>x.split("\\W+"))
    val lower=words.map(x=>x.toLowerCase())
    val wordCount=lower.map(x=>(x,1)).reduceByKey((x,y)=>x+y)
    //Currenlty the tupe constis of (count,word) and we need to flip to (count,word) and then sort by key
    val wordCountSorted=wordCount.map(x=>(x._2,x._1)).sortByKey()
    for(result<-wordCountSorted){
      val count=result._1
      val word=result._2
      println(s"$word:$count")
    }
```
![image](https://user-images.githubusercontent.com/53164959/94888330-b4912400-04b3-11eb-866b-0569d0129f88.png)
