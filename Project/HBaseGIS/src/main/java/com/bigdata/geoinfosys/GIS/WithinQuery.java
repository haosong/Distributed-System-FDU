package com.bigdata.geoinfosys.GIS;

import com.bigdata.geoinfosys.GIS.filter.WithinFilter;
import com.bigdata.geoinfosys.GIS.model.QueryMatch;
import ch.hsr.geohash.BoundingBox;
import ch.hsr.geohash.GeoHash;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.PrefixFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by shenying on 17/5/20.
 * 多边形查询
 */
public class WithinQuery {

    static final byte[] TABLE = "wifi".getBytes();
    static final byte[] FAMILY = "a".getBytes();
    static final byte[] ID = "id".getBytes();
    static final byte[] X_COL = "lon".getBytes();
    static final byte[] Y_COL = "lat".getBytes();
    static final byte[] NAME = "name".getBytes();
    static final byte[] ADDR = "address".getBytes();

    final GeometryFactory factory = new GeometryFactory();
    final HTablePool pool;

    public WithinQuery(HTablePool pool) {
        this.pool = pool;
    }

    /**
     * 获取Geohash矩形的四个拐角
     * @param hash geohash
     * @return 四个拐角（西南、西北、东南、东北）
     */
    Set<Coordinate> getCoords(GeoHash hash) {
        BoundingBox bbox = hash.getBoundingBox();
        Set<Coordinate> coords = new HashSet<Coordinate>(4);
        coords.add(new Coordinate(bbox.getMinLon(), bbox.getMinLat()));
        coords.add(new Coordinate(bbox.getMinLon(), bbox.getMaxLat()));
        coords.add(new Coordinate(bbox.getMaxLon(), bbox.getMaxLat()));
        coords.add(new Coordinate(bbox.getMaxLon(), bbox.getMinLat()));
        return coords;
    }

    /**
     * 获取所有GeoHash拐角的凸包
     * @param hashes 所有GeoHash
     * @return 凸包
     */
    Geometry convexHull(GeoHash[] hashes) {
        Set<Coordinate> coords = new HashSet<Coordinate>();
        for (GeoHash hash : hashes) { // 收集Geohash所有的拐角
            coords.addAll(getCoords(hash));
        }
        Geometry geom
                = factory.createMultiPoint(coords.toArray(new Coordinate[0]));
        return geom.convexHull();
    }

    /**
     * 获取能最小包围多边形的geohash前缀集合
     * @param query
     * @return
     */
    GeoHash[] minimumBoundingPrefixes(Geometry query) {
        GeoHash candidate;
        Geometry candidateGeom;
        Point queryCenter = query.getCentroid(); // 形心
        for (int precision = 7; precision > 0; precision--) { // geohash精度 7 -> 1
            candidate
                    = GeoHash.withCharacterPrecision(queryCenter.getY(),
                    queryCenter.getX(),
                    precision);

            candidateGeom = convexHull(new GeoHash[]{ candidate });
            if (candidateGeom.contains(query)) { // 检查该精度的hash能否覆盖多边形
                return new GeoHash[]{ candidate };
            }

            candidateGeom = convexHull(candidate.getAdjacent());
            if (candidateGeom.contains(query)) { // 检查该精度的hash & 其8个邻居 能否覆盖多边形
                GeoHash[] ret = Arrays.copyOf(candidate.getAdjacent(), 9);
                ret[8] = candidate;
                return ret;
            }
        }
        throw new IllegalArgumentException(
                "Geometry cannot be contained by GeoHashs");
    }

    public Set<QueryMatch> query(Geometry query) throws IOException {
        GeoHash[] prefixes = minimumBoundingPrefixes(query); // 得到用于扫描的前缀集合
        Set<QueryMatch> ret = new HashSet<QueryMatch>();
        HTableInterface table = pool.getTable(TABLE);

        for (GeoHash prefix : prefixes) { // 执行扫描
            byte[] p = prefix.toBase32().getBytes();
            Scan scan = new Scan(p);
            scan.setFilter(new PrefixFilter(p));
            scan.addFamily(FAMILY);
            scan.setMaxVersions(1);
            scan.setCaching(50);

            ResultScanner scanner = table.getScanner(scan);
            for (Result r : scanner) {
                String hash = new String(r.getRow());
                String id = new String(r.getValue(FAMILY, ID));
                String lon = new String(r.getValue(FAMILY, X_COL));
                String lat = new String(r.getValue(FAMILY, Y_COL));
                String name = new String(r.getValue(FAMILY, NAME));
                String addr = new String(r.getValue(FAMILY, ADDR));
                ret.add(new QueryMatch(id, hash,
                        Double.parseDouble(lon),
                        Double.parseDouble(lat),
                        name, addr));
            }
        }

        table.close();

        int exclusionCount = 0;
        for (Iterator<QueryMatch> iter = ret.iterator(); iter.hasNext();) { // 遍历所有候选对象
            QueryMatch candidate = iter.next();
            Coordinate coord = new Coordinate(candidate.getLon(), candidate.getLat());
            Geometry point = factory.createPoint(coord);
            if (!query.contains(point)) { // 检查是否包含在多边形内
                iter.remove();
                exclusionCount++;
            }
        }
        System.out.println("Geometry predicate filtered " + exclusionCount + " points.");
        return ret;
    }

    public Set<QueryMatch> queryWithFilter(Geometry query) throws IOException {
        GeoHash[] prefixes = minimumBoundingPrefixes(query); // 得到用于扫描的前缀集合
        Filter withinFilter = new WithinFilter(query);
        Set<QueryMatch> ret = new HashSet<QueryMatch>();
        HTableInterface table = pool.getTable(TABLE);

        for (GeoHash prefix : prefixes) { // 执行扫描
            byte[] p = prefix.toBase32().getBytes();
            Filter filters = new FilterList(new PrefixFilter(p), withinFilter);
            Scan scan = new Scan(p, filters);
            scan.addFamily(FAMILY);
            scan.setMaxVersions(1);
            scan.setCaching(50);

            ResultScanner scanner = table.getScanner(scan);
            for (Result r : scanner) {
                String hash = new String(r.getRow());
                String id = new String(r.getValue(FAMILY, ID));
                String lon = new String(r.getValue(FAMILY, X_COL));
                String lat = new String(r.getValue(FAMILY, Y_COL));
                String name = new String(r.getValue(FAMILY, NAME));
                String addr = new String(r.getValue(FAMILY, ADDR));
                ret.add(new QueryMatch(id, hash,
                        Double.parseDouble(lon),
                        Double.parseDouble(lat),
                        name, addr));
            }
        }

        table.close();
        return ret;
    }

    public static void main(String[] args)
            throws IOException, ParseException {

        if (args.length != 2 || (!"local".equals(args[0]) && !"remote".equals(args[0]))) {
            System.out.println("WithinQuery: require 2 arguments");
            System.exit(0);
        }

        WKTReader reader = new WKTReader(); // to read Geometry text
        Geometry query = reader.read(args[1]);

        HTablePool pool = new HTablePool();
        WithinQuery q = new WithinQuery(pool);
        Set<QueryMatch> results;
        if ("local".equals(args[0])) {
            results = q.query(query);
        } else {
            results = q.queryWithFilter(query);
        }

        System.out.println("Query matched " + results.size() + " points.");
        for (QueryMatch result : results) {
            System.out.println(result);
        }

        pool.close();
    }
}

