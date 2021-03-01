# Instruction

Create a directory,name it at your disposal,and copy the logfile in it. Ultimately, your aim is to see if your streaming applicaton picks it up and process it. In the application, you need to include SQL operations to parse out the data from those log lines using regular
expressions and group thigs together over some window if you want to. Finally, stream out the ouput to your main console. 

```scala
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col,regexp_extract}




object test {

    def main(args: Array[String]): Unit ={


      val path:String="put directory where log-data is stored"
      Logger.getLogger("org").setLevel(Level.ERROR)
      val spark=SparkSession
        .builder
        .appName("StructureStreaming")
        .master("local[*]")
        .config("spark.sql.shuffle.partitions",3)
        .getOrCreate()

      import spark.implicits._
      val accessLines=spark.readStream.text(path)

      //Regular expressions to extrac pieces of apache acess log lines
      val hostExp="(^\\S+\\.[\\S+\\.]+\\S+)\\s"
      val timeStamp="\\[(\\d{2}/\\w{3}/\\d{4}:\\d{2}:\\d{2}:\\d{2}\\s\\S+)]"
      val generalExp="\"(\\S+)\\s(\\S+)\\s*(\\S*)\""
      val statusExp="\\s*(\\d{3})\\s"

      //Apply the regular expression to current structure from the unstructured text
      val logDF=accessLines.select(
        regexp_extract(col("value"),hostExp,groupIdx=1).alias("host"),
        regexp_extract(col("value"),timeStamp,groupIdx=1).alias("timestamp"),
        regexp_extract(col("value"),generalExp,groupIdx=1).alias("method"),
        regexp_extract(col("value"),generalExp,groupIdx=2).alias("endpoint"),
        regexp_extract(col("value"),generalExp,groupIdx=3).alias("protocol"),
        regexp_extract(col("value"),statusExp,groupIdx=1).cast("Integer").alias("status")
      )
      //Keep a running count of status codes
      val statusCountDF=logDF.groupBy("status").count()

      //Display the stream to the console
      val query=statusCountDF.writeStream.outputMode("complete").format("console").queryName("counts").start()

      //Wait until we terminate the scripts
      query.awaitTermination()

      //stop the session
      spark.stop()


    }
}
```

![image](https://user-images.githubusercontent.com/53164959/109450489-5716f880-7a8e-11eb-9c64-1e2055f1a859.png)
