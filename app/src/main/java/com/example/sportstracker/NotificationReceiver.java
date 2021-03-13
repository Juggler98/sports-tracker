package com.example.sportstracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 *  Broadcast receiver to receive if button stop was clicked on notification.
 */
public class NotificationReceiver extends BroadcastReceiver {

    /**
     * If stop was clicked Service GPS is stopped.
     *
     * @param context application context
     * @param intent application intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NotificationReceiver_LC", "OnReceive");
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.sharedPreferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.recordingPref), true);
        editor.remove(context.getString(R.string.routeNamePref));
        editor.apply();

        Intent intent1 = new Intent(context, ServiceGPS.class);
        context.stopService(intent1);
    }
}
