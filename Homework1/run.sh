hdfs dfs -mkdir /homework1
hdfs dfs -mkdir /homework1/data
hdfs dfs -put ./data/*.txt /homework1/data
hadoop jar ${HADOOP_HOME}/share/hadoop/tools/lib/hadoop-streaming-2.6.5.jar  \
-files ./src/mapper.py,./src/reducer.py \
-mapper ./src/mapper.py \
-reducer ./src/reducer.py \
-input /homework1/data/*.txt \
-output /homework1/output
echo "Top 20 words:"
hdfs dfs -cat /homework1/output/part-00000 | sort -nk 2 | tail -20 | tac