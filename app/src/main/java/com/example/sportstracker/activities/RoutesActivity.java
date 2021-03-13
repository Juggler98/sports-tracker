package com.example.sportstracker.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.sportstracker.data.Database;
import com.example.sportstracker.R;
import com.example.sportstracker.data.Route;
import com.example.sportstracker.adapters.RouteAdapter;
import com.example.sportstracker.data.RouteItem;
import com.example.sportstracker.RoutesMethods;

import java.util.ArrayList;


/**
 * This activity show all recorder routes. Click for more detail and long click for delete activity.
 */
public class RoutesActivity extends AppCompatActivity {

    private RoutesMethods routesMethods = new RoutesMethods();
    private Database database;

    private ListView listView;

    private ArrayList<String> arrayListListView = new ArrayList<>();
    private ArrayList<Route> activities = new ArrayList<>();

    private RecyclerView recyclerView;
    private RouteAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<RouteItem> routeItemsList = new ArrayList<>();

    private int activityOpen;

    SharedPreferences sharedPreferences;
    private final String SORT_BY = "sortByPref";
    private final String REVERSE = "reversePref";

    private int sortBy = 0;
    private boolean reverse = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
        Log.d("Routes_LC", "onCreate Routes");

        sharedPreferences = getSharedPreferences(getString(R.string.sharedPreferences), MODE_PRIVATE);

        database = new Database(RoutesActivity.this);
        activities = database.getActivities();

        sortRoutes(activities);

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
            sortRoutes(activities);
            if (activities.size() < oldSize) {
                routeItemsList.remove(activityOpen);
                adapter.notifyItemRemoved(activityOpen);
            } else if (activities.size() > oldSize) {
//                Activity activity = activities.get(activities.size()-1);
//                routeItemsList.add(new RouteItem(getIcon(activity.getIdType()), routesMethods.getDate(activity.getTimeStart()), activity.getTitle()));
//                adapter.notifyItemInserted(routeItemsList.size() - 1 - 5);
            } else {
                routeItemsList.get(activityOpen).setTitle(activities.get(activityOpen).getTitle());
                routeItemsList.get(activityOpen).setIcon(routesMethods.getIcon(activities.get(activityOpen).getIdType()));
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

//    private void loadListView() {
//        arrayListListView.clear();
//        for (int i = 0; i < activities.size(); ++i) {
//            Activity activity = activities.get(i);
//            if (activity.getTitle().equals("")) {
//                arrayListListView.add((i + 1) + ". " + routesMethods.getDate(activity.getTimeStart()));
//            } else {
//                arrayListListView.add((i + 1) + ". " + activity.getTitle());
//            }
//        }
//    }

    private void reloadRouteItemsList() {
        routeItemsList.clear();
        for (Route route : activities) {
            routeItemsList.add(new RouteItem(routesMethods.getIcon(route.getIdType()), routesMethods.getDate(route.getTimeStart()), route.getTitle()));
        }
    }

    private void openStats(int activityID) {
        Intent intent = new Intent(this, RouteInfoActivity.class);
        intent.putExtra(getString(R.string.intentExtra), activityID);
        startActivity(intent);
    }

    //Insertion Sort
    //https://stackabuse.com/insertion-sort-in-java/
    private void sortRoutes(ArrayList<Route> sortingList) {
        sortBy = sharedPreferences.getInt(SORT_BY, 0);
        reverse = sharedPreferences.getBoolean(REVERSE, false);
        for (int i = 1; i < sortingList.size(); i++) {
            Route current = sortingList.get(i);
            int j = i - 1;
            while (j > -1 && this.compare(sortingList.get(j), current)) {
                sortingList.set(j + 1, sortingList.get(j));
                j--;
            }
            sortingList.set(j + 1, current);
        }
        reloadRouteItemsList();
        if (adapter != null && !sharedPreferences.getBoolean(getString(R.string.reloadPref), false))
            adapter.notifyDataSetChanged();
    }

    private boolean compare(Route route1, Route route2) {
        if (sortBy == 0) {
            return Double.compare(route1.getTimeStart(), route2.getTimeStart()) == (reverse ? -1 : 1);
        } else {
            return Integer.compare(route1.getIdType(), route2.getIdType()) == (reverse ? -1 : 1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.routes_menu, menu);
        if (sortBy == 0) {
            menu.findItem(R.id.sortDate).setChecked(true);
        } else {
            menu.findItem(R.id.sortType).setChecked(true);
        }
        menu.getItem(1).setChecked(reverse);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        item.setChecked(!item.isChecked());
        switch (item.getItemId()) {
            case R.id.sortDate:
                editor.putInt(SORT_BY, 0);
                break;
            case R.id.sortType:
                editor.putInt(SORT_BY, 1);
                break;
            case R.id.sortReverse:
                editor.putBoolean(REVERSE, item.isChecked());
                break;
        }
        editor.apply();
        this.sortRoutes(activities);
        return super.onOptionsItemSelected(item);
    }

}
