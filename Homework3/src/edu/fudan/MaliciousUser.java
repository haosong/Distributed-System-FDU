package edu.fudan;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;

public class MaliciousUser {

    public static void main(String[] args) {
        new MaliciousUser().run("./data/records.txt");
    }

    public void run(String filePath) {
        
        String master = "local[*]";
        SparkConf conf = new SparkConf().setAppName(MaliciousUser.class.getName()).setMaster(master);
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> lines = sc.textFile(filePath);
        JavaRDD<String> line = lines.flatMap(text -> Arrays.asList(text.split("\n")).iterator());
        JavaPairRDD<String, Integer> pairs = line.mapToPair(record -> {
            int starNum = Integer.parseInt(record.split("\t")[2]);
            int isMalicious = starNum < 2 ? 1 : 0;
            return new Tuple2(record.split("\t")[0], isMalicious);
        });
        JavaPairRDD<String, Integer> counts = pairs.reduceByKey((a, b) -> a + b);

        // Get all users who once gave malicious rating.
        //List<Tuple2<String, Integer>> allMaliciousUser = counts.collect();

        // Get top 100 users who gave most malicious rating.
        //List<Tuple2<String, Integer>> top100MaliciousUser = counts.takeOrdered(100, new Util.IntegerComparator());

        // Get all users who gave more than 27 times malicious rating.
        List<Tuple2<String, Integer>> maliciousUser = counts.filter(user -> user._2() > 27).collect();

        System.out.println("Malicious users:");
        System.out.println("#userid\t#number of ratings (less than 2)");
        for (Tuple2<String, Integer> r : maliciousUser) {
            System.out.println(r._1() + " \t" + r._2());
        }

    }
}
