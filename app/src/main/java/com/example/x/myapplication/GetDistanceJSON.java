package com.example.x.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Arnoldas on 2017-05-06.
 */

public class GetDistanceJSON {
    private static String Url;
    public String distance;

    GetDistanceJSON(LatLng origin, LatLng destination){
        this.Url = "https://maps.googleapis.com/maps/api/directions/json?origin="+origin.latitude+","+origin.longitude+"&destination="+destination.latitude+","+destination.longitude+"&sensor=false&units=metric&mode=driving";
    }

    protected void getJson() {

        HttpURLConnection conn = null;
        final StringBuilder json = new StringBuilder();
        try {

            URL url = new URL(this.Url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());


            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                json.append(buff, 0, read);
            }
        } catch (IOException e) {
            Log.e("JsonParser", "Error connecting to service", e);
            //throw new IOException("Error connecting to service", e); //uncaught
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        setDistance(json.toString());
    }

    protected void setDistance(String json) {

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray routesArray = jsonObject.getJSONArray("routes");
            JSONObject route = routesArray.getJSONObject(0);
            JSONArray legs = route.getJSONArray("legs");
            JSONObject leg = legs.getJSONObject(0);

            JSONObject distanceObject = leg.getJSONObject("distance");
            this.distance = distanceObject.getString("value");
            }
        catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    protected float getDistance(){
        float distance = Float.parseFloat(this.distance);
        return distance;
    }
}
