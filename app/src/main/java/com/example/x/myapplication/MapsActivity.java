package com.example.x.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    public static final String mPath = "stationsAutoLatLng.txt";

    private GoogleMap mMap;
    private Marker mCurrLocationMarker;
    private GasStation[] stationArray;
    List<Marker> mMarkers = new ArrayList<>();
    List<Marker> closeMarkers = new ArrayList<>();
    Marker nearest;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings setting = mMap.getUiSettings();
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        buildGoogleApiClient();
                        mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        setting.setZoomControlsEnabled(true);
        MakingStations stationReader = new MakingStations(this);
        stationArray = stationReader.readFiles(mPath);
        for (GasStation station : stationArray) {
            changeMarkerImage(station);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        getNearestMarker().setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)) {


                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public void onMapSearch(View view) {
        EditText locationSearch = (EditText) findViewById(R.id.editText);
        String location = locationSearch.getText().toString();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    public void selectItem(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        CheckBox check1 = (CheckBox) findViewById(R.id.checkBox1);//maistas
        CheckBox check2 = (CheckBox) findViewById(R.id.checkBox2);//plovykla
        CheckBox check3 = (CheckBox) findViewById(R.id.checkBox3);//oras
        switch (view.getId()) {
            case R.id.checkBox1:
                if (checked) {
                    if (check2.isChecked() && check3.isChecked()) {
                        for (GasStation station : stationArray) {
                            if (station.getLocation() != null) {
                                if (!(station.getFood() && station.getCarwash() && station.getAir())) {
                                    for (Marker marker : mMarkers) {
                                        if (marker.getTitle().equals(station.toString())) {
                                            mMarkers.remove(marker);
                                            marker.remove();
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                    } else if (check2.isChecked()) {
                        for (GasStation station : stationArray) {
                            if (station.getLocation() != null) {
                                if (!(station.getFood() && station.getCarwash())) {
                                    for (Marker marker : mMarkers) {
                                        if (marker.getTitle().equals(station.toString())) {
                                            mMarkers.remove(marker);
                                            marker.remove();
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    } else if (check3.isChecked()) {
                        for (GasStation station : stationArray) {
                            if (station.getLocation() != null) {
                                if (!(station.getFood() && station.getAir())) {
                                    for (Marker marker : mMarkers) {
                                        if (marker.getTitle().equals(station.toString())) {
                                            mMarkers.remove(marker);
                                            marker.remove();
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        for (GasStation station : stationArray) {
                            if (station.getLocation() != null) {
                                if (!(station.getFood())) {
                                    for (Marker marker : mMarkers) {
                                        if (marker.getTitle().equals(station.toString())) {
                                            mMarkers.remove(marker);
                                            marker.remove();
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                } else {
                    if (check2.isChecked() && check3.isChecked()) {
                        for (GasStation station : stationArray) {
                            if (station.getLocation() != null) {
                                if(!station.getFood()) {
                                    changeMarkerImage(station);
                                }

                            }
                        }
                    } else if (check2.isChecked()) {
                        for (GasStation station : stationArray) {
                            if (station.getLocation() != null) {
                                if (station.getCarwash() && !station.getFood()) {
                                    changeMarkerImage(station);
                                }
                            }
                        }
                    } else if (check3.isChecked()) {
                        for (GasStation station : stationArray) {
                            if (station.getLocation() != null) {
                                if (station.getAir() && !station.getFood()) {
                                    changeMarkerImage(station);
                                }
                            }
                        }
                    } else {
                        for (GasStation station : stationArray) {
                            if (station.getLocation() != null) {
                                if (!station.getFood()) {
                                    changeMarkerImage(station);
                                }
                            }
                        }
                    }
                }
                break;
            case R.id.checkBox2:
                if (checked) {
                    if (check1.isChecked() && check3.isChecked()) {
                        for (GasStation station : stationArray) {
                            if (station.getLocation() != null) {
                                if (!(station.getFood() && station.getCarwash() && station.getAir())) {
                                    for (Marker marker : mMarkers) {
                                        if (marker.getTitle().equals(station.toString())) {
                                            mMarkers.remove(marker);
                                            marker.remove();
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    } else if (check1.isChecked()) {
                        for (GasStation station : stationArray) {
                            if (station.getLocation() != null) {
                                if (!(station.getFood() && station.getCarwash())) {
                                    for (Marker marker : mMarkers) {
                                        if (marker.getTitle().equals(station.toString())) {
                                            mMarkers.remove(marker);
                                            marker.remove();
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    } else if (check3.isChecked()) {
                        for (GasStation station : stationArray) {
                            if (station.getLocation() != null) {
                                if (!(station.getCarwash() && station.getAir())) {
                                    for (Marker marker : mMarkers) {
                                        if (marker.getTitle().equals(station.toString())) {
                                            mMarkers.remove(marker);
                                            marker.remove();
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        for (GasStation station : stationArray) {
                            if (station.getLocation() != null) {
                                if (!station.getCarwash()) {
                                    for (Marker marker : mMarkers) {
                                        if (marker.getTitle().equals(station.toString())) {
                                            mMarkers.remove(marker);
                                            marker.remove();
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (check1.isChecked() && check3.isChecked()) {
                        for (GasStation station : stationArray) {
                            if (station.getLocation() != null) {
                                if (station.getFood() && station.getAir() && !station.getCarwash()) {
                                    changeMarkerImage(station);
                                }
                            }
                        }
                    } else if (check1.isChecked()) {
                        for (GasStation station : stationArray) {
                            if (station.getLocation() != null) {
                                if (station.getFood() && !station.getCarwash()) {
                                    changeMarkerImage(station);
                                }
                            }
                        }
                    } else if (check3.isChecked()) {
                        for (GasStation station : stationArray) {
                            if (station.getLocation() != null) {
                                if (station.getAir() && !station.getCarwash()) {
                                    changeMarkerImage(station);
                                }
                            }
                        }
                    } else {
                        for (GasStation station : stationArray) {
                            if (station.getLocation() != null) {
                                if (!station.getCarwash() && !station.getCarwash()) {
                                    changeMarkerImage(station);
                                }
                            }
                        }
                    }
                }

                break;
            case R.id.checkBox3:
                if (checked) {
                    if (check1.isChecked() && check2.isChecked()) {
                        for (GasStation station : stationArray) {
                            if (station.getLocation() != null) {
                                if (!(station.getFood() && station.getCarwash() && station.getAir())) {
                                    for (Marker marker : mMarkers) {
                                        if (marker.getTitle().equals(station.toString())) {
                                            mMarkers.remove(marker);
                                            marker.remove();
                                            break;
                                        }
                                    }
                                }

                            }
                        }
                    } else if (check1.isChecked()) {
                        for (GasStation station : stationArray) {
                            if (station.getLocation() != null) {
                                if (!(station.getFood() && station.getAir())) {
                                    for (Marker marker : mMarkers) {
                                        if (marker.getTitle().equals(station.toString())) {
                                            mMarkers.remove(marker);
                                            marker.remove();
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    } else if (check2.isChecked()) {
                        for (GasStation station : stationArray) {
                            if (station.getLocation() != null) {
                                if (!(station.getCarwash() && station.getAir())) {
                                    for (Marker marker : mMarkers) {
                                        if (marker.getTitle().equals(station.toString())) {
                                            mMarkers.remove(marker);
                                            marker.remove();
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        for (GasStation station : stationArray) {
                            if (station.getLocation() != null) {
                                if (!station.getAir()) {
                                    for (Marker marker : mMarkers) {
                                        if (marker.getTitle().equals(station.toString())) {
                                            mMarkers.remove(marker);
                                            marker.remove();
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (check1.isChecked() && check2.isChecked()) {
                        for (GasStation station : stationArray) {
                            if (station.getLocation() != null) {
                                if (station.getFood() && station.getCarwash() && !station.getAir()) {
                                    changeMarkerImage(station);
                                }
                            }
                        }
                    } else if (check1.isChecked()) {
                        for (GasStation station : stationArray) {
                            if (station.getLocation() != null) {
                                if (station.getFood() && !station.getAir()) {
                                }
                            }
                        }
                    } else if (check2.isChecked()) {
                        for (GasStation station : stationArray) {
                            if (station.getLocation() != null) {
                                if (station.getFood() && !station.getAir()) {
                                    changeMarkerImage(station);
                                }
                            }
                        }
                    } else {
                        for (GasStation station : stationArray) {
                            if (station.getLocation() != null) {
                                if (!station.getAir()) {
                                    changeMarkerImage(station);
                                }
                            }
                        }
                    }
                }
                break;
        }
    }

    public void changeMarkerImage(GasStation station){
            if (station.getLocation() != null) {
                String img;
                switch (station.getName()){
                    case "Lukoil":
                        img="lukoil.png";
                        break;
                    case "Orlen":
                        img="orlen.png";
                        break;
                    case "Baltic Petroleum":
                        img="baltic-petroleum.png";
                        break;
                    case "Jozita":
                        img="jozita.png";
                        break;
                    case "Luktarna":
                        img="luk.png";
                        break;
                    default:
                        img="neste.png";
                        break;
                }
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(station.getLocation())
                        .title(station.toString()).icon(BitmapDescriptorFactory.fromAsset(img)));
                mMarkers.add(marker);
        }
    }

    public Marker getNearestMarker(){
        if (nearest != null){
            nearest.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
        float mDistance = 99999999;
        for (Marker marker : mMarkers){
            float[] results = new float[1];
            Location.distanceBetween(mCurrLocationMarker.getPosition().latitude, mCurrLocationMarker.getPosition().longitude,
                    marker.getPosition().latitude, marker.getPosition().longitude, results);
            if (results[0] < 1000) {
                closeMarkers.add(marker);
            }
        }
        for (Marker closeMarker : closeMarkers){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
            StrictMode.setThreadPolicy(policy);
            GetDistanceJSON test = new GetDistanceJSON(mCurrLocationMarker.getPosition(), closeMarker.getPosition());
            test.getJson();
            if (test.getDistance() < mDistance){
                mDistance = test.getDistance();
                nearest = closeMarker;
            }
        }
        if (nearest == null){
            nearest = mCurrLocationMarker;
        }
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().detectNetwork().build();
        StrictMode.setThreadPolicy(policy);
        return nearest;
    }
}
