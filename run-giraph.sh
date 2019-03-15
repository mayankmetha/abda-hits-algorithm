#!/bin/bash

if [ $# -ne 4 ]; then
    echo "Usage: <JAR file> <Class> <HDFS input> <HDFS output>"
    exit 1
fi

cp $1 $HADOOP_HOME/share/hadoop/mapreduce/lib

$HADOOP_HOME/bin/hadoop jar $1 \
    org.apache.giraph.GiraphRunner \
    $2 \
    -vif com.pes.giraph.SimpleHitsInputFormat\
    -vip $3 \
    -vof org.apache.giraph.io.formats.IdWithValueTextOutputFormat \
    -op $4 \
    --yarnjars $1 \
    -w 1 \
    -mc com.pes.giraph.MasterComputer \
    -ca mapred.job.tracker=`hostname`:8032 \
    # -ca giraph.SplitMasterWorker=false \
    # -ca giraph.pure.yarn.job=true \
