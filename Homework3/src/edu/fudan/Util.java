package edu.fudan;

import scala.Tuple2;

import java.io.Serializable;
import java.util.Comparator;

public class Util {
    public static class IntegerComparator implements Comparator<Tuple2<String, Integer>>, Serializable {
        IntegerComparator() {
        }

        @Override
        public int compare(Tuple2<String, Integer> t1, Tuple2<String, Integer> t2) {
            return t2._2().compareTo(t1._2());
        }
    }
}
