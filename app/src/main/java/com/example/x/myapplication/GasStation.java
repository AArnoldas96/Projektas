package com.example.x.myapplication;


import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Arnoldas on 2017-03-18.
 */

public class GasStation {
    private String StationName;
    String Adress;
    private boolean Food;
    private boolean Air;
    private boolean Carwash;
    LatLng Location;

    public GasStation(){
    }

    public GasStation(String adress, String name, boolean food, boolean air, boolean carwash) {
        StationName = name;
        Adress = adress;
        Food = food;
        Air = air;
        Carwash = carwash;
    }

    public void setLocation(LatLng locat){
        Location = locat;
    }

    @Override
    public String toString(){
        return String.format("%1$-10s, %2$10s", StationName, Adress);
    }
}
