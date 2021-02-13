package com.example.sportstracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;


import static com.example.sportstracker.MainActivity.EXTRA;
import static com.example.sportstracker.MainActivity.NAME_OF_ACTIVITY;
import static com.example.sportstracker.MainActivity.RECORDING_PREF;
import static com.example.sportstracker.MainActivity.SHARED_PREFERENCES;

/**
 * Show mapView with actual tracking progress.
 */
public class RecordActivity extends AppCompatActivity implements OnMapReadyCallback {

    private RoutesMethods routesMethods = new RoutesMethods();
    private MapView mapView;
    private GoogleMap gMap;

    private int routeID;
    private boolean firstChangeOfPosition = true;
    private boolean firstStart = true;
    private boolean newActivity;

    private BroadcastReceiver broadcastReceiver;
    private SharedPreferences sharedPreferences;
    public static final String FIRST_START = "start";

    private final String LAT = "lat";
    private final String LON = "lon";
    private final String BEAR = "bear";
    private final String TILT = "tilt";
    private final String ZOOM = "zoom";

    private CameraPosition cameraPosition;
    private ArrayList<LatLng> latLngArrayList = new ArrayList<>();

    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        database = new Database(RecordActivity.this);

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        routeID = sharedPreferences.getInt(NAME_OF_ACTIVITY, 0);

        Button stopButton = findViewById(R.id.button7);
        Button infoButton = findViewById(R.id.button8);

        mapView = findViewById(R.id.mapView2);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Stop tracking.
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newActivity = sharedPreferences.getBoolean(RECORDING_PREF, true);

                if (!newActivity) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecordActivity.this);
                    builder.setMessage("Stop recording?").setCancelable(true).setPositiveButton("Stop", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), ServiceGPS.class);
                            stopService(intent);
                            newActivity = true;
                            routeID = 0;

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(RECORDING_PREF, newActivity);
                            editor.remove(NAME_OF_ACTIVITY);
                            editor.apply();
                            finish();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

        // Start activity with info about actual tracking session
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RouteInfoActivity.class);
                intent.putExtra(EXTRA, routeID);
                startActivity(intent);
            }
        });

        cameraPosition = getCameraPosition();
        firstStart = sharedPreferences.getBoolean(FIRST_START, true);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(FIRST_START);
        editor.remove(LAT);
        editor.remove(LON);
        editor.remove(BEAR);
        editor.remove(TILT);
        editor.remove(ZOOM);

        editor.remove(FIRST_START);
        editor.apply();


        Log.d("RECORD_LC", "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        Log.d("RECORD_LC", "onStart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("RECORD_LC", "onDestroy");
        unregisterReceiver(broadcastReceiver);
        broadcastReceiver = null;
        if (!isFinishing()) {
            CameraPosition cameraPosition = gMap.getCameraPosition();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(FIRST_START, false);

            editor.putFloat(LAT, (float) cameraPosition.target.latitude);
            editor.putFloat(LON, (float) cameraPosition.target.longitude);
            editor.putFloat(BEAR, cameraPosition.bearing);
            editor.putFloat(TILT, cameraPosition.tilt);
            editor.putFloat(ZOOM, cameraPosition.zoom);
            editor.apply();
        }
        gMap.setMyLocationEnabled(false);
        gMap = null;
        mapView.onDestroy();
    }

    // return last camera position if map was rotated
    private CameraPosition getCameraPosition() {
        double lat = sharedPreferences.getFloat(LAT, 0);
        if (lat == 0)
            return null;
        double lon = sharedPreferences.getFloat(LON, 0);
        LatLng target = new LatLng(lat, lon);

        float zoom = sharedPreferences.getFloat(ZOOM, 0);
        float tilt = sharedPreferences.getFloat(TILT, 0);
        float bear = sharedPreferences.getFloat(BEAR, 0);
        return new CameraPosition(target, zoom, tilt, bear);

    }

    /**
     *  Create map and every time is location changed is written polyline on the map.
     *
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("RECORD_LC", "Map is Ready");
        gMap = googleMap;
        gMap.setMyLocationEnabled(true);
        gMap.getUiSettings().setZoomControlsEnabled(true);

        // move camera to restored position
        if (cameraPosition != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            gMap.moveCamera(cameraUpdate);
        }
        // load coordinates
        if (routeID != 0) {
//            latLngArrayList = routesMethods.loadLatLng(routeID, getApplicationContext());
//            latLngArrayList = database.getLatLng(routeID);
            latLngArrayList = routesMethods.getLatLng(database.getPoints(routeID));
        } else {
            Log.d("RECORD_LC", "Route ID is null");
        }

        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String latLon = intent.getStringExtra(EXTRA);
                    String[] tokens = latLon.split(",");
                    double lat = Double.parseDouble(tokens[0]);
                    double lon = Double.parseDouble(tokens[1]);
                    LatLng latlng = new LatLng(lat, lon);
                    latLngArrayList.add(latlng);
                    //write line on map
                    gMap.addPolyline(new PolylineOptions().addAll(latLngArrayList).color(Color.RED));

                    Log.d("mapLC", "On Receive: " + lat + " " + lon);
                    if (firstChangeOfPosition) {
                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
                        gMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        firstChangeOfPosition = false;
                        Log.d("MAP_LC", "First Change of Position");
                    }

                }

            };

        }
        registerReceiver(broadcastReceiver, new IntentFilter(EXTRA));

        // if there is some data write it to map
        if (!latLngArrayList.isEmpty()) {
            if (firstStart) {
                Log.d("MAP_LC", "First Start: " + routeID);
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngArrayList.get(latLngArrayList.size() - 1), 15));
            }
            // marker for first position
            gMap.addMarker(new MarkerOptions().position(latLngArrayList.get(0)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            gMap.addPolyline(new PolylineOptions().addAll(latLngArrayList).color(Color.RED));
            firstChangeOfPosition = false;
            Log.d("MAP_LC", "Writing lines from: " + routeID);
        }
    }
}