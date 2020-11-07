### 1. Objective

In this seciton,we still use the same dataset from the previous section,but are to find out the list of heros who are deemed to be
the most obscure. The word "obscure" can be interepreted in diffrent ways and narrowing it to our case only, it means anyone with the only one conneciton.


### 2. Strategy

As for the methods to achieve the specified goal,we should follow the below steps:

- Start with a copy of the MostPopularSuperheroDataset script and give it a new name in the process

- Can be largely unchanged up to point where the "connections" dataset is constructed

- Filter the connections to find rows with only one connection

- Join the results with the names dataset

- Select the names column and show it 


### 3. Code

```scala


object obscureHeroes {
  case class partners(value:String)
  case class heroNames(id:Int,name:String)
  def main(args:Array[String]){
    Logger.getLogger("org").setLevel(Level.ERROR)
    val spark=SparkSession
              .builder
              .appName("test")
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
              .as[heroNames]
    val lines=spark.read
                    .text("../Marvel-graph.txt")
                    .as[partners]
    val connections=lines
                    .withColumn("id",split(col("value")," ")(0))
                    .withColumn("counts",size(split(col("value")," "))-1)
                    .groupBy("id").agg(sum("counts").alias("connections"))
    val minValue=connections.agg(min("connections")).first().getLong(0)
    val leastPopular=connections.filter($"connections"===minValue).select("id")
    val leastPoularLists=leastPopular.join(names,Seq("id"),"left")
    leastPoularLists.show()
     
   }
   
}
  
```

### 4. Results

![image](https://user-images.githubusercontent.com/53164959/98439040-24896e80-2132-11eb-8c8f-bb9ac6b0687c.png)

