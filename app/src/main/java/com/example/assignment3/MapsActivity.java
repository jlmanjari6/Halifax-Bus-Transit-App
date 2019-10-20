package com.example.assignment3;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import android.location.Location;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;

public class MapsActivity extends FragmentActivity
        implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, OnMapReadyCallback, LocationListener {

    protected LocationManager locationManager;
    public final int MY_LOCATION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    Marker currMarker;
    List<FeedEntity> busData = new ArrayList<>();
    Bus busMarkers;
    ProgressDialog dialog;
    boolean isFirstTime = true;
    private CameraPosition cp;
    ImageView btnFilter;
    String busId;
    ScheduledExecutorService executor =
            Executors.newSingleThreadScheduledExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.mapFragment);
            mapFragment.getMapAsync(this);
        }

        //  to get bus positions from saved instance state
        if (savedInstanceState != null) {
            busMarkers = savedInstanceState.getParcelable(getString(R.string.busMarkers));
        }

        btnFilter = findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BusFilterActivity.class);
                startActivity(intent);
            }
        });

        busId = getIntent().getStringExtra("busNo");
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        // we check for location permission from the user and we request for location permission if needed.
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        // This is to get the latitude, longitude, zoom level of camera values from shared preferences that were stored
        //  using onStop() method during the application termination
        SharedPreferences shred = getSharedPreferences(getString(R.string.cp), 0);
        float lat = shred.getFloat(getString(R.string.latitude), 0);
        float lon = shred.getFloat(getString(R.string.longitude), 0);
        float zoomLevel = shred.getFloat(getString(R.string.zoom), 0);
        if (lon != 0.0 && lat != 0.0) {
            LatLng startPosition = new LatLng((double) lat, (double) lon);
            cp = new CameraPosition.Builder()
                    .target(startPosition).zoom(zoomLevel).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
        }

        // This is to set the bus markers on rotation of screen.
        //  We stored the markers in savedInstanceState and retrieved them in onCreate() method.
        if (busMarkers != null) {
            mMap.clear();
            List<Bus> b = busMarkers.getBusMarkers();
            for (Bus bus : b) {
                String vehicleID = bus.getVehicleID();
                double latitude = bus.getLatitude();
                double longitude = bus.getLongitude();

                LatLng busPosition = new LatLng(latitude, longitude);
                currMarker = mMap.addMarker(new MarkerOptions().position(busPosition)
                        .title(vehicleID));
                currMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.shuttle));
            }
        }

        // This is to display "Loading" dialog box for the user so that, in the mean time, thread execution is completed to display the bus markers
        // The if condition below is to ensure that dialog box displays only when the app is loading for the first time and also not when screen is rotated
        if (isFirstTime && busMarkers == null) {
            dialog = ProgressDialog.show(MapsActivity.this, getString(R.string.LoadTitle), getString(R.string.LoadMessage));
            dialog.show();
            isFirstTime = false;
        }

        // here we run the threads to get the live transit data and update the markers for every 15 seconds
        getBusData();
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    // This method gets the live transit data from the given URL using a thread which executes for every 15 seconds.
    // We used Handler- Post delayed to update the markers on UI
    private void getBusData() {

        executor.shutdown();
        executor = Executors.newSingleThreadScheduledExecutor();
        Runnable thread1 = new Runnable() {
            @Override
            public void run() {
                try {
                    // Your implementation goes here
                    URL url = new URL("http://gtfs.halifax.ca/realtime/Vehicle/VehiclePositions.pb");
                    FeedMessage feed = FeedMessage.parseFrom(url.openStream());
                    busData = feed.getEntityList();
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            // dismiss the "Loading" dialog box once the data is retrieved.
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            updateLocation(busData);
                        }
                    }, 15000);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        executor.scheduleAtFixedRate(thread1, 0, 15, TimeUnit.SECONDS);
    }

    // Here we are displaying the marker positions based on the data retrieved from URL
    private void updateLocation(List<FeedEntity> busData) {
        List<Bus> buses = new ArrayList<>();

        mMap.clear(); // This is to clear the old markers while updating with new markers every 15 seconds
        for (FeedEntity entity : busData) {

            String vehicleID = entity.getVehicle().getTrip().getRouteId();
            float latitude = entity.getVehicle().getPosition().getLatitude();
            float longitude = entity.getVehicle().getPosition().getLongitude();
            int delay = entity.getTripUpdate().getDelay();
            String delayStatus = "";
            if (delay == 0) {
                delayStatus = "On time";
            } else if (delay > 0) {
                delayStatus = "Early " + delay + " seconds";
            } else {
                delayStatus = "Delayed " + delay + " seconds";
            }

            // if a bus is selected to filter all the buses - to display only the selected bus markers
            if (busId == null || busId.equals("SHOW ALL")) {
                buses.add(new Bus(vehicleID, latitude, longitude));
                LatLng busPosition = new LatLng(latitude, longitude);
                currMarker = mMap.addMarker(new MarkerOptions().position(busPosition).snippet(delayStatus)
                        .title(vehicleID));
                currMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.shuttle));
            } else {
                if (vehicleID.equals(busId)) {
                    buses.add(new Bus(vehicleID, latitude, longitude));
                    LatLng busPosition = new LatLng(latitude, longitude);
                    currMarker = mMap.addMarker(new MarkerOptions().position(busPosition).snippet(delayStatus)
                            .title(vehicleID));
                    currMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.shuttle));
                }
            }
        }

        busMarkers = new Bus(buses);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                }
            }
        }
    }

    // Here we are saving the bus marker positions to restore them on rotation of screen
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.busMarkers), busMarkers);
    }

    // Here we are saving the camera position to restore it in onResume().
    @Override
    protected void onPause() {
        super.onPause();
        if (mMap != null) {
            cp = mMap.getCameraPosition();
        }
    }

    // When the application comes on foreground, we make sure the camera position is restored.
    @Override
    protected void onResume() {
        super.onResume();
        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.mapFragment);
            mapFragment.getMapAsync(this);
        }
        if (cp != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
            cp = null;
        }
    }

    // Here we are storing the latitude, longitude, zoom level of camera whenever the application is about to be terminated.
    // We use these values to restore the camera position even after the application is terminated or relaunched
    @Override
    protected void onStop() {
        super.onStop();

        if (cp != null) {
            cp = mMap.getCameraPosition();
            double latitude = cp.target.latitude;
            double longitude = cp.target.longitude;
            float zoomLevel = cp.zoom;

            SharedPreferences settings = getSharedPreferences("cp", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putFloat(getString(R.string.longitude), (float) longitude);
            editor.putFloat(getString(R.string.latitude), (float) latitude);
            editor.putFloat(getString(R.string.zoom), zoomLevel);
            editor.commit();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}


