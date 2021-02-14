package com.example.sportstracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


import static com.example.sportstracker.MainActivity.EXTRA;
import static com.example.sportstracker.MainActivity.SHARED_PREFERENCES;
import static com.example.sportstracker.RouteInfoActivity.IS_RELOAD_NEEDED;

/**
 * This activity show all recorder routes. Click for more detail and long click for delete activity.
 */
public class RoutesActivity extends AppCompatActivity {

    private RoutesMethods routesMethods = new RoutesMethods();

    private ListView listView;

    private ArrayList<String> arrayListListView = new ArrayList<>();
    private ArrayList<Activity> activities = new ArrayList<>();

    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        database = new Database(RoutesActivity.this);
        activities = database.getActivities();


//        final RoutesMethods routesMethods = new RoutesMethods();

        Log.d("Routes_LC", "onCreate Routes");

        listView = findViewById(R.id.listView);

        loadListView();
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayListListView));

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final int positionFinal = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(RoutesActivity.this);
                builder.setMessage("Delete this activity?").setCancelable(true).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        database.deleteActivity(activities.get(position).getId());
                        activities = database.getActivities();
                        loadListView();
                        listView.setAdapter(new ArrayAdapter<>(RoutesActivity.this, android.R.layout.simple_list_item_1, arrayListListView));
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
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openStats(activities.get(position).getId());
            }
        });

        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    protected void onStart() {
        Log.d("Routes_LC", "onStart Routes");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Routes_LC", "onPause Routes");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Routes_LC", "onResume Routes");
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        boolean isReloadNeeded = sharedPreferences.getBoolean(IS_RELOAD_NEEDED, false);
        if (isReloadNeeded) {
            activities = database.getActivities();
            loadListView();
            listView.setAdapter(new ArrayAdapter<>(RoutesActivity.this, android.R.layout.simple_list_item_1, arrayListListView));
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(IS_RELOAD_NEEDED, false);
            editor.apply();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Routes_LC", "onDestroy Routes");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Routes_LC", "onStop Routes");
    }

    private void loadListView() {
        arrayListListView.clear();
        for (int i = 0; i < activities.size(); ++i) {
            Activity activity = activities.get(i);
            if (activity.getTitle().equals("")) {
                arrayListListView.add((i + 1) + ". " + routesMethods.getDate(activity.getTimeStart()));
            } else {
                arrayListListView.add((i + 1) + ". " + activity.getTitle());
            }
        }
    }

    private void openStats(int activityID) {
        Intent intent = new Intent(this, RouteInfoActivity.class);
        intent.putExtra(EXTRA, activityID);
        startActivity(intent);
    }
}
