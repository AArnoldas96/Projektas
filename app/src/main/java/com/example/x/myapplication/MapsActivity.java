package com.example.x.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
    public static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap;
    private UiSettings setting;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private MakingStations stationReader;
    private GasStation[] stationArray;
    List<Marker> mMarkers = new ArrayList<Marker>();
    Marker nearest;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setting = mMap.getUiSettings();
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Initialize Google Play Services
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

        stationReader = new MakingStations(this);
        stationArray = stationReader.readFiles(mPath);

        for (GasStation station : stationArray) {
            if (station.Location != null) {
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
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(station.Location);
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(station.Location)
                        .title(station.toString()).icon(BitmapDescriptorFactory.fromAsset(img)));
                mMarkers.add(marker);
            }
            /*mMap.addMarker(new MarkerOptions()
                    .position(station.Location)
                    .title(station.toString()));*/
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
                            if (station.Location != null) {
                                if (!(station.getFood() && station.getCarwash() && station.getAir())) {
                                    for (Marker marker : mMarkers) {
                                        if (marker.getTitle().equals(station.toString())) {
                                            //marker.remove();
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
                            if (station.Location != null) {
                                if (!(station.getFood() && station.getCarwash())) {
                                    for (Marker marker : mMarkers) {
                                        if (marker.getTitle().equals(station.toString())) {
                                            //marker.remove();
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
                            if (station.Location != null) {
                                if (!(station.getFood() && station.getAir())) {
                                    for (Marker marker : mMarkers) {
                                        if (marker.getTitle().equals(station.toString())) {
                                            //marker.remove();
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
                            if (station.Location != null) {
                                if (!(station.getFood() == true)) {
                                    for (Marker marker : mMarkers) {
                                        if (marker.getTitle().equals(station.toString())) {
                                            //marker.remove();
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
                            if (station.Location != null) {
                                if(station.getFood()!=true) {
                                    /*Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(station.Location)
                                            .title(station.toString()));
                                    mMarkers.add(marker);*/
                                    changeMarkerImage(station);
                                }

                            }
                        }
                    } else if (check2.isChecked()) {
                        for (GasStation station : stationArray) {
                            if (station.Location != null) {
                                if (station.getCarwash() && station.getFood()!=true) {
                                    /*Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(station.Location)
                                            .title(station.toString()));
                                    mMarkers.add(marker);*/
                                    changeMarkerImage(station);
                                }
                            }
                        }
                    } else if (check3.isChecked()) {
                        for (GasStation station : stationArray) {
                            if (station.Location != null) {
                                if (station.getAir() && station.getFood()!=true) {
                                    /*Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(station.Location)
                                            .title(station.toString()));
                                    mMarkers.add(marker);*/
                                    changeMarkerImage(station);
                                }
                            }
                        }
                    } else {
                        for (GasStation station : stationArray) {
                            if (station.Location != null) {
                                if (!station.getFood()) {
                                    /*Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(station.Location)
                                            .title(station.toString()));
                                    mMarkers.add(marker);*/
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
                            if (station.Location != null) {
                                if (!(station.getFood() && station.getCarwash() && station.getAir())) {
                                    for (Marker marker : mMarkers) {
                                        if (marker.getTitle().equals(station.toString())) {
                                            //marker.remove();
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
                            if (station.Location != null) {
                                if (!(station.getFood() && station.getCarwash())) {
                                    for (Marker marker : mMarkers) {
                                        if (marker.getTitle().equals(station.toString())) {
                                            //marker.remove();
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
                            if (station.Location != null) {
                                if (!(station.getCarwash() && station.getAir())) {
                                    for (Marker marker : mMarkers) {
                                        if (marker.getTitle().equals(station.toString())) {
                                            //marker.remove();
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
                            if (station.Location != null) {
                                if (!station.getCarwash() == true) {
                                    for (Marker marker : mMarkers) {
                                        if (marker.getTitle().equals(station.toString())) {
                                            //marker.remove();
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
                            if (station.Location != null) {
                                if (station.getFood() && station.getAir() && station.getCarwash()!=true) {
                                    /*Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(station.Location)
                                            .title(station.toString()));
                                    mMarkers.add(marker);*/
                                    changeMarkerImage(station);
                                }
                            }
                        }
                    } else if (check1.isChecked()) {
                        for (GasStation station : stationArray) {
                            if (station.Location != null) {
                                if (station.getFood() && station.getCarwash()!=true) {
                                    /*Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(station.Location)
                                            .title(station.toString()));
                                    mMarkers.add(marker);*/
                                    changeMarkerImage(station);
                                }
                            }
                        }
                    } else if (check3.isChecked()) {
                        for (GasStation station : stationArray) {
                            if (station.Location != null) {
                                if (station.getAir() && station.getCarwash()!=true) {
                                    /*Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(station.Location)
                                            .title(station.toString()));
                                    mMarkers.add(marker);*/
                                    changeMarkerImage(station);
                                }
                            }
                        }
                    } else {
                        for (GasStation station : stationArray) {
                            if (station.Location != null) {
                                if (!station.getCarwash() && station.getCarwash()!=true) {
                                    /*Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(station.Location)
                                            .title(station.toString()));
                                    mMarkers.add(marker);*/
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
                            if (station.Location != null) {
                                if (!(station.getFood() && station.getCarwash() && station.getAir())) {
                                    for (Marker marker : mMarkers) {
                                        if (marker.getTitle().equals(station.toString())) {
                                            //marker.remove();
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
                            if (station.Location != null) {
                                if (!(station.getFood() && station.getAir())) {
                                    for (Marker marker : mMarkers) {
                                        if (marker.getTitle().equals(station.toString())) {
                                            //marker.remove();
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
                            if (station.Location != null) {
                                if (!(station.getCarwash() && station.getAir())) {
                                    for (Marker marker : mMarkers) {
                                        if (marker.getTitle().equals(station.toString())) {
                                            //marker.remove();
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
                            if (station.Location != null) {
                                if (!station.getAir()) {
                                    for (Marker marker : mMarkers) {
                                        if (marker.getTitle().equals(station.toString())) {
                                            //marker.remove();
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
                            if (station.Location != null) {
                                if (station.getFood() && station.getCarwash() && station.getAir()!=true) {
                                    /*Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(station.Location)
                                            .title(station.toString()));
                                    mMarkers.add(marker);*/
                                    changeMarkerImage(station);
                                }
                            }
                        }
                    } else if (check1.isChecked()) {
                        for (GasStation station : stationArray) {
                            if (station.Location != null) {
                                if (station.getFood() && station.getAir()!=true) {
                                    /*Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(station.Location)
                                            .title(station.toString()));
                                    mMarkers.add(marker);*/
                                }
                            }
                        }
                    } else if (check2.isChecked()) {
                        for (GasStation station : stationArray) {
                            if (station.Location != null) {
                                if (station.getFood() && station.getAir()!=true) {
                                    /*Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(station.Location)
                                            .title(station.toString()));
                                    mMarkers.add(marker);*/
                                    changeMarkerImage(station);
                                }
                            }
                        }
                    } else {
                        for (GasStation station : stationArray) {
                            if (station.Location != null) {
                                if (!station.getAir()) {
                                    /*Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(station.Location)
                                            .title(station.toString()));
                                    mMarkers.add(marker);*/
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
            if (station.Location != null) {
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
                        .position(station.Location)
                        .title(station.toString()).icon(BitmapDescriptorFactory.fromAsset(img)));
                mMarkers.add(marker);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        getNearestMarker().setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
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

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
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
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            //You can add here other case statements according to your requirement.
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
            if (results[0] < mDistance){
                mDistance = results[0];
                nearest = marker;
            }
        }
        return nearest;
    }
}
