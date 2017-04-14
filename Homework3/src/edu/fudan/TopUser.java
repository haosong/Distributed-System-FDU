package edu.fudan;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;

public class TopUser {

    public static void main(String[] args) {
        new TopUser().run("./data/records.txt");
    }

    public void run(String filePath) {

        String master = "local[*]";
        SparkConf conf = new SparkConf().setAppName(TopUser.class.getName()).setMaster(master);
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> lines = sc.textFile(filePath);
        JavaRDD<String> line = lines.flatMap(text -> Arrays.asList(text.split("\n")).iterator());
        JavaPairRDD<String, Integer> pairs = line.mapToPair(record -> new Tuple2(record.split("\t")[1], 1));
        JavaPairRDD<String, Integer> counts = pairs.reduceByKey((a, b) -> a + b);
        List<Tuple2<String, Integer>> topN = counts.takeOrdered(10, new Util.IntegerComparator());

        System.out.println("Top 10 doctors with most users:");
        System.out.println("#docid\t#user number");
        for (Tuple2<String, Integer> r : topN) {
            System.out.println(r._1() + " \t" + r._2());
        }
        
    }
}