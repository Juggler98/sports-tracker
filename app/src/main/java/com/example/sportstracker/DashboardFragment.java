package com.example.sportstracker;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import static com.example.sportstracker.MainActivity.EXTRA;
import static com.example.sportstracker.MainActivity.NAME_OF_ACTIVITY;
import static com.example.sportstracker.MainActivity.RECORDING_PREF;
import static com.example.sportstracker.MainActivity.SHARED_PREFERENCES;

public class DashboardFragment extends Fragment implements View.OnClickListener {

    private CardView recordCard, activitiesCard, statsCard;

    private int routeID = 0;
    private boolean newActivity = true;
    private SharedPreferences sharedPreferences;

    private Database database;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dashboard_fragment, container, false);

        recordCard = view.findViewById(R.id.record_card);
        activitiesCard = view.findViewById(R.id.activities_card);
        statsCard = view.findViewById(R.id.stats_card);

        recordCard.setOnClickListener(this);
        activitiesCard.setOnClickListener(this);
        statsCard.setOnClickListener(this);

        sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFERENCES, getContext().MODE_PRIVATE);
        newActivity = sharedPreferences.getBoolean(RECORDING_PREF, true);
        routeID = sharedPreferences.getInt(NAME_OF_ACTIVITY, 0);

        database = new Database(getContext());

        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.record_card:
                if (ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    record(v);
                break;
            case R.id.activities_card:
                intent = new Intent(v.getContext(), RoutesActivity.class);
                startActivity(intent);
                break;
            case R.id.stats_card:
                intent = new Intent(v.getContext(), StatsActivity.class);
                startActivity(intent);
                break;

        }
    }

    // boolean new activity permit start GPS service only one time, until stop
    private void record(View view) {
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

            Intent intent = new Intent(view.getContext(), ServiceGPS.class);
            intent.putExtra(EXTRA, routeID);
            getContext().startService(intent);

            Log.d("RECORD_LC", "Starting new Service GPS");
        }
        Intent intent = new Intent(getContext(), RecordActivity.class);
        startActivity(intent);
    }

}
