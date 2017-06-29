package com.bigdata.geoinfosys.GIS;

import com.bigdata.geoinfosys.GIS.model.DistanceComparator;
import com.bigdata.geoinfosys.GIS.model.QueryMatch;
import ch.hsr.geohash.GeoHash;
import com.google.common.collect.MinMaxPriorityQueue;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.PrefixFilter;

import java.io.IOException;
import java.util.Comparator;
import java.util.Queue;

/**
 * Created by shenying on 17/5/20.
 * 最近邻查询
 */
public class KNNQuery {

    static final byte[] TABLE = "wifi".getBytes();
    static final byte[] FAMILY = "a".getBytes();
    static final byte[] ID = "id".getBytes();
    static final byte[] X_COL = "lon".getBytes();
    static final byte[] Y_COL = "lat".getBytes();
    static final byte[] NAME = "name".getBytes();
    static final byte[] ADDR = "address".getBytes();

    final HTablePool pool;
    int precision = 7;

    public KNNQuery(HTablePool pool) {
        this.pool = pool;
    }

    public KNNQuery(HTablePool pool, int characterPrecision) {
        this.pool = pool;
        this.precision = characterPrecision;
    }

    /**
     * 找到Geohash前缀为prefix的矩形中离目标点最近的N个点
     * @param comp 与目标点的比较器
     * @param prefix Geohash前缀
     * @param n 最大返回结果数
     * @return
     * @throws IOException
     */
    Queue<QueryMatch> takeN(Comparator<QueryMatch> comp,
                            String prefix,
                            int n) throws IOException {
        Queue<QueryMatch> candidates
                = MinMaxPriorityQueue.orderedBy(comp)
                .maximumSize(n)
                .create();

        Scan scan = new Scan(prefix.getBytes());
        scan.setFilter(new PrefixFilter(prefix.getBytes()));
        scan.addFamily(FAMILY);
        scan.setMaxVersions(1);
        scan.setCaching(50);

        HTableInterface table = pool.getTable(TABLE);

        int cnt = 0;
        ResultScanner scanner = table.getScanner(scan);
        for (Result r : scanner) {
            String hash = new String(r.getRow());
            String id = new String(r.getValue(FAMILY, ID));
            String lon = new String(r.getValue(FAMILY, X_COL));
            String lat = new String(r.getValue(FAMILY, Y_COL));
            String name = new String(r.getValue(FAMILY, NAME));
            String addr = new String(r.getValue(FAMILY, ADDR));
            candidates.add(new QueryMatch(id, hash,
                    Double.parseDouble(lon),
                    Double.parseDouble(lat),
                    name, addr));
            cnt++;
        }

        table.close();

        System.out.println(
                String.format("Scan over '%s' returned %s candidates.",
                        prefix, cnt));
        return candidates;
    }

    /**
     * 获取离目标最近的n个点
     * @param lat 目标纬度
     * @param lon 目标经度
     * @param n 最大返回结果数
     * @return 所有符合条件的点
     * @throws IOException
     */
    public Queue<QueryMatch> queryKNN(double lat, double lon, int n)
            throws IOException {
        DistanceComparator comp = new DistanceComparator(lon, lat);
        Queue<QueryMatch> ret
                = MinMaxPriorityQueue.orderedBy(comp)
                .maximumSize(n)
                .create();

        for (int precision = 7; precision > 0; precision--) { // geohash精度 7 -> 1
            GeoHash target = GeoHash.withCharacterPrecision(lat, lon, precision);
            ret.addAll(takeN(comp, target.toBase32(), n)); // 对自身进行操作
            for (GeoHash h : target.getAdjacent()) { // 对所有邻居进行takeN操作
                ret.addAll(takeN(comp, h.toBase32(), n));
            }
//            QueryMatch last = null;
//            for (QueryMatch q: ret){
//                if (last != null){
//                    System.out.println(q.getHash() + "    last:" + last.getHash());
//                }
//                if (last != null && q.getHash().equals(last.getHash())){
//                    ret.remove(q);
//                    System.out.println("same");
//                }
//                last = q;
//            }
            if (ret.size() == n){
                return ret;
            }
            ret.clear();
        }

        return ret;
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 3) {
            System.out.println("KNNQuery: require 3 arguments");
            System.exit(0);
        }


        double lon = Double.parseDouble(args[0]);
        double lat = Double.parseDouble(args[1]);
        int n = Integer.parseInt(args[2]);

        HTablePool pool = new HTablePool();
        KNNQuery q = new KNNQuery(pool);
        Queue<QueryMatch> ret = q.queryKNN(lat, lon, n);

        QueryMatch m;
        while ((m = ret.poll()) != null) {
            System.out.println(m);
        }

        pool.close();
    }
}

