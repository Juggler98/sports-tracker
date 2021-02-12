package com.example.sportstracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import static com.example.sportstracker.MainActivity.NAME_OF_ACTIVITY;
import static com.example.sportstracker.MainActivity.RECORDING_PREF;
import static com.example.sportstracker.MainActivity.SHARED_PREFERENCES;

/**
 *  Broadcast receiver to receive if button stop was clicked on notification.
 */
public class NotificationReceiver extends BroadcastReceiver {

    /**
     * If stop was clicked Service GPS is stopped.
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NotificationReceiver_LC", "OnReceive");
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(RECORDING_PREF, true);
        editor.remove(NAME_OF_ACTIVITY);
        editor.apply();

        Intent intent1 = new Intent(context, ServiceGPS.class);
        context.stopService(intent1);
    }
}
