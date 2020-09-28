# Part 2. Programming with RDDs

## 1.Transformation 
Transformation: Create a slight modified RDD from the previosu one by implemeting a funcito such as filter,map and so on

### 1.1 filter

Note that filter operation does not mutate teh exsiting RDD. Instead,it returns a pointer to the entirely new RDD. 
The existing RDD is left "intact" for later use.

```spark

val inputRDD=sc.textFile("log.txt")
val errorRDD=inputRDD.filter(x=>x.contains("error"))
```
### 1.2 union

Instead of taking only one RDD, union operates on two RDDs instead of one.Trasfomration can actually happen on any number of input RDDs

![image](https://user-images.githubusercontent.com/53164959/94438178-b61fcb00-01d9-11eb-8218-1e5fd14c87ad.png)

```spark

val errorRDD=inputRDD.filter(x=>x.contains("error"))
val warningRDD=inputRDD.filter(x=>x.contains("warning"))
val unionRDD=errorRDD.union(warningRDD)
```

### 1.3 Map()
map() takes in a function and applies to each element in the RDD with the reult of the function being the new value of each element in the resulting RDD


```spark
  Logger.getLogger("org").setLevel(Level.ERROR)
  val sc=new SparkContext("local[*]","test")
  val input=sc.parallelize(List(1,2,3,4))
  val results=input.map(x=>x*x)
  println(results.collect().mkString(","))
  
```

### 1.4 flatmap()

flatmap() is found especially useful if we wish to produce multiple outcomes for a given element.

- common feature with map()
map and flatmap() are all called individually for each element

- distinct features
1) Instead of returing a single element,it returns a generator with returned values.
2) Rathern than producing a RDD of iterator, we get back an RDD that consists of the elements from all the iterators.
  
```spark
 def main(args:Array[String]){
   Logger.getLogger("org").setLevel(Level.ERROR)
   val sc=new SparkContext("local[*]","test")
   val input=sc.parallelize(List("hello world","hi"))
   val lines=input.flatMap(x=>x.split(" "))
   println(lines.collect().mkString(","))
   }

```


![image](https://user-images.githubusercontent.com/53164959/94451786-c8a20080-01e9-11eb-998f-e3c1c78fae49.png)


### 1.5 distinct()

distinct() returns an RDD consisting of unique values 

### 1.6 union(other)

gives back an RDD consisting of the data from both sources

```spark
val sc=new SparkContext("local[*]","test")
   val list1=sc.parallelize(List(1,2,3,4,5,6))
   val list2=sc.parallelize(List(3,4,5,6,7))
   val result=list1.union(list2).distinct()
   println(result.collect().mkString(","))
```



### 1.7 intersect(other)

returns only elements in both RDDs,removing all duplicates while running.


### 1.8 substract(other)
take any duplicated elements from the first RDD. 

```spark

   val sc=new SparkContext("local[*]","test")
   val list1=sc.parallelize(List(1,2,3,4,5,6))
   val list2=sc.parallelize(List(3,4,5,6,7))
   val result=list1.subtract(list2)
   println(result.collect().mkString(","))
```

### 1.9 cartesian(other)

cartesian transformation returns all possible pairs of (a,b) where a is in the source RDD and b is in the other RDD
```spark
val sc=new SparkContext("local[*]","test")
val rdd = sc.parallelize(List('a','b','c','d'))
val combinations = rdd.cartesian(rdd).collect()
combinations.foreach(println)

```


## 2. Action

Actions are the operations that return a final value to the driver or write data to an external storage system.


### 2.1 count

count() returns the count as a number

```spark
println("Input Bad"+errorRDD.count()+"concering lines")
```


### 2.2 take

take() returns a number of elements from RDD. We often specify a number in the bracekt to indicate how many elements we want to print.

```spark
println("Input Warning"+warningRDD.count()+"conerning lines")
println("Here are 10 examples")
warningRDD.take(10).foreach(println)
```


### 2.3 collect

RDD also has a special feature fucniton,collect() to retrieve the entire RDD. But pay an extra attenion before using it. Your data set must fit in memory
on single machine to use collect() on it, so it is not recommendable to use a collect() on lage dataset.




