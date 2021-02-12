package com.example.sportstracker;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.sportstracker.App.CHANNEL_ID;
import static com.example.sportstracker.MainActivity.EXTRA;
import static java.lang.Math.round;


/**
 * Creates foreground service with location manager to get location change.
 */
public class ServiceGPS extends Service {

    private LocationManager locationManager;
    private int routeID;
    private RoutesMethods routesMethods = new RoutesMethods();
    private NotificationCompat.Builder notification;

    private Database database = new Database(ServiceGPS.this);


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("GPS_LC", "GPS onCreate");
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("GPS_LC", "Location is Changed");
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            double elevation = round(location.getAltitude() * 10.0) / 10.0;
            double time = location.getTime();
            double speed = location.getSpeed();
            double hdop = location.getAccuracy();
            double vdop = 0.0;
            if (Build.VERSION.SDK_INT >= 26) {
                vdop = location.getVerticalAccuracyMeters();
            }
            double course = location.getBearing();

            Log.d("GPS_LC_TIME",location.getTime() + "");

//            DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
//            Date date = new Date(location.getTime());
//            String time = format.format(date);

//            String data = latitude + "," + longitude + "," + elevation + "," + time + "\n";

            Point point = new Point(routeID, database.getLastPointID(routeID) + 1,
                    latitude, longitude, elevation, time, speed, hdop, vdop, course);
//            routesMethods.write(routeID, data, getApplicationContext());
            database.addPoint(point);

            // when location is changed notification is updated
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notification.setContentText(routesMethods.getDistance(routeID, getApplicationContext()) + " km ");
            notification.setContentText(database.getDistance(routeID) + " km ");
            notificationManager.notify(1, notification.build());

            // send coordinates to record activity to write lines on map
            Intent intent = new Intent(EXTRA);
            intent.putExtra(EXTRA, latitude + "," + longitude);
            sendBroadcast(intent);

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
            //Log.d("GPS_LC", "GPS Provider enable");
        }

        @Override
        public void onProviderDisabled(String provider) {
            //Log.d("GPS_LC", "GPS Provider disable");
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("GPS_LC", "GPS Service Location Destroyed");
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
        database.updateActivity(routeID,0, System.currentTimeMillis(), "");
        stopSelf();
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("GPS_LC", "onStartCommand");

        routeID = intent.getIntExtra(EXTRA,1);

        // pending intent for click on notification
        Intent notificationIntent = new Intent(this, RecordActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        // creates broadcast for notification button
        Intent broadCastIntent = new Intent(this, NotificationReceiver.class);
        broadCastIntent.putExtra(EXTRA,"stop this");
        PendingIntent actionIntent = PendingIntent.getBroadcast(this, 0,broadCastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

//        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
//        Date date = new Date();
//        String time = format.format(date);

        Log.d("GPS_LC",  routeID + " ");

        // creates notification
        notification = new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("GPS Tracking is running").setContentText("0.0 km").setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent).setOnlyAlertOnce(true).setColor(Color.RED).addAction(R.mipmap.ic_launcher, "Stop",actionIntent).setUsesChronometer(true);

        startForeground(1, notification.build());

//        routesMethods.write("data", routeID + "," + time + "," + "" + "\n", getApplicationContext());

        Log.d("GPS_LC", "Creating new Route: " + routeID);
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        //missing permission check, it is not needed, because app will be closed without getting permission in main
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 4, locationListener);

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("GPS_LC", "GPS Bind");
        return null;
    }

}
