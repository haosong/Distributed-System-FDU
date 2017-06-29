package com.bigdata.geoinfosys.GIS.model;

import org.apache.log4j.Logger;

import java.awt.geom.Point2D;
import java.util.Comparator;

/**
 * Created by shenying on 17/5/20.
 * compare the distance between the origin point and other pointes
 */
public class DistanceComparator implements Comparator<QueryMatch> {
    private static final Logger LOG = Logger.getLogger(DistanceComparator.class);

    private Point2D origin;

    public DistanceComparator(double lon, double lat) {
        this.origin = new Point2D.Double(lon, lat);
    }

    public int compare(QueryMatch o1, QueryMatch o2) {
        if (Double.isNaN(o1.getDistance())) {
            o1.setDistance(origin.distance(o1.getLon(), o1.getLat()));
        }
        if (Double.isNaN(o2.getDistance())){
            o2.setDistance(origin.distance(o2.getLon(), o2.getLat()));
        }
        double disO1 = o1.getDistance();
        double disO2 = o2.getDistance();
        if (disO1 >= 0 && disO2 >= 0) {
            return Double.compare(disO1, disO2);
        } else {
            LOG.warn("negative distance detected!");
            return Double.compare(disO1, disO2);
        }
    }
}
