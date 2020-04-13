package com.example.pablo.kayakapp.training;

/**
 * Created by Pablo on 30/04/2018.
 */
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;



public class MyLocationListener implements LocationListener {

    private String lat;
    private String lon;
    private float vel;
    Context mlocationContext;

    public MyLocationListener(Context c){
        mlocationContext = c;
    }

    @Override
    public void onLocationChanged(Location location) {

        lat = String.valueOf(location.getLatitude());
        lon = String.valueOf(location.getLongitude());
        vel = location.getSpeed();

        ((Training) mlocationContext).updateGps(lat, lon, vel);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        //Toast.makeText(mlocationContext,"Gps Activado",Toast.LENGTH_SHORT ).show();
        }

    @Override
    public void onProviderDisabled(String provider) {
       // Toast.makeText(mlocationContext,"Gps Desactivado",Toast.LENGTH_SHORT ).show();
        }

}
