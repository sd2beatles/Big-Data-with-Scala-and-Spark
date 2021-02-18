## Aggregations  


### 1. Introduction  _______________________________________:black_nib:

Aggregation is the act of collecing something together and its type further divides into the following types

- _group by_ allows you to specify one or more keys as well as one or more aggregation functions to transform the value
  columns. A group-by takes data and every row can go into one grouping.
 
- _window_ acts in a similar manner but the rows input to the function are somehow related to the current row
  .That is, a window function computes a return value for every input row of a table based on a group of rows
   called a frame. Each row can fall into one or more frames. If you look at a rolling average of stock price for every five days, some of rows representing a price for a specific day may end up in different frames.  
   
- Grouping Sets 
 While group-by function relates to an aggregation on a set of columns with those values in those columns
,groupign sets is aggregation across multiple groups. Its types further divide into rollup and cube.  
   - _Rollup_ is simply defined as a multidimensional aggregation that performs  a variety of group-by style calculations for us.
   - _cube_  is to perform aggregation functions across all combinations of columns 
   
  


  
### 2.Types  ______________________________________________________:black_nib:

#### 2.1 Aggregation Functions

Think of df as a dataframe object and in the following sections we will apply a variety of aggreation functions 
to retrieve the result we may use in data analysis.

#### 2.1.1 count() 

selectedColName is the one we specify to count. 

```scala
df.select(selectedColName).count()

```

#### 2.1.2 distinct().count()

```scala
df.select(selectedColName).distinct().count()
```

#### 2.1.3 first and last
  
To list the two results in the same row simultaneously, we need to deploy another trick to make it work out.  The extra function to execute is "agg".
Additionally, if you decide to name each result, alias function should be appended right after the named function. 

```scala
df.agg(last(col(selectedColName).alias("last"),first(col(selectedColName).alias("first")).show()
```

#### 2.1.4 max and min

 ```scala
df.agg(max(col(selectedColName).alias("maxValue"),min(col(selectedColName).alias("minValue")).show()
```

#### 2.1.5 sum
There are two occasions where we normally use the sum function 

- Total sum of one specified column 

```scala
df.groupBy().sum(specifiedColName).show()
```

- Total sum of qautitative Column according to every member of qualitative one

```scala
df.groupBy(qualitativeCol).sum(quantitativeCol).show()
```

#### 2.1.6 Aggregating to _Complex Types_

Not only are aggrating functions performed on numberical values but also
on complex types. In our example, we can collect a list of values present in a given
column or the only unique values by collecting to a set. 

```scala
df.agg(collect_set(selectedColName),collect_list("Country")).show()
```

#### 2.1.7 Grouping with Maps

If you want your trasnfomrations as a series of Maps for which they key is column and the value is the aggreation
function that you would like to perform.

```scala
df.groupBy(qualitativeColumn).agg(quantitativeColumn->aggfunction,quantitativeColumn2->aggfunction2).show()
```
For example, if you decide to compute average and standard deviation of "quatative" column according to "InvoiceNo",
the following code is the one we seek after. 

```scala
df.groupBy("InvoiceNo").agg("Quantity"->"avg", "Quantity"->"stddev_pop").show()
```


### 2.2  Window Function - case study


![image](https://user-images.githubusercontent.com/53164959/108184939-185c7680-714f-11eb-9942-d1ab7511194e.png)

We first create an extra column for which our invoice data is coverted into a column that contains data information. 

:heavy_exclamation_mark:
As for spark 3.0 version, in parsing date using to_date(), you will be more likely to see the Legacy related exception. Then You need to add an extra line to cope with this issue. 

```scala
//spark is the SparkSession obeject
 spark.sql("set spark.sql.legacy.timeParserPolicy=LEGACY")
 val df_date = df.withColumn("date", to_date(col("InvoiceDate"), "MM/d/yyyy H:mm"))
 
 ```
 
 Now,we need some codes to break up our group and the ordering within a given partition is arragnged in a decremental manner.Finally, the frame specification states which rows will be included in the frame based on
 the reference to the current input row. 
 
 ```scala
 val windowSpec=Window
               .partitionBy("CustomerID","date")
               .orderBy(col("quantity").desc)
               .rowsBetween(Window.unboundedPreceding,Window.currentRow)
 ```
 
 Now, we use the aggreagation functions by passing column name. In addition,we indicate the window specification that defines to which frames of data this function will apply 
 
 
 ```scala
 val data=df_date.withColumn("maxAmount",max(col("Quantity")).over(windowSpec))
 df.show(3)
 ```
 
 
 
### 2.3 Roll up and Cube  

:heavy_exclamation_mark:

Groupign sets such as Roll-up and Cube need to requires an extra attention. Beacuse they are highly affected
by null values for aggregating levels, without filteringout nulls, you would get incorrect results. 

### 2.3.1 Rollup

As previously mentioned, the "rollup" function is a special function of "group-by" in that they will generate an extra column with the total sum of values across the combinations of columns. 

Suppose we have three coulumns,date,country,and quantity. Your main interest is to compute the grand sum of quantity over all dates, the grand sum for each date and the subtotal for each country on each date

```scala
//make sure that you get rid of all null values before proceeding to the next stage
val ddfNotNull=df.drop()
val rollUp=dfNotNull
    .rollup("date", "Country")
    .agg(sum("Quantity"))
    .withColumnRenamed("sum(Quantity)","totalQuantity")
    .select("date","Country","totalQuantity")
df.rollUp.show()
```


### 2.3.2 Cube

Let's conitnue with the previous case. This time, not only will a cube go by date over the entire time period,but also the country. So here are the resulting outcomes we expect from cube function

- Total sum across all dates and countries
- Total sum for each date across all coutries
- The toal for each country across all dates 
- The toal for each country on each date

```scala

  val dfNotNull=dfDate.drop()
  val cube=dfNotNull
    .cube("date","Country")
    .agg(sum("Quantity"))
    .withColumnRenamed("sum(Quantity)","totalQuantity")
    .select("date","Country","totalQuantity")
    .orderBy(col("date").desc)
```

### 3.Pivot

Pivot makes it possible for you to convert a row into a column. In ourcse current data, we have a Country
column.With a pivot, we can aggreagate accoring to some functions for each of those given countries. 

```scala

val pivoted=dfDatte.groupBy("date").pivot("Country").sum()

```

![image](https://user-images.githubusercontent.com/53164959/108301407-4a201c80-71e5-11eb-949f-dde7b7b6c905.png)



### 4. User-Defined Aggregation Functions

UDFAs are a way for users to define thier own aggregation functions based on custom formulae or business rules. To create a UDAF, we must inherit from the UserDefinedAggregateFunctions base calss and implements the
fllowing methods;


![image](https://user-images.githubusercontent.com/53164959/108301700-e518f680-71e5-11eb-8f19-bf35e66e6f9f.png)

:orange_book: Source from _Spark:The Definitive Guide Book_








