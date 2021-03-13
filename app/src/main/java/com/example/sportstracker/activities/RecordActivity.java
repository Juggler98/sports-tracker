package com.example.sportstracker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sportstracker.fragments.DashboardFragment;
import com.example.sportstracker.data.Database;
import com.example.sportstracker.data.Point;
import com.example.sportstracker.R;
import com.example.sportstracker.data.Route;
import com.example.sportstracker.RoutesMethods;
import com.example.sportstracker.services.ServiceGPS;
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

import static java.lang.Math.max;
import static java.lang.Math.min;


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
    private SharedPreferences defaultSharedPreferences;

    private final String FIRST_START = "start";
    private final String LAT = "lat";
    private final String LON = "lon";
    private final String BEAR = "bear";
    private final String TILT = "tilt";
    private final String ZOOM = "zoom";

    private CameraPosition cameraPosition;
    private ArrayList<LatLng> latLngArrayList = new ArrayList<>();
    private ArrayList<Point> twoPoints = new ArrayList<>();
    private double oldSpeed = 0;
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
    private TextView avgSpeedView;
    private TextView avgSpeedMovingView;

    private TextView avgSpeedInfoView;
    private TextView avgSpeedMovingInfoView;
    private TextView speedInfoView;

    private double avgSpeed;
    private double avgSpeedMoving;
    private double speed;

    private boolean avgVsPace;
    private int delayMillis;

    private Route route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        database = new Database(RecordActivity.this);

        sharedPreferences = getSharedPreferences(getString(R.string.sharedPreferences), MODE_PRIVATE);
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        routeID = sharedPreferences.getInt(getString(R.string.routeNamePref), 0);

        delayMillis = Integer.parseInt(defaultSharedPreferences.getString(getString(R.string.timeIntervalPref),"4")) * 1000;
        delayMillis = min(delayMillis, 30*1000);
        delayMillis = max(delayMillis, 1000);

        ImageView stopButton = findViewById(R.id.stop);
        final ImageView pauseButton = findViewById(R.id.pause);
        ImageView infoButton = findViewById(R.id.info);

        mapView = findViewById(R.id.mapView2);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();

        timeView = findViewById(R.id.time);
        timeMovingView = findViewById(R.id.timeMoving);
        distanceView = findViewById(R.id.distance);
        altitudeView = findViewById(R.id.altitude);
        eleGainView = findViewById(R.id.elevationGain);
        eleLossView = findViewById(R.id.elevationLoss);
        speedView = findViewById(R.id.speed);
        avgSpeedView = findViewById(R.id.avgSpeed);
        avgSpeedMovingView = findViewById(R.id.avgSpeedMoving);

        avgSpeedInfoView = findViewById(R.id.avgInfo);
        avgSpeedMovingInfoView = findViewById(R.id.avgMovingInfo);
        speedInfoView = findViewById(R.id.speedInfo);

        route = database.getActivity(routeID);

        avgVsPace = route.getIdType() == 3;

        avgSpeedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avgVsPace = !avgVsPace;
                setAvgSpeedPace();
            }
        });

        avgSpeedMovingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avgVsPace = !avgVsPace;
                setAvgSpeedPace();
            }
        });

        speedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avgVsPace = !avgVsPace;
                setAvgSpeedPace();
            }
        });


        // Stop tracking.
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newActivity = sharedPreferences.getBoolean(getString(R.string.recordingPref), true);

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
                            editor.putBoolean(getString(R.string.pausePref), false);
                            editor.putBoolean(getString(R.string.recordingPref), newActivity);
                            editor.remove(getString(R.string.routeNamePref));
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
                boolean pause = sharedPreferences.getBoolean(getString(R.string.pausePref), false);
                pause = !pause;
                if (pause)
                    pauseButton.setImageResource(R.drawable.ic_record);
                else
                    pauseButton.setImageResource(R.drawable.ic_pause);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(getString(R.string.pausePref), pause);
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

        boolean pause = sharedPreferences.getBoolean(getString(R.string.pausePref), false);
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

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int mapType = Integer.parseInt(defaultSharedPreferences.getString(getString(R.string.mapTypePref),"0"));
        switch (mapType) {
            case 0:
                gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case 1:
                gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case 2:
                gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case 3:
                gMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            default:
                gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }

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
                    String latLon = intent.getStringExtra(getString(R.string.intentExtra));
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
//                        gMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        gMap.addMarker(new MarkerOptions().position(latLngArrayList.get(0)).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_start)));
                        firstChangeOfPosition = false;
                        Log.d("MAP_LC", "First Change of Position");
                    }

                }

            };

        }
        registerReceiver(broadcastReceiver, new IntentFilter(getString(R.string.intentExtra)));

        // if there is some data write it to map
        if (!latLngArrayList.isEmpty()) {
            if (firstStart) {
                Log.d("MAP_LC", "First Start: " + routeID);
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngArrayList.get(latLngArrayList.size() - 1), 15));
            }
            // marker for first position
