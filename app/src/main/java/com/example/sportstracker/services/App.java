package com.example.sportstracker.services;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import com.example.sportstracker.R;

/**
 * Creates Notification channel for android API >= 26.
 */
public class App extends Application {

    /**
     * Channel ID which using foreground service
     */
    //public static final String CHANNEL_ID = "ChannelID";
    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel(getString(R.string.chanelID), "Notification Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(notificationChannel);
            Log.d("GPS_LC", "New Channel");
        }

    }
}