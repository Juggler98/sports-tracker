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

//    private final RoutesMethods routesMethods = new RoutesMethods();
    private Database database;

    private final ArrayList<RouteItem> routeItemsList = new ArrayList<>();
    private ArrayList<Route> activities = new ArrayList<>();

    private RouteAdapter adapter;

    private SharedPreferences sharedPreferences;
    private final String SORT_BY = "sortByPref";
    private final String REVERSE = "reversePref";

    private int activityOpen = 0;
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

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new RouteAdapter(routeItemsList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RouteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                activityOpen = position;
                openStats(activities.get(activityOpen).getId());
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Routes_LC", "onStart Routes");
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
        if (isReloadNeeded) {
            int oldSize = activities.size();
            int newSize = database.getActivities().size();
            if (newSize == oldSize && oldSize > 0) {
                routeItemsList.get(activityOpen).setTitle(activities.get(activityOpen).getTitle());
                routeItemsList.get(activityOpen).setIcon(RoutesMethods.getIcon(activities.get(activityOpen).getIdType()));
                adapter.notifyItemChanged(activityOpen);
            } else if (newSize < oldSize) {
                routeItemsList.remove(activityOpen);
                adapter.notifyItemRemoved(activityOpen);
            }
            activities = database.getActivities();
            sortRoutes(activities);
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

    private void reloadRouteItemsList() {
        routeItemsList.clear();
        for (Route route : activities) {
            routeItemsList.add(new RouteItem(RoutesMethods.getIcon(route.getIdType()), RoutesMethods.getDate(route.getTimeStart(), "dd.MM.yyyy HH:mm"), route.getTitle()));
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
        for (int k = 0; k <= sortBy; k++) {
            for (int i = 1; i < sortingList.size(); i++) {
                Route current = sortingList.get(i);
                int j = i - 1;
                while (j > -1 && this.compare(sortingList.get(j), current, k)) {
                    sortingList.set(j + 1, sortingList.get(j));
                    j--;
                }
                sortingList.set(j + 1, current);
            }
        }
        reloadRouteItemsList();
        if (adapter != null && !sharedPreferences.getBoolean(getString(R.string.reloadPref), false))
            adapter.notifyDataSetChanged();
    }

    private boolean compare(Route route1, Route route2, int sortBy) {
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
