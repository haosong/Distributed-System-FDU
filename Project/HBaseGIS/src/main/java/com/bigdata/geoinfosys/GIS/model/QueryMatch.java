package com.bigdata.geoinfosys.GIS.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by shenying on 17/5/20.
 * matched points
 */
public class QueryMatch {
    private String id;
    private String hash;
    private double lon, lat;
    private double distance = Double.NaN;
    private String name;
    private String address;

    public QueryMatch(String id, String hash, double lon, double lat, String name, String address) {
        this.id = id;
        this.hash = hash;
        this.lon = lon;
        this.lat = lat;
        this.name = name;
        this.address = address;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("hash", hash)
                .append("distance", distance)
                .append("lat", lat)
                .append("lon", lon)
                .toString();
    }

    public double getLon(){
        return lon;
    }

    public double getLat(){
        return lat;
    }

    public String getHash(){
        return hash;
    }

    public void setDistance(double distance){
        this.distance = distance;
    }

    public double getDistance(){
        return distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
