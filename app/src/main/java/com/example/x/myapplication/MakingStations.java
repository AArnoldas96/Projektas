package com.example.x.myapplication;

import android.content.Context;
import android.content.res.AssetManager;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.wearable.NodeApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MakingStations {
    private Context mContext;

    public MakingStations(Context context) {
        this.mContext = context;
    }

    public GasStation[] readFiles(String filepath){
        List<String> stations = readLine(filepath);
        GasStation[] stationArray = new GasStation[stations.size()];
        StationMaking(stations, stationArray);
        for (GasStation station : stationArray) {
            station.setLocation(getLocationFromAddress(station.Adress));
        }
        return stationArray;
    }



    public List<String> readLine(String path) {
        List<String> mLines = new ArrayList<>();

        AssetManager am = mContext.getAssets();

        try {
            InputStream is = am.open(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;

            while ((line = reader.readLine()) != null)
                mLines.add(line);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mLines;
    }

    private void StationMaking(List<String> stations, GasStation[] stationArray){
        int i = 0;
        for (String station : stations){
            String[] parts = station.split(";");
            if (parts.length > 3)
                stationArray[i++] = new GasStation(parts[0], parts[1], Boolean.parseBoolean(parts[2]),
                    Boolean.parseBoolean(parts[3]), Boolean.parseBoolean(parts[4]));
        }
    }

    private LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this.mContext);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address.size() < 1) {
                Log.d(MapsActivity.TAG, strAddress);
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }
}
