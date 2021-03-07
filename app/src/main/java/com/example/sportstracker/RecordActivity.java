package com.example.sportstracker;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;


import static com.example.sportstracker.MainActivity.EXTRA;
import static com.example.sportstracker.MainActivity.NAME_OF_ACTIVITY;
import static com.example.sportstracker.MainActivity.PAUSE;
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

    private Handler handler;
    private Runnable runnable;

    private TextView timeView;
    private TextView timeMovingView;
    private TextView distanceView;
    private TextView altitudeView;
    private TextView eleGainView;
    private TextView eleLossView;
    private TextView speedView;
    private TextView paceView;
    private TextView avgSpeedView;
    private TextView avgSpeedMovingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        database = new Database(RecordActivity.this);

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        routeID = sharedPreferences.getInt(NAME_OF_ACTIVITY, 0);

        ImageView stopButton = findViewById(R.id.stop);
        final ImageView pauseButton = findViewById(R.id.pause);
        ImageView infoButton = findViewById(R.id.info);

        mapView = findViewById(R.id.mapView2);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

//        navigationView.bringToFront();

        timeView = findViewById(R.id.time);
        timeMovingView = findViewById(R.id.timeMoving);
        distanceView = findViewById(R.id.distance);
        altitudeView = findViewById(R.id.altitude);
        eleGainView = findViewById(R.id.elevationGain);
        eleLossView = findViewById(R.id.elevationLoss);
        speedView = findViewById(R.id.speed);
        avgSpeedView = findViewById(R.id.avgSpeed);
        avgSpeedMovingView = findViewById(R.id.avgSpeedMoving);


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
                            database.updateActivity(routeID, 0, System.currentTimeMillis(), "");
                            routeID = 0;

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(PAUSE, false);
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
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);

                }
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean pause = sharedPreferences.getBoolean(PAUSE, false);
                pause = !pause;
                if (pause)
                    pauseButton.setImageResource(R.drawable.ic_record);
                else
                    pauseButton.setImageResource(R.drawable.ic_pause);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(PAUSE, pause);
                editor.apply();
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

        boolean pause = sharedPreferences.getBoolean(PAUSE, false);
        if (pause)
           pauseButton.setImageResource(R.drawable.ic_record);
        else
            pauseButton.setImageResource(R.drawable.ic_pause);

        this.changeContent();

        Log.d("RECORD_LC", "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        Log.d("RECORD_LC", "onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void openFragment(View v) {
        DashboardFragment fragment = new DashboardFragment();
        fragment.setEnterTransition(android.R.transition.slide_bottom);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment)
                .commit();
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
        handler.removeCallbacks(this.runnable);
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
     * Create map and every time is location changed is written polyline on the map.
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

    private void refresh() {
        this.handler = new Handler();
        this.runnable = new Runnable() {
            @Override
            public void run() {
                changeContent();
            }
        };
        this.handler.postDelayed(this.runnable, 1000);
    }

    private void changeContent() {
        ArrayList<Point> points = database.getPoints(routeID);
        double distance = routesMethods.getDistance(points);

        double[] hours = routesMethods.getHours(points);
        int[] hoursMinutesSeconds = routesMethods.getHoursMinutesSeconds(hours[0]);
        String minutesStr = hoursMinutesSeconds[1] < 10 ? "0" + hoursMinutesSeconds[1] : "" + hoursMinutesSeconds[1];
        String secondsStr = hoursMinutesSeconds[2] < 10 ? "0" + hoursMinutesSeconds[2] : "" + hoursMinutesSeconds[2];
        timeView.setText(hoursMinutesSeconds[0] + ":" + minutesStr + ":" + secondsStr);

        hoursMinutesSeconds = routesMethods.getHoursMinutesSeconds(hours[1]);
        minutesStr = hoursMinutesSeconds[1] < 10 ? "0" + hoursMinutesSeconds[1] : "" + hoursMinutesSeconds[1];
        secondsStr = hoursMinutesSeconds[2] < 10 ? "0" + hoursMinutesSeconds[2] : "" + hoursMinutesSeconds[2];
        timeMovingView.setText(hoursMinutesSeconds[0] + ":" + minutesStr + ":" + secondsStr);

        distanceView.setText(distance + " km");
        if (points.size() > 0) {
            altitudeView.setText(points.get(points.size() - 1).getEle() + " m");
            speedView.setText(points.get(points.size() - 1).getSpeed() + " km/h");
        }

        eleGainView.setText(routesMethods.getElevationGainLoss(points)[0] + " m");
        eleLossView.setText("-" + routesMethods.getElevationGainLoss(points)[1] + " m");

        double avgSpeed = Math.round(distance / hours[0] * 10) / 10.0;
        avgSpeedView.setText(avgSpeed + " km/h");
        avgSpeed = Math.round(distance / hours[1] * 10) / 10.0;
        avgSpeedMovingView.setText(avgSpeed + " km/h");

        refresh();
    }




}