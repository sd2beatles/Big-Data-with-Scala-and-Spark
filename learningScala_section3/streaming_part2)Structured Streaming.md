1. What is Structrued Streaming? ________________________________________________:black_nib:

Structured streaming is responsible for running your queries "incrementally"(ie update the result efficiently as new data arrives) and continuously as a new data comes in. 
This is a streaming processing framework built on the _spark SQL engine_. Therfore, strcutured streaming uses the existing 
structured API in spark (DataFrame,Datasets,SQL), implies that all the opertions we are often familar with are supported. 

The main idea behind this framework is to treat a stream of data as a table to which data is continously appended. Its job inclues chekcing for the new input data,processing it,updating some internal state, and updating its result. Internally, Structured Streaming figures out how to incrementalize your query and run it in a fault-tolerant manner.


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
  


  


