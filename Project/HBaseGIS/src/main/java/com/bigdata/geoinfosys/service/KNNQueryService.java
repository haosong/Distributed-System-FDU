package com.bigdata.geoinfosys.service;

import com.bigdata.geoinfosys.GIS.KNNQuery;
import com.bigdata.geoinfosys.GIS.model.QueryMatch;
import org.apache.hadoop.hbase.client.HTablePool;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mortbay.log.Log;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Queue;

/**
 * Created by shenying on 17/6/15.
 */
@WebServlet("/KNNQuery")
public class KNNQueryService extends HttpServlet {

    public KNNQueryService(){
        super();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        JSONArray jsonArray = new JSONArray();
        double lon;
        double lat;
        int k;

        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) { /*report an error*/ }

        try {
            JSONObject jsonObject =  new JSONObject(jb.toString());
            lon = jsonObject.getDouble("lng");
            lat = jsonObject.getDouble("lat");
            k = jsonObject.getInt("k");
        } catch (JSONException e) {
            // crash and burn
            throw new IOException("Error parsing JSON request string");
        }

        Log.info("lon:" + lon + " lat:" + lat + "k: " + k);

        HTablePool pool = new HTablePool();
        KNNQuery q = new KNNQuery(pool);
        Queue<QueryMatch> ret = q.queryKNN(lat, lon, k);

        QueryMatch m;
        while ((m = ret.poll()) != null) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("lat", m.getLat());
            jsonObj.put("lng", m.getLon());
            jsonObj.put("name", m.getName());
            jsonObj.put("address", m.getAddress());
            jsonArray.put(jsonObj);
            System.out.println(m);
        }
        pool.close();

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
}
