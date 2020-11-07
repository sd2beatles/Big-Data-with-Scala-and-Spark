## 1. Introduction

In this section, we are curious about figuring out "Superhero Degress of Separtion". 
Let's say thjat for the sake of argument, Spiderman appears in the same comic book as the incredible hulk and the incredible hulk
in turn appeared in the same comic book as Thor. In that case, we would say the Spiderman and Thor are two degrees of separation. That is,
there are two steps away from spiderman to Thor. 

## 2. Things to Know

The way to find out the connections sounds to be concerned with a computer science algorithm called "Breadth-first search" that gives us some clues.
The way we are seeking is much more like answering a question; given a superhero in the graph of connections, how many steps away the given hero is from 
any other superheros?


![image](https://user-images.githubusercontent.com/53164959/98439844-f9a21900-2137-11eb-93c5-2f54162a5060.png)

#### What is the breadth-first search?

First, set up all the numbers in the nodes to infinity, which indicates none of the nodes are assigned the level of separation. 
Pick one of the nodes and find the nearest neighbors and give 0 to the starting node.  Find the nearest neighbors who are directly 
connected to the chosen node and increment the counts.  Now, the starting point is going to be colored black, which means we are not 
going to revisit it in the future. We find the connections of the gray(s) and repeat the previous procedure. 


## 3.Strategy
