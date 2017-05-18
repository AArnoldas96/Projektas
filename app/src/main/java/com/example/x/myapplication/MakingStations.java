package com.example.x.myapplication;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.android.gms.maps.model.LatLng;

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
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return mLines;
    }

    private void StationMaking(List<String> stations, GasStation[] stationArray){
        int i = 0;
        for (String station : stations){
            String[] parts = station.split(";");
            if (parts.length > 3) {
                LatLng loc = new LatLng(Double.parseDouble(parts[5]), Double.parseDouble(parts[6]));
                stationArray[i] = new GasStation(parts[0], parts[1], Boolean.parseBoolean(parts[2]),
                        Boolean.parseBoolean(parts[3]), Boolean.parseBoolean(parts[4]));
                stationArray[i].setLocation(loc);
                i++;
            }
        }
    }
}
