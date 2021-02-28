1. What is Structrued Streaming? ________________________________________________:black_nib:

Structured streaming is responsible for running your queries "incrementally"(ie update the result efficiently as new data arrives) and continuously as a new data comes in. 
This is a streaming processing framework built on the _spark SQL engine_. Therfore, strcutured streaming uses the existing 
structured API in spark (DataFrame,Datasets,SQL), implies that all the opertions we are often familar with are supported. 

The main idea behind this framework is to treat a stream of data as a table to which data is continously appended. Its job indluces chekcing for the new input data,processing it,updating some internal state, and updating its result. 


2. Core Concepts __________________________________________________________________:black_nib:

- Transformation and Actions :
  
  Structured Streaming maintains the same concept of transformation and actions that we have in the existing structured API in spark.
  
- Input Sources :
  
  Structured Streaming supports serveral input sources for readning in a streaming fashion, including kafka or files on a distributed system,
  a scocket for testing. 
  
- Sinks:

  Sinks specify the destinations for the result set of that stream. 
  
 - Output Modes:
   
   This task is mostly associated with defning how we want Spark to write data to prespecifed Sinks. The supported output modes include
   
   - Append(only add new records to the output sink)
   - Update(update changed records in place)
   - Complete(rewrite the full output)

- Tirggers:
  Triggers define when structured streaming should check for new input and update its result. By deafult, the framework look for input
  records as it has finished processing the last group of input data. This gives a lowest latency possible for new results. However, this
  behavior leads to many small output files if the sink is a set of files. Thus, spark alos supports triggers based on processing time(only 
  look for new data at a fixed interval)
  
 - Event-time Processing:
   
   Event time means processing data according to the time that it was generated rather than the when it reached your system. Since the system
   views the input data as a table, the even time is just another field in the table.
   

 - Watermarks 
 
   When structured streaming process knows that one of your columns is an event-time field, it can take some special actions such as optimizing    query execution or determing when it is safe to forget about a time window. Many of these actions can be controlled by woater marks.
   Virtaully, it determines _how late streaming systems see data in event time._ 
   System that supports event time allow setting watermarks to limit how long they remember old data. 

  


  


