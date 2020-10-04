### Introdcution of Spark SQL


### 1.Introdcution 


In the world of Scala, more and more users are now focusing on Datasets because they find it more efficient and convenient in fulfilling their tasks.
SparkSQL is a layer on the top of Spark core and enables you to think of your problem in terms of SQL command many users feel familiar with. In fact,
everything is built on the top of RDD, an original interface, which we have discovered in the previous section. Sometimes it is still 
useful to get back to the low level for the best optimization for simpler problems. 

Since the release of Spark2, a majority of work is mostly done through the SparkSQL interface in Apache Spark. 
One of the fine features of SparkSQL is to offer a very efficient and easy to use a solution for getting  the answer you want across an entire cluster.

### 2. Steps to Use Spark SQL

If you decide to use SparkSQL or Datasets, you need first to have SparkSession which is rather similar to a database session in the relational 
database. Once you have that session, you can actually get a Sparkcontextr from it and use that to issue actual queries on your datasets.
Right after having a daset object loaded up, you implement qurie functions such as show,select,filter,or group by. 


