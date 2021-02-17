## Aggregations


### 1. Introduction
Aggregation is the act of collecing something together and its type further divides into the following types

- _group by_ allows you to specify one or more keys as well as one or more aggregation functions to transform the value
  columns. A group-by takes data and every row can go into one grouping.
 
- _window_ acts in a similar manner but the rows input to the function are somehow related to the current row
  .That is, a window function computes a return value for every input row of a table based on a group of rows
   called a frame. Each row can fall into one or more frames. If you look at a rolling average of stock price for every five days, some of rows representing a price for a specific day may end up in different frames.  
   
   
- _grouping set_ is used to aggregate at multiple different levels. The sets are available as a primitive in SQL
   and via rollups and cubes in DataFames
   
- _cube_ allows you to specify one or more keys as well as more aggregation functions to transform the    
  value columns,which will be summarized across all combinations of columns.
  


  
### 2.Types 

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


### 2.2 Window Function 







