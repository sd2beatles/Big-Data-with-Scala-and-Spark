### 1. Objective

Reimplement the weather data acitivity,which we have already gone trhough by using RDD in the previous lesson. In this section,
we are not longer to infer it from a CSV file,we are going to tell it explicitly what the shcema is. (**Really important concept here!")


### 2. Things to Remember

Just imagine a situation where there is no header in the file you import and you want to decide a specific label for each column. In this case,
you need to make a use of funciton like new StructType() in order to give a specific name for header instead of infering it.


### 3. Code 

```scala
import org.apache.spark.sql.types.{FloatType,DoubleType,IntegerType,StringType,StructType}

object RatingsCounter {
  case class Temperature(stationID:String,date:Int,measture_type:String,tempeature:Float)
  def main(args:Array[String]){
   Logger.getLogger("org").setLevel(Level.ERROR)
   val spark=SparkSession
          .builder
          .appName("practice")
          .master("local[*]")
          .getOrCreate()
   
  val temperature=new StructType()
                  .add("stationID",StringType,nullable=true)
                  .add("date",IntegerType,nullable=true)
                  .add("measure_type",StringType,nullable=true)
                  .add("temperature",FloatType,nullable=true)
  import spark.implicits._
  val ds=spark.read  
         .schema(temperature)
         .csv("../1800.csv")
         .as[Temperature]
   
  val minTemps=ds.filter($"meature_type"==="TMIN")
  val stationTemps=minTemps.select("stationID","temperature")
  val minTempByStation=stationTemps.groupBy("stationID").min("temperature")
  val minTempByStationF=minTempByStation.withColumn("temperature",round($"min(temperature)"*0.1f*(9.0f/5,0f)+32.0f,2))
                        .select("stationID","temperature")
  val results=minTempByStation.collect()
  for(result<-results){
    val station=result(0)
    val temp=result(1).asInstanceOf[Float]
    val formatted=f"$temp%.2f F"
    println(s"$station minimum temperatere: $formatted")
  }
 
```
