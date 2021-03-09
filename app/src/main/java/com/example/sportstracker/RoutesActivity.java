package com.example.sportstracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;



/**
 * This activity show all recorder routes. Click for more detail and long click for delete activity.
 */
public class RoutesActivity extends AppCompatActivity {

    private RoutesMethods routesMethods = new RoutesMethods();
    private Database database;

    private ListView listView;

    private ArrayList<String> arrayListListView = new ArrayList<>();
    private ArrayList<Activity> activities = new ArrayList<>();

    private RecyclerView recyclerView;
    private RouteAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<RouteItem> routeItemsList = new ArrayList<>();

    private int activityOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
        Log.d("Routes_LC", "onCreate Routes");

        database = new Database(RoutesActivity.this);
        activities = database.getActivities();

        for (Activity activity : activities) {
            routeItemsList.add(new RouteItem(getIcon(activity.getIdType()), routesMethods.getDate(activity.getTimeStart()), activity.getTitle()));
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new RouteAdapter(routeItemsList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RouteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                openStats(activities.get(position).getId());
                activityOpen = position;
            }
        });

//        listView = findViewById(R.id.listView);
//        loadListView();
//        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, activities));
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                openStats(activities.get(position).getId());
//            }
//        });

//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//                final int positionFinal = position;
//                AlertDialog.Builder builder = new AlertDialog.Builder(RoutesActivity.this);
//                builder.setMessage("Delete this activity?").setCancelable(true).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        database.deleteActivity(activities.get(position).getId());
//                        activities = database.getActivities();
//                        loadListView();
//                        listView.setAdapter(new ArrayAdapter<>(RoutesActivity.this, android.R.layout.simple_list_item_1, arrayListListView));
//                    }
//                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//                AlertDialog alertDialog = builder.create();
//                alertDialog.show();
//
//                return true;
//            }
//        });


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
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.sharedPreferences), MODE_PRIVATE);
        boolean isReloadNeeded = sharedPreferences.getBoolean(getString(R.string.reloadPref), false);
        int oldSize = activities.size();
        if (isReloadNeeded) {
            activities = database.getActivities();
            if (activities.size() < oldSize) {
                routeItemsList.remove(activityOpen);
                adapter.notifyItemRemoved(activityOpen);
            } else if (activities.size() > oldSize){
//                Activity activity = activities.get(activities.size()-1);
//                routeItemsList.add(new RouteItem(getIcon(activity.getIdType()), routesMethods.getDate(activity.getTimeStart()), activity.getTitle()));
//                adapter.notifyItemInserted(routeItemsList.size() - 1 - 5);
            } else {
                routeItemsList.get(activityOpen).setTitle(activities.get(activityOpen).getTitle());
                routeItemsList.get(activityOpen).setIcon(getIcon(activities.get(activityOpen).getIdType()));
                adapter.notifyItemChanged(activityOpen);
//                Log.d("Routes_LC", "onResume Routes " + activityOpen);
            }
//            loadListView();
//            listView.setAdapter(new ArrayAdapter<>(RoutesActivity.this, android.R.layout.simple_list_item_1, arrayListListView));
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.reloadPref), false);
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
        intent.putExtra(getString(R.string.intentExtra), activityID);
        startActivity(intent);
    }

    private int getIcon(int type) {
        switch (type) {
            case 1:
                return R.drawable.ic_hike;
            case 2:
                return R.drawable.ic_bike;
            case 3:
                return R.drawable.ic_run;
            case 4:
                return R.drawable.ic_swim;
            case 5:
                return R.drawable.ic_ski;
            case 6:
                return R.drawable.ic_walk;
            case 7:
                return R.drawable.ic_skate;
            default:
                return R.drawable.ic_hike;
        }
    }
}
