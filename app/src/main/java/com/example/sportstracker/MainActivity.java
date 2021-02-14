package com.example.sportstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * Entry activity for application.
 */
public class MainActivity extends AppCompatActivity {


    /**  EXTRA - key for intent over whole app */
    public static final String EXTRA = "extra";
    /** SHARED_PREFERENCES - key for Shared Preferences over whole app */
    public static final String SHARED_PREFERENCES = "sharedPreferences";
    /** RECORDING_PREF - value data for SharedPreferences to indicate if recording can be execute */
    public static final String RECORDING_PREF = "boolean";
    /** NAME_OF_ACTIVITY = value data for SharedPreferences to locate id of actual recording route */
    public static final String NAME_OF_ACTIVITY = "name";

    private Button buttonRecord;
    private int routeID = 0;
    private boolean newActivity = true;
    private SharedPreferences sharedPreferences;

    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(getString(R.string.title_activity_main));

        database = new Database(MainActivity.this);

        database.checkTypes();

        Log.d("MAIN_LC", "onCreate");

        buttonRecord = findViewById(R.id.button1);
        Button buttonActivities = findViewById(R.id.button2);
        Button buttonStats = findViewById(R.id.button3);

        // write this because I want to change something so I can tag this commit final. but I used tag final accidentally before to tag 2 commit. so I am using finall tag to make it work properly

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        newActivity = sharedPreferences.getBoolean(RECORDING_PREF, true);
        routeID = sharedPreferences.getInt(NAME_OF_ACTIVITY, 0);

        buttonStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StatsActivity.class);
                startActivity(intent);
            }
        });

        buttonActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RoutesActivity.class);
                startActivity(intent);
            }
        });

        // permit start tracking only with getting GPS permission
        if (!permission()) {
            enableRecording();
        }
    }

    // boolean new activity permit start GPS service only one time, until stop
    private void enableRecording() {
        buttonRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newActivity = sharedPreferences.getBoolean(RECORDING_PREF, true);
                if (newActivity) {

                    double time = System.currentTimeMillis();
                    Activity activity = new Activity(1, time);
                    database.createActivity(activity);

                    newActivity = false;
                    routeID = database.getLastActivityID();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(RECORDING_PREF, newActivity);
                    editor.putInt(NAME_OF_ACTIVITY, routeID);
                    editor.apply();

                    Intent intent = new Intent(getApplicationContext(), ServiceGPS.class);
                    intent.putExtra(EXTRA, routeID);
                    startService(intent);

                    Log.d("RECORD_LC", "Starting new Service GPS");
                }
                openMap();
            }
        });
    }

    private void openMap() {
        Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
        startActivity(intent);
    }

    // check and ask for permission
    private boolean permission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return true;
        }
        return false;
    }


    /**
     * Check if user permit GPS access. If yes, app will continue and he can start recording  and if not app is closed.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                enableRecording();
            } else {
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Open about app dialog if was item clicked successful
     * @param item which was clicked.
     * @return true if item was clicked
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item1) {
            AboutAppDialog aboutAppDialog = new AboutAppDialog();
            aboutAppDialog.show(getSupportFragmentManager(), "about app");
            Log.d("Main_LC", "About App");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
