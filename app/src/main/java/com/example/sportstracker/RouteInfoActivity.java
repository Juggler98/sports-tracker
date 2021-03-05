package com.example.sportstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.example.sportstracker.MainActivity.EXTRA;
import static com.example.sportstracker.MainActivity.SHARED_PREFERENCES;
import static java.lang.Math.max;
import static java.lang.Math.round;

/**
 * Activity for showing route info.
 */
public class RouteInfoActivity extends AppCompatActivity implements OnMapReadyCallback, RenameDialog.RenameDialogListener {

    private RoutesMethods routesMethods = new RoutesMethods();

    private TextView avgSpeed;
    private TextView avgInfo;
    private MapView mapView;

    private ArrayList<LatLng> latLngArrayList = new ArrayList<>();
    /**
     * IS_RELOAD_NEEDED - value data for shared preferences to indicate if listView in Route activity is needed
     */
    public static final String IS_RELOAD_NEEDED = "isReloadNeeded";

    public static final String ROUTE_NAME = "routeName";

    private SharedPreferences sharedPreferences;

    private int routeID;
    private boolean avgVsPace;
    private double avg;

    private Database database;

    private Activity route;

    private int routeType;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routeinfo);

        database = new Database(RouteInfoActivity.this);

        avgInfo = findViewById(R.id.avgInfo);
        avgSpeed = findViewById(R.id.avgSpeed);

        TextView dateView = findViewById(R.id.date);
        TextView distance = findViewById(R.id.distance);
        TextView time = findViewById(R.id.time);

        TextView elevationGain = findViewById(R.id.elevationGain);
        TextView elevationLoss = findViewById(R.id.elevationLoss);
        TextView maxAltitude = findViewById(R.id.maxAltitude);
        TextView minAltitude = findViewById(R.id.minAltitude);
        TextView maxSpeed = findViewById(R.id.maxSpeed);

        mapView = findViewById(R.id.mapView);

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(IS_RELOAD_NEEDED);
        editor.apply();

        routeID = getIntent().getIntExtra(EXTRA, 0);

        Log.d("RouteInfo_LC", "onCreate Route: " + routeID);

        route = database.getActivity(routeID);
        ArrayList<Point> points = database.getPoints(routeID);

        setIcon();

        String name = "";
        if (route != null)
            name = route.getTitle();
        if (name.equals(""))
            name = "Details";

        getSupportActionBar().setTitle(name);

        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date((long)route.getTimeStart());
        String timeStr = format.format(date);

        dateView.setText(timeStr);

        double distanceD = routesMethods.getDistance(points);
        distance.setText(distanceD + " " + getString(R.string.km));

        // calculate hours minutes and seconds from hours
        double hoursD = routesMethods.getHours(points);
        int hours = (int) hoursD;
        double minutesD = (hoursD - hours) * 60.0;
        int minutes = (int) minutesD;
        double secondsD = (minutesD - minutes) * 60.0;
        int seconds = (int) secondsD;

        String minutesStr = minutes < 10 ? "0" + minutes : "" + minutes;
        String secondsStr = seconds < 10 ? "0" + seconds : "" + seconds;

        time.setText(getString(R.string.time_data, hours, minutesStr, secondsStr));
        elevationGain.setText(getString(R.string.metres, (int) routesMethods.getElevationGainLoss(points)[0]));
        elevationLoss.setText("-" + getString(R.string.metres, (int) routesMethods.getElevationGainLoss(points)[1]));

        maxAltitude.setText(routesMethods.getAltitudeMaxMin(points)[0] + " m");
        minAltitude.setText(routesMethods.getAltitudeMaxMin(points)[1] + " m");

        double maxSpeedDouble = round(routesMethods.getMaxSpeed(points) * 3.6 * 10)/10.0;
        maxSpeed.setText(maxSpeedDouble + " km/h");

        if (hoursD == 0) {
            this.avg = 0.0;
        } else {
            this.avg = round(distanceD / hoursD * 100.0) / 100.0;
        }


        avgSpeed.setText(avg + " " + getString(R.string.kmh));

        avgVsPace = true;

        avgSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeAvgPace();
            }
        });

        avgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeAvgPace();
            }
        });

        latLngArrayList = routesMethods.getLatLng(database.getPoints(routeID));

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:

                return true;
            case R.id.item2:
                createActivityTypeDialog();
                return true;
            case R.id.item3:
                // rename activity
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(ROUTE_NAME, route.getTitle());
                editor.apply();
                openDialog();
                return true;
            case R.id.item4:
                AlertDialog.Builder builder = new AlertDialog.Builder(RouteInfoActivity.this);
                builder.setMessage("Delete this activity?").setCancelable(true).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete activity
                        database.deleteActivity(routeID);
                        setReloadIsNeeded();
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (!latLngArrayList.isEmpty()) {
            // show map of recorded route
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngArrayList.get(latLngArrayList.size() / 4), 13));
            googleMap.addMarker(new MarkerOptions().position(latLngArrayList.get(0)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            googleMap.addMarker(new MarkerOptions().position(latLngArrayList.get(latLngArrayList.size() - 1)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            googleMap.addPolyline(new PolylineOptions().addAll(latLngArrayList).color(Color.RED));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    public void openDialog() {
        RenameDialog renameDialog = new RenameDialog();
        renameDialog.show(getSupportFragmentManager(), "rename");
    }

    private void rename(String name) {
        database.updateActivity(routeID, 0, 0.0, name);
        route = database.getActivity(routeID);
        name = name.equals("") ? "Details" : name;
        getSupportActionBar().setTitle(name);
        Log.d("RouteInfo_LC", "Renaming: " + routeID + " " + name);
        setReloadIsNeeded();
    }

    // if click on avg speed it changed to avg pace and opposite
    @SuppressLint("SetTextI18n")
    private void changeAvgPace() {
        if (avgVsPace) {
            avgInfo.setText(getString(R.string.avg_pace));
            if (avg == 0) {
                avgSpeed.setText(getString(R.string.avg_pace_null));
            } else {
                double minutesD = 60 / avg;
                int minutes = (int) minutesD;
                double secondsD = (minutesD - minutes) * 60.0;
                int seconds = (int) secondsD;
                String secondsStr = seconds < 10 ? "0" + seconds : "" + seconds;
                avgSpeed.setText(getString(R.string.avgPace_data, minutes, secondsStr));
            }
        } else {
            avgInfo.setText(getString(R.string.avg_speed));
            avgSpeed.setText(avg + " " + getString(R.string.kmh));
        }
        avgVsPace = !avgVsPace;
    }

    @Override
    public void applyText(String name) {
        rename(name);
    }

    private void createActivityTypeDialog() {
        int type = 1;
        String typeStr;
        ArrayList<String> arrayList = new ArrayList<>();
        do {
            typeStr = database.getType(type++);
            if (!typeStr.equals("")) {
                arrayList.add(typeStr);
            }
        } while (!typeStr.equals(""));

        String[] types = new String[arrayList.size()];
        types = arrayList.toArray(types);
        routeType = route.getIdType();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Activity Type").setSingleChoiceItems(types, routeType - 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                routeType = which + 1;
            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (routeType != route.getIdType()) {
                    database.updateActivity(routeID, routeType, 0.0, "");
                    route = database.getActivity(routeID);
                    setIcon();
                    setReloadIsNeeded();
                }
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void setIcon() {
        int icon = R.drawable.ic_hike;
        ImageView imageView = findViewById(R.id.icon);
        switch (route.getIdType()) {
            case 1:
                icon = R.drawable.ic_hike;
                break;
            case 2:
                icon = R.drawable.ic_bike;
                break;
            case 3:
                icon = R.drawable.ic_run;
                break;
            case 4:
                icon = R.drawable.ic_swim;
                break;
            case 5:
                icon = R.drawable.ic_ski;
                break;
            case 6:
                icon = R.drawable.ic_walk;
                break;
            case 7:
                icon = R.drawable.ic_skate;
                break;
        }
        imageView.setImageResource(icon);
    }

    private void setReloadIsNeeded() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_RELOAD_NEEDED, true);
        editor.apply();
    }

}
