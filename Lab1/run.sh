cd records
hdfs dfs -mkdir /lab1
hdfs dfs -mkdir /lab1/rawdata
python records.py
hdfs dfs -put ./records.txt /lab1/rawdata
cd ..

hadoop jar ${HADOOP_HOME}/share/hadoop/tools/lib/hadoop-streaming-2.6.5.jar \
-files ./src/distinct/mapper.py,./src/distinct/reducer.py \
-mapper ./src/distinct/mapper.py \
-reducer ./src/distinct/reducer.py \
-input /lab1/rawdata/records.txt \
-output /lab1/filtereddata
hdfs dfs -mv /lab1/filtereddata/part-00000 /lab1/filtereddata/records.txt

hadoop jar ${HADOOP_HOME}/share/hadoop/tools/lib/hadoop-streaming-2.6.5.jar \
-files ./src/count/mapper.py,./src/count/reducer.py \
-mapper ./src/count/mapper.py \
-reducer ./src/count/reducer.py \
-input /lab1/filtereddata/records.txt \
-output /lab1/count
hdfs dfs -mv /lab1/count/part-00000 /lab1/count/count.txt

hadoop jar ${HADOOP_HOME}/share/hadoop/tools/lib/hadoop-streaming-2.6.5.jar \
-files ./src/topRate_1/mapper.py,./src/topRate_1/reducer.py \
-mapper ./src/topRate_1/mapper.py \
-reducer ./src/topRate_1/reducer.py \
-input /lab1/count/count.txt \
-output /lab1/topRate1
hdfs dfs -mv /lab1/topRate1/part-00000 /lab1/topRate1/topRate1.txt

hadoop jar ${HADOOP_HOME}/share/hadoop/tools/lib/hadoop-streaming-2.6.5.jar \
-files ./src/topUser_1/mapper.py,./src/topUser_1/reducer.py \
-mapper ./src/topUser_1/mapper.py \
-reducer ./src/topUser_1/reducer.py \
-input /lab1/count/count.txt \
-output /lab1/topUser1
hdfs dfs -mv /lab1/topUser1/part-00000 /lab1/topUser1/topUser1.txt

hadoop jar ${HADOOP_HOME}/share/hadoop/tools/lib/hadoop-streaming-2.6.5.jar \
-files ./src/topRate_2/mapper.py,./src/topRate_2/reducer.py \
-mapper ./src/topRate_2/mapper.py \
-reducer ./src/topRate_2/reducer.py \
-input /lab1/count/count.txt \
-output /lab1/topRate2
hdfs dfs -mv /lab1/topRate2/part-00000 /lab1/topRate2/topRate2.txt

hadoop jar ${HADOOP_HOME}/share/hadoop/tools/lib/hadoop-streaming-2.6.5.jar \
-files ./src/topUser_2/mapper.py,./src/topUser_2/reducer.py \
-mapper ./src/topUser_2/mapper.py \
-reducer ./src/topUser_2/reducer.py \
-input /lab1/count/count.txt \
-output /lab1/topUser2
hdfs dfs -mv /lab1/topUser2/part-00000 /lab1/topUser2/topUser2.txt
