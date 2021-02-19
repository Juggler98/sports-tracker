package com.example.sportstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

/**
 * Entry activity for application.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    /**  EXTRA - key for intent over whole app */
    public static final String EXTRA = "extra";
    /** SHARED_PREFERENCES - key for Shared Preferences over whole app */
    public static final String SHARED_PREFERENCES = "sharedPreferences";
    /** RECORDING_PREF - value data for SharedPreferences to indicate if recording can be execute */
    public static final String RECORDING_PREF = "boolean";
    /** NAME_OF_ACTIVITY = value data for SharedPreferences to locate id of actual recording route */
    public static final String NAME_OF_ACTIVITY = "name";

    public static final String PAUSE = "pause";


    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            getSupportActionBar().setTitle(getString(R.string.title_activity_main));
        } catch (Exception e) {
            Log.d("MAIN_LC", "Cannot set Title of Action Bar");
        }

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }

        database = new Database(MainActivity.this);

        database.checkTypes();

        Log.d("MAIN_LC", "onCreate");

        // permit start tracking only with getting GPS permission
        if (!permission()) {
            enableRecording();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d("Main_LC", "NavigationBar clicked");
        switch (item.getItemId()) {
            case R.id.nav_dashboard:
                getSupportActionBar().setTitle("Dashboard");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();
                break;
            case R.id.nav_map:
                getSupportActionBar().setTitle("Map");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapFragment()).commit();
                break;
            case R.id.nav_import:
                break;
            case R.id.nav_settings:
                break;
            case R.id.nav_info:
                AboutAppDialog aboutAppDialog = new AboutAppDialog();
                aboutAppDialog.show(getSupportFragmentManager(), "about app");
                Toast.makeText(this, "Dashboard", Toast.LENGTH_SHORT).show();

                Log.d("Main_LC", "About App");
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // boolean new activity permit start GPS service only one time, until stop
    private void enableRecording() {

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
