package com.example.sportstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

    private ArrayList<String> dataArrayList = new ArrayList<>();
    private RoutesMethods routesMethods = new RoutesMethods();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        TextView distance = findViewById(R.id.textView3);
        TextView time = findViewById(R.id.textView4);
        TextView activityCount = findViewById(R.id.textView2);
        TextView elevationGain = findViewById(R.id.textView5);

        dataArrayList = routesMethods.loadData(getApplicationContext());

        double distanceD = 0;
        double timeD = 0;
        double elevationGainD = 0;

        String name;
        for (int i = 0; i < dataArrayList.size(); ++i)
        {
            String[] tokens = dataArrayList.get(i).split(",");
            name = tokens[0];
            double distancePartial = routesMethods.getDistance(Integer.parseInt(name), getApplicationContext());
            double timePartial = routesMethods.getTime(Integer.parseInt(name), getApplicationContext());
            double elevationGainPartial = routesMethods.getElevationGain(Integer.parseInt(name), getApplicationContext());
            distanceD += distancePartial;
            timeD += timePartial;
            elevationGainD += elevationGainPartial;
        }

        int hours = (int)timeD;
        double minutesD = (timeD - hours) * 60.0;
        int minutes = (int)minutesD;

        distance.setText(round(distanceD) +  " " + getString(R.string.km));
        time.setText(getString(R.string.time_data_stats, hours, minutes));
        elevationGain.setText(getString(R.string.metres, round(elevationGainD)));
        activityCount.setText(getString(R.string.activitiesCount, dataArrayList.size()));



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_stats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item1)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(StatsActivity.this);
            builder.setMessage("Delete all activities?").setCancelable(true).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteAll();
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

    private void deleteAll() {
        dataArrayList = routesMethods.loadData(getApplicationContext());
        String name;
        for (int i = 0; i < dataArrayList.size(); i++)
        {
            String[] tokens = dataArrayList.get(i).split(",");
            deleteFile(tokens[0] + ".txt");
        }
        deleteFile( "data.txt");
        dataArrayList.clear();
    }
}
