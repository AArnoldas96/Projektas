package com.example.x.myapplication;

import android.widget.CheckBox;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class CheckedService {

    public void checkedFirst(CheckBox check2, CheckBox check3, boolean checked, GasStation[] stationArray,
                             List<Marker> mMarkers,GoogleMap mMap){
        if (checked) {
            if (check2.isChecked() && check3.isChecked()) {
                for (GasStation station : stationArray) {
                    if (station.Location != null) {
                        if (!(station.getFood() && station.getCarwash() && station.getAir())) {
                            removeMarker(mMarkers, station);
                        }
                    }
                }

            } else if (check2.isChecked()) {
                for (GasStation station : stationArray) {
                    if (station.Location != null) {
                        if (!(station.getFood() && station.getCarwash())) {
                            removeMarker(mMarkers, station);
                        }
                    }
                }
            } else if (check3.isChecked()) {
                for (GasStation station : stationArray) {
                    if (station.Location != null) {
                        if (!(station.getFood() && station.getAir())) {
                            removeMarker(mMarkers, station);
                        }
                    }
                }
            } else {
                for (GasStation station : stationArray) {
                    if (station.Location != null) {
                        if (!(station.getFood() == true)) {
                            removeMarker(mMarkers, station);
                        }
                    }
                }
            }

        } else {
            if (check2.isChecked() && check3.isChecked()) {
                for (GasStation station : stationArray) {
                    if (station.Location != null) {
                        if (station.getFood() && station.getAir() && station.getCarwash() != true) {
                            changeMarkerImage(station, mMap, mMarkers);
                        }

                    }
                }
            } else if (check2.isChecked()) {
                for (GasStation station : stationArray) {
                    if (station.Location != null) {
                        if (station.getCarwash() && station.getFood() != true) {
                            changeMarkerImage(station, mMap, mMarkers);
                        }
                    }
                }
            } else if (check3.isChecked()) {
                for (GasStation station : stationArray) {
                    if (station.Location != null) {
                        if (station.getAir() && station.getFood() != true) {
                            changeMarkerImage(station, mMap, mMarkers);
                        }
                    }
                }
            } else {
                for (GasStation station : stationArray) {
                    if (station.Location != null) {
                        if (!station.getFood()) {
                            changeMarkerImage(station, mMap, mMarkers);
                        }
                    }
                }
            }
        }
    }

    public void checkedSecond(CheckBox check1, CheckBox check3, boolean checked, GasStation[] stationArray,
                              List<Marker> mMarkers,GoogleMap mMap){
        if (checked) {
            if (check1.isChecked() && check3.isChecked()) {
                for (GasStation station : stationArray) {
                    if (station.Location != null) {
                        if (!(station.getFood() && station.getCarwash() && station.getAir())) {
                            removeMarker(mMarkers, station);
                        }
                    }
                }
            } else if (check1.isChecked()) {
                for (GasStation station : stationArray) {
                    if (station.Location != null) {
                        if (!(station.getFood() && station.getCarwash())) {
                            removeMarker(mMarkers, station);
                        }
                    }
                }
            } else if (check3.isChecked()) {
                for (GasStation station : stationArray) {
                    if (station.Location != null) {
                        if (!(station.getCarwash() && station.getAir())) {
                            removeMarker(mMarkers, station);
                        }
                    }
                }
            } else {
                for (GasStation station : stationArray) {
                    if (station.Location != null) {
                        if (!station.getCarwash() == true) {
                            removeMarker(mMarkers, station);
                        }
                    }
                }
            }
        } else {
            if (check1.isChecked() && check3.isChecked()) {
                for (GasStation station : stationArray) {
                    if (station.Location != null) {
                        if (station.getFood() && station.getAir() && station.getCarwash() != true) {
                            changeMarkerImage(station, mMap, mMarkers);
                        }
                    }
                }
            } else if (check1.isChecked()) {
                for (GasStation station : stationArray) {
                    if (station.Location != null) {
                        if (station.getFood() && station.getCarwash() != true) {
                            changeMarkerImage(station, mMap, mMarkers);
                        }
                    }
                }
            } else if (check3.isChecked()) {
                for (GasStation station : stationArray) {
                    if (station.Location != null) {
                        if (station.getAir() && station.getCarwash() != true) {
                            changeMarkerImage(station, mMap, mMarkers);
                        }
                    }
                }
            } else {
                for (GasStation station : stationArray) {
                    if (station.Location != null) {
                        if (!station.getCarwash()) {
                            changeMarkerImage(station, mMap, mMarkers);
                        }
                    }
                }
            }
        }
    }
    public void checkedThird(CheckBox check1, CheckBox check2, boolean checked, GasStation[] stationArray,
                             List<Marker> mMarkers,GoogleMap mMap){
        if (checked) {
            if (check1.isChecked() && check2.isChecked()) {
                for (GasStation station : stationArray) {
                    if (station.Location != null) {
                        if (!(station.getFood() && station.getCarwash() && station.getAir())) {
                            removeMarker(mMarkers, station);
                        }

                    }
                }
            } else if (check1.isChecked()) {
                for (GasStation station : stationArray) {
                    if (station.Location != null) {
                        if (!(station.getFood() && station.getAir())) {
                            removeMarker(mMarkers, station);
                        }
                    }
                }
            } else if (check2.isChecked()) {
                for (GasStation station : stationArray) {
                    if (station.Location != null) {
                        if (!(station.getCarwash() && station.getAir())) {
                            removeMarker(mMarkers, station);
                        }
                    }
                }
            } else {
                for (GasStation station : stationArray) {
                    if (station.Location != null) {
                        if (!station.getAir()) {
                            removeMarker(mMarkers, station);
                        }
                    }
                }
            }
        } else {
            if (check1.isChecked() && check2.isChecked()) {
                for (GasStation station : stationArray) {
                    if (station.Location != null) {
                        if (station.getFood() && station.getCarwash() && station.getAir() != true) {
                            changeMarkerImage(station, mMap, mMarkers);
                        }
                    }
                }
            } else if (check1.isChecked()) {
                for (GasStation station : stationArray) {
                    if (station.Location != null) {
                        if (station.getFood() && station.getAir() != true) {
                            changeMarkerImage(station, mMap, mMarkers);
                        }
                    }
                }
            } else if (check2.isChecked()) {
                for (GasStation station : stationArray) {
                    if (station.Location != null) {
                        if (station.getFood() && station.getAir() != true) {
                            changeMarkerImage(station, mMap, mMarkers);
                        }
                    }
                }
            } else {
                for (GasStation station : stationArray) {
                    if (station.Location != null) {
                        if (!station.getAir()) {
                            changeMarkerImage(station, mMap, mMarkers);
                        }
                    }
                }
            }
        }
    }
    public void removeMarker(List<Marker> mMarkers, GasStation station) {
        for (Marker marker : mMarkers) {
            if (marker.getTitle().equals(station.toString())) {
                mMarkers.remove(marker);
                marker.remove();
                break;
            }
        }
    }

    public void changeMarkerImage(GasStation station, GoogleMap mMap, List<Marker> mMarkers){
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


    /*public void checked(CheckBox check2, CheckBox check3, boolean checked, GasStation[] stationArray,
                             List<Marker> mMarkers,GoogleMap mMap, String service){
        if (checked) {
            if (check2.isChecked() && check3.isChecked()) {
                for (GasStation station : stationArray) {
                    if (station.Location != null) {
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
                    if (station.Location != null) {
                        if(service.equals("maistas")){

                        }
                        else if(service.equals("plovykla")){

                        }
                        else if(service.equals("oras")){

                        }
                        if (!(station.getFood() && station.getAir()))
                        if (!(station.getFood() && station.getCarwash()))
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
                    if (station.Location != null) {
                        if (!(station.getCarwash() && station.getAir()))
                        if (!(station.getCarwash() && station.getAir()))
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
                    if (station.Location != null) {
                        if (!station.getAir())
                        if (!station.getCarwash() == true)
                        if (!(station.getFood() == true)) {
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
                    if (station.Location != null) {
                        if (station.getFood() && station.getAir() && station.getCarwash() != true){
                            changeMarkerImage(station, mMap, mMarkers);
                        }

                    }
                }
            } else if (check2.isChecked()) {
                for (GasStation station : stationArray) {
                    if (station.Location != null) {
                        if (station.getFood() && station.getCarwash() != true)
                        if (station.getFood() && station.getCarwash() != true)
                        if (station.getCarwash() && station.getFood() != true) {
                            changeMarkerImage(station, mMap, mMarkers);
                        }
                    }
                }
            } else if (check3.isChecked()) {
                for (GasStation station : stationArray) {
                    if (station.Location != null) {
                        if (station.getFood() && station.getAir() != true)
                        if (station.getAir() && station.getCarwash() != true)
                        if (station.getAir() && station.getFood() != true) {
                            changeMarkerImage(station, mMap, mMarkers);
                        }
                    }
                }
            } else {
                for (GasStation station : stationArray) {
                    if (station.Location != null) {
                        if (!station.getAir())
                        if (!station.getCarwash())
                        if (!station.getFood()) {
                            changeMarkerImage(station, mMap, mMarkers);
                        }
                    }
                }
            }
        }


    }*/
}
