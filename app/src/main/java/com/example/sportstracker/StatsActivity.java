package com.example.sportstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

import static java.lang.Math.round;

/**
 * Activity for showing global stats.
 */
public class StatsActivity extends AppCompatActivity {

    private RoutesMethods routesMethods = new RoutesMethods();
    private Database database;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);



        TextView distance = findViewById(R.id.textView3);
        TextView time = findViewById(R.id.textView4);
        TextView activityCount = findViewById(R.id.textView2);
        TextView elevationGain = findViewById(R.id.textView5);

        database = new Database(StatsActivity.this);
        ArrayList<Activity> activities = database.getActivities();

        double distanceD = 0;
        double timeD = 0;
        double elevationGainD = 0;

        for (int i = 0; i < activities.size(); ++i) {
            int activityID = activities.get(i).getId();
            ArrayList<Point> points = database.getPoints(activityID);
            double distancePartial = routesMethods.getDistance(points);
            double timePartial = routesMethods.getHours(points);
            double elevationGainPartial = routesMethods.getElevationGainLoss(points)[0];
            distanceD += distancePartial;
            timeD += timePartial;
            elevationGainD += elevationGainPartial;
        }

        int hours = (int) timeD;
        double minutesD = (timeD - hours) * 60.0;
        int minutes = (int) minutesD;

        distance.setText(round(distanceD) + " " + getString(R.string.km));
        time.setText(getString(R.string.time_data_stats, hours, minutes));
        elevationGain.setText(getString(R.string.metres, round(elevationGainD)));
        activityCount.setText(getString(R.string.activitiesCount, activities.size()));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_stats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(StatsActivity.this);
            builder.setMessage("Delete all activities?").setCancelable(true).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    database.deleteAll();
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

            Log.d("Stats_LC", "All Item deleted");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
