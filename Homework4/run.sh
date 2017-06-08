#!/usr/bin/env bash
cd records/
python ./records.py
hdfs dfs -mkdir /homework4
hdfs dfs -mkdir /homework4/data
hdfs dfs -put ./records.txt /homework4/data
cd ..
hadoop jar ${HADOOP_HOME}/share/hadoop/tools/lib/hadoop-streaming-2.6.5.jar  \
-files ./src/mapper.py,./src/reducer.py \
-mapper './src/mapper.py' \
-reducer './src/reducer.py' \
-input /homework4/data/records.txt \
-output /homework4/output \
-cmdenv "o=$((500000+RANDOM%500000))" \
-cmdenv "l=$((50+RANDOM%50))"
echo -e '\nResults:'
hdfs dfs -cat /homework4/output/part-00000
echo -e '\n'
