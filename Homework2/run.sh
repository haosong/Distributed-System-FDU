cd records
javac GenerateRecords.java
java GenerateRecords
cd ..
hdfs dfs -mkdir /homework2
hdfs dfs -mkdir /homework2/data
hdfs dfs -put ./records/records.txt /homework2/data
hadoop jar ${HADOOP_HOME}/share/hadoop/tools/lib/hadoop-streaming-2.6.5.jar  \
-files ./src/TopRating/mapper.py,./src/TopRating/reducer.py \
-mapper ./src/TopRating/mapper.py \
-reducer ./src/TopRating/reducer.py \
-input /homework2/data/records.txt \
-output /homework2/output/TopRating
hadoop jar ${HADOOP_HOME}/share/hadoop/tools/lib/hadoop-streaming-2.6.5.jar  \
-files ./src/TopUser/mapper.py,./src/TopUser/reducer.py \
-mapper ./src/TopUser/mapper.py \
-reducer ./src/TopUser/reducer.py \
-input /homework2/data/records.txt \
-output /homework2/output/TopUser
echo -e '\nResults'
echo -e '\nTop 10 Rating:'
echo -e '#docid \t #rating'
hdfs dfs -cat /homework2/output/TopRating/part-00000
echo -e '\nTop 10 User:'
echo -e '#docid \t #user amount'
hdfs dfs -cat /homework2/output/TopUser/part-00000