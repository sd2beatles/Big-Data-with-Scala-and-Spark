### Objective: 
   Among the numerous fields in the dataset, our attention is placed on the first three columns(statonID,StationType,and Temperature)
   Imagine that you were told to find the minimum temperature for each station and the temperature must be measured according to the below formula.
   
   _converted_temp=tempearature*0.1*(9.0/5.0)+32.0_

### Code:

```scala

def parseLine(line:String)={
  val fields=line.split(",")
  val stationID=fields(0)
  val stationType=fields(2)
  val temperature=fields(3).toFloat*0.1f*(9.0f/5.0f)+32.0f
  (stationID,stationType,temperature)
}

  
  
def main(args:Array[String]){
   Logger.getLogger("org").setLevel(Level.ERROR)
   val sc=new SparkContext("local[*]","test")
   val lines=sc.textFile("../1800.csv")
   val parseLines=lines.map(parseLine)
   val selectedLines=parseLines.filter(x=>x._2=="TMIN")
   val stationTemps=selectedLines.map(x=>(x._1,x._3.toFloat))
   val minTempByStation=stationTemps.reduceByKey((x,y)=>min(x,y))
   val results=minTempByStation.collect()
  
   for(result<-results.sorted){
     val station=result._1
     val temp=result._2
     val formatted=f"$temp%.2f F"
     println(s"$station min temperature: $formatted")
     
   }
}

```

### Result:

 ![image](https://user-images.githubusercontent.com/53164959/94844320-0ce70800-0459-11eb-9a7e-4608b643f0d6.png)
