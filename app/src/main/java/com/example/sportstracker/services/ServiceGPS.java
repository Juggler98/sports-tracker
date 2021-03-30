package com.example.sportstracker.services;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.example.sportstracker.R;
import com.example.sportstracker.RoutesMethods;
import com.example.sportstracker.activities.RecordActivity;
import com.example.sportstracker.data.Database;
import com.example.sportstracker.data.Point;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;

/**
 * Creates foreground service with location manager to get location change.
 */
public class ServiceGPS extends Service {

    private LocationManager locationManager;
    private int routeID;
//    private final RoutesMethods routesMethods = new RoutesMethods();
    private NotificationCompat.Builder notification;

    private final Database database = new Database(ServiceGPS.this);

    private SharedPreferences sharedPreferences;
    private SharedPreferences defaultSharedPreferences;

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("GPS_LC", "Location is Changed");
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            double elevation = round(location.getAltitude() * 10.0) / 10.0;
            double time = location.getTime();
            double speed = location.getSpeed();
            double hacc = location.getAccuracy();
            double course = location.getBearing();
            double vacc = -1;
            if (Build.VERSION.SDK_INT >= 26) {
                vacc = location.getVerticalAccuracyMeters();
            }

            Log.d("GPS_LC_TIME", location.getTime() + "");

            Point point = new Point(routeID, database.getLastPointID(routeID) + 1,
                    latitude, longitude, elevation, time, speed, hacc, course, vacc);

            if (sharedPreferences.getBoolean(getString(R.string.pausePref), false)) {
                point.setPaused(true);
                database.setPause(routeID, database.getLastPointID(routeID));
            } else {
                int minHorizontal = Integer.parseInt(defaultSharedPreferences.getString(getString(R.string.horizontalPref), "20"));
                int minVertical = Integer.parseInt(defaultSharedPreferences.getString(getString(R.string.verticalPref), "15"));

                minHorizontal = max(minHorizontal, 4);
                minVertical = max(minVertical, 4);

                if (point.getHacc() <= minHorizontal && point.getVacc() <= minVertical) {
                    database.addPoint(point);
                    // send coordinates to record activity to write lines on map
                    Intent intent = new Intent(getString(R.string.intentExtra));
                    intent.putExtra(getString(R.string.intentExtra), latitude + "," + longitude);
                    sendBroadcast(intent);
                }
            }

            // when location is changed notification is updated
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notification.setContentText(Math.round(RoutesMethods.getDistance(database.getPoints(routeID)) / 10) / 100.0 + " km ");
            if (notificationManager != null)
                notificationManager.notify(1, notification.build());

            Log.d("GPS_LC", "Write Location to: " + routeID);
        }

        //this method is deprecated in API level 29. This callback will never be invoked on Android Q and above.

        /** @deprecated in API level 29 */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //Log.d("GPS_LC", "GPS Status Changed");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d("GPS_LC", "GPS Provider enable");
        }

        @Override
        public void onProviderDisabled(String provider) {
            //Log.d("GPS_LC", "GPS Provider disable");
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("GPS_LC", "GPS onCreate");
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("GPS_LC", "onStartCommand");

        routeID = intent.getIntExtra(getString(R.string.intentExtra), 1);
        sharedPreferences = getSharedPreferences(getString(R.string.sharedPreferences), MODE_PRIVATE);
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // pending intent for click on notification
        Intent notificationIntent = new Intent(this, RecordActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        // creates broadcast for notification button
        Intent broadCastIntent = new Intent(this, NotificationReceiver.class);
        broadCastIntent.putExtra(getString(R.string.intentExtra), "stop this");
        PendingIntent actionIntent = PendingIntent.getBroadcast(this, 0, broadCastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.d("GPS_LC", routeID + "");

        // creates notification
        notification = new NotificationCompat.Builder(this, getString(R.string.chanelID)).setContentTitle("GPS Tracking is running").setContentText("0.0 km").setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent).setOnlyAlertOnce(true).setColor(Color.RED).addAction(R.mipmap.ic_launcher, "Stop", actionIntent).setUsesChronometer(true);

        startForeground(1, notification.build());


        Log.d("GPS_LC", "Creating new Route: " + routeID);
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        int minDistance = Integer.parseInt(defaultSharedPreferences.getString(getString(R.string.distanceIntervalPref), "10"));
        int minTime = Integer.parseInt(defaultSharedPreferences.getString(getString(R.string.timeIntervalPref), "4")) * 1000;

        minDistance = min(minDistance, 50);
        minTime = min(minTime, 30 * 1000);

        minDistance = max(minDistance, 5);
        minTime = max(minTime, 1000);

        //missing permission check, it is not needed, because app cannot run without getting permission in main
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locationListener);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("GPS_LC", "GPS Service Location Destroyed");
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
        database.updateActivity(routeID, 0, System.currentTimeMillis(), "");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(getString(R.string.pausePref), false);
        editor.apply();
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("GPS_LC", "GPS Bind");
        return null;
    }

}
