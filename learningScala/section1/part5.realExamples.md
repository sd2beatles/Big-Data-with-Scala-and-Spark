### Objective:

Extrac every word in the following text file and count the number of occureance of it. 

```scala
def main(args:Array[String]){
    
    Logger.getLogger("org").setLevel(Level.ERROR)
    val sc=new SparkContext("local[*]","application")
    //Load each line of my book into RDD
    val lines=sc.textFile("../book.txt")
    //Split using a regular expression 
    val words=lines.flatMap(x=>x.split("\\W+"))
    //Normalize everything to lowercase
    val lowerWords=words.map(x=>x.toLowerCase())
    //Count of the occurence of each word
    val wordCounts=lowerWords.countByValue()
    //Print the result
    wordCounts.foreach(println)
   }
```

![image](https://user-images.githubusercontent.com/53164959/94887518-76930080-04b1-11eb-8ac2-3915f9795c95.png)
