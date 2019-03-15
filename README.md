# Implementation of Hits Algorithm using Giraph

## Setting up Giraph
Giraph runs on top of Hadoop. Download the binaries for [Giraph](http://mirrors.estointernet.in/apache/giraph/giraph-1.2.0/) (make sure to get the **bin-hadoop2** version). <br>
Configure Giraph, and run a few sample programs, as shown [here](http://giraph.apache.org/quick_start.html). <br>
<br>
Add the following environment variables to your bashrc file:
 * GIRAPH_HOME
 * HADOOP_HOME
 * HADOOP_CONF_DIR
If needed, add the `bin` folder of Giraph to `PATH` <br>

### Common errors
All the giraph libraries need to be copied to the Hadoop directory in order to get the examples to work. Run this command to copy the libraries:
```bash
cp $GIRAPH_HOME/*.jar $GIRAPH_HOME/lib/*.jar $HADOOP_HOME/share/hadoop/yarn/lib
```

In addition, the jar that you run from needs to be copied to the location `$HADOOP_HOME/share/hadoop/mapreduce/lib`.

## Building the code
Build the code and the target jar with the following command:
```bash
mvn clean install assembly:single
```
This produces the output jar named `HitsAlgorithm-1.0-SNAPSHOT-jar-with-dependencies.jar` in the `target` folder, which is to be used to run the Giraph application.

## Running the application
Ensure Hadoop cluster is up and running. It is required for Giraph to run <br>
Use the script `run-giraph.sh` to run the Giraph Application. The command requires the following arguments:
 1. Path to the jar file created through the build
 2. The fully qualified class name
 3. Path to input file (on HDFS)
 4. Path to output file (on HDFS)
<br>

For example: <br>
```bash
./run-giraph.sh target/HitsAlgorithm-1.0-SNAPSHOT-jar-with-dependencies.jar \
    com.pes.giraph.App \
    /usr/input/input_small.txt \
    /usr/output/hits_small  
```
### Number of supersteps
Default max number of supersteps has been set to 50. Modify this as needed in the `run-giraph.sh` script by setting the value into `max.num.steps`.

## Getting the output
The output can be read by looking at the output folder specified for the run. Sample output is shown below (ran agains the `small.txt` dataset):
```
A       (Hub,Auth) = (1.0000,0.0000)
B       (Hub,Auth) = (0.8422,0.4564)
C       (Hub,Auth) = (0.0000,0.5801)
D       (Hub,Auth) = (0.3431,0.5801)
```
