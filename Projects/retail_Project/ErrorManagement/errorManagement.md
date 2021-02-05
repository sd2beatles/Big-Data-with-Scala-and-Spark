### 1. Failure to parse date in Spark 3.0

```scala
a different result due to the upgrading of Spark 3.0: Fail to parse '12/1/2010 8:26' in the new parser. You can set spark.sql.legacy.timeParserPolicy to LEGACY to restore the behavior before Spark 3.0, or set 
 to CORRECTED and treat it as an invalid datetime string.
```

If you happend to see a similar message above, add an extra code to set it to legacy, which make it possilbe to process data.

```scala
spark.sql("set spark.sql.legacy.timeParserPolicy=LEGACY")
```
Note that spark in our code here refers SparkSession.

### 2. 
