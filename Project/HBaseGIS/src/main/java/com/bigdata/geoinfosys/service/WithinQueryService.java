package com.bigdata.geoinfosys.service;

import com.bigdata.geoinfosys.GIS.WithinQuery;
import com.bigdata.geoinfosys.GIS.model.QueryMatch;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.apache.hadoop.hbase.client.HTablePool;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Queue;
import java.util.Set;

/**
 * Created by shenying on 17/6/15.
 */
@WebServlet("/WithinQuery")
public class WithinQueryService extends HttpServlet{

    public WithinQueryService(){
        super();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        StringBuffer buffer = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                buffer.append(line);
        } catch (Exception e) { }

        String polygon = getPolygon(buffer.toString());
        System.out.println(polygon);
        JSONArray jsonArray = new JSONArray();
        WKTReader reader = new WKTReader(); // to read Geometry text
        Geometry query = null;
        try {
            query = reader.read(polygon);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        HTablePool pool = new HTablePool();
        WithinQuery q = new WithinQuery(pool);
        Set<QueryMatch> results;

        results = q.query(query);

        for (QueryMatch result : results) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("lat", result.getLat());
            jsonObj.put("lng", result.getLon());
            jsonObj.put("name", result.getName());
            jsonObj.put("address", result.getAddress());
            jsonArray.put(jsonObj);
        }

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.append(jsonArray.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private String getPolygon(String points){
        String ret = "POLYGON ((";

        JSONArray jsonArray = new JSONArray(points);
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            double lon = jsonObj.getDouble("lng");
            double lat = jsonObj.getDouble("lat");
            ret += lon + " " + lat + ", ";
        }
        ret += jsonArray.getJSONObject(0).getDouble("lng") + " " + jsonArray.getJSONObject(0).getDouble("lat");
        ret += "))";
        return ret;
    }
}