//            gMap.addMarker(new MarkerOptions().position(latLngArrayList.get(0)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            gMap.addMarker(new MarkerOptions().position(latLngArrayList.get(0)).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_start)));
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
        this.handler.postDelayed(this.runnable, delayMillis);
    }

    private void changeContent() {
        ArrayList<Point> points = database.getPoints(routeID);
        if (points.size() > 0) {
            twoPoints.add(points.get(points.size() - 1));
        }
        double distance = routesMethods.getDistance(points)/1000.0;

        double[] hours = routesMethods.getHours(points, route.getAutoPause());
        int[] hoursMinutesSeconds = routesMethods.getHoursMinutesSeconds(hours[0]);
        String minutesStr = hoursMinutesSeconds[1] < 10 ? "0" + hoursMinutesSeconds[1] : "" + hoursMinutesSeconds[1];
        String secondsStr = hoursMinutesSeconds[2] < 10 ? "0" + hoursMinutesSeconds[2] : "" + hoursMinutesSeconds[2];
        timeView.setText(hoursMinutesSeconds[0] + ":" + minutesStr + ":" + secondsStr);

        hoursMinutesSeconds = routesMethods.getHoursMinutesSeconds(hours[1]);
        minutesStr = hoursMinutesSeconds[1] < 10 ? "0" + hoursMinutesSeconds[1] : "" + hoursMinutesSeconds[1];
        secondsStr = hoursMinutesSeconds[2] < 10 ? "0" + hoursMinutesSeconds[2] : "" + hoursMinutesSeconds[2];
        timeMovingView.setText(hoursMinutesSeconds[0] + ":" + minutesStr + ":" + secondsStr);

        distanceView.setText(Math.round(distance * 100.0) / 100.0 + " km");
        if (points.size() > 0) {
            altitudeView.setText((int) points.get(points.size() - 1).getEle() + " m");
            speed = points.get(points.size() - 1).getSpeed();
            if (speed <= 0 && twoPoints.size() == 2) {
                speed = routesMethods.getSpeed(twoPoints);
            }
            if (speed == oldSpeed) {
                speed = 0;
            }
            oldSpeed = speed;
        } else {
            speed = 0;
        }



        eleGainView.setText(Math.round(routesMethods.getElevationGainLoss(points)[0]) + " m");
        eleLossView.setText("-" + Math.round(routesMethods.getElevationGainLoss(points)[1]) + " m");

        avgSpeed = Math.round(distance / hours[0] * 10) / 10.0;
        avgSpeedMoving = Math.round(distance / hours[1] * 10) / 10.0;
        speed = Math.round(speed * 3.6 * 10) / 10.0;

        setAvgSpeedPace();

        if (twoPoints.size() == 2) {
            twoPoints.remove(0);
        }

        refresh();
    }

    private void setAvgSpeedPace() {
        if (avgVsPace) {
            avgSpeedInfoView.setText(getString(R.string.avg_pace));
            avgSpeedMovingInfoView.setText("Avg Pace (mov)");
            speedInfoView.setText("Pace");
            if (avgSpeed == 0) {
                avgSpeedView.setText(getString(R.string.avg_pace_null));
            } else {
                String[] pace = this.getPace(avgSpeed);
                avgSpeedView.setText(getString(R.string.avgPace_data, pace[0], pace[1]));
            }
            if (avgSpeedMoving == 0) {
                avgSpeedMovingView.setText(getString(R.string.avg_pace_null));
            } else {
                String[] pace = this.getPace(avgSpeedMoving);
                avgSpeedMovingView.setText(getString(R.string.avgPace_data, pace[0], pace[1]));
            }
            if (speed == 0) {
                speedView.setText(getString(R.string.avg_pace_null));
            } else {
                String[] pace = this.getPace(speed);
                speedView.setText(getString(R.string.avgPace_data, pace[0], pace[1]));
            }
        } else {
            avgSpeedInfoView.setText(getString(R.string.avg_speed));
            avgSpeedView.setText(avgSpeed + " " + getString(R.string.kmh));
            avgSpeedMovingInfoView.setText("Avg speed (mov)");
            avgSpeedMovingView.setText(avgSpeedMoving + " " + getString(R.string.kmh));
            speedInfoView.setText("Speed");
            speedView.setText(speed + " " + getString(R.string.kmh));
        }
    }

    private String[] getPace(double speed) {
        String[] pace = new String[2];
        double minutesD = 60 / speed;
        int minutes = (int) minutesD;
        double secondsD = (minutesD - minutes) * 60.0;
        int seconds = (int) secondsD;
        String secondsStr = seconds < 10 ? "0" + seconds : "" + seconds;
        pace[0] = minutes + "";
        pace[1] = secondsStr;
        return pace;
    }


}