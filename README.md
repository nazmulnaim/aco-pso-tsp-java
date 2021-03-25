## Intoduction:

Here the problem is the well-known Traveling Salesman Problem which has been tried to solve with two population-based stochastic algorithms.

This framework has support to execute the TSP problem with a general implementation of Ant Colony Optimization and Particle Swarm Optimization.

The framework is written in java and can be run with JDK1.8

## Datafile

In this project, 2 data files are included from the following public source repository
> a. att48.tsp
> b. bays29.tsp

https://people.sc.fsu.edu/~jburkardt/datasets/tsp/tsp.html

More can be managed from the same website. Worth remembering currently, the program only supports the `.tsp` format. To define metadata about the data file and select a different file to process, please check the configuration section.


## Prerequisite:

You should have the following tools installed in your system to run the application

- jdk-1.8.0

- maven 3.6.3

## Build and run:

**Build the application with Maven**

It's a maven based application, To build the application following command need to be run from the command line.
~~~
mvn package
~~~

**Run the application**

~~~
java -jar target/aco-pso-tsp-java-1.0.0.jar
~~~

**Parameter**

For ACO one can find following parameter as default -
~~~
Use the parameter '-p' for custom settings.
Otherwise the default values will be:      
Ants per epoch:           100
Epochs:                   100
Evaporation Rate:         0.1
Alpha (pheromone impact): 1
Beta (distance impact):   5
~~~

A few parameters can be changed before performing execution by passing values after -p

For PSO here are the default values

~~~
Use the parameter '-p' for custom settings.
Otherwise the default values will be:
Swarm Number:           100
Iteration Time:         100
Weight Factor:          0.5
Starting Point:         1
~~~

One thing to be noted, if it is necessary to override the default value the runtime algorithms need to be predefined, either ACO or PSO

**Configuration**

There are a few configurations which can be found in `src\main\resources\config.properties` file

~~~
optimization.method = aco, pso
data.set.name = att48.tsp
starting.line = 0
number.of.cities = 48
~~~

> 1. `optimization.method` to choose which optimization algorithm should be run
> 2. `data.set.name` to choose datafile from `src\main\resources\` directory
> 3. `starting.line` starting point of the city location in ta TSP file
> 4. `number.of.cities` is to define the number of cities to be selected for execution

## Credit:
A few part of the program is inspired by following repositories -
> https://github.com/LazoCoder/Ant-Colony-Optimization-for-the-Traveling-Salesman-Problem
