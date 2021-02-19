package com.example.sportstracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.example.sportstracker.MainActivity.EXTRA;
import static com.example.sportstracker.MainActivity.NAME_OF_ACTIVITY;
import static com.example.sportstracker.MainActivity.RECORDING_PREF;
import static com.example.sportstracker.MainActivity.SHARED_PREFERENCES;

public class DashboardFragment extends Fragment {



    private Button buttonRecord;
    private int routeID = 0;
    private boolean newActivity = true;
    private SharedPreferences sharedPreferences;

    private Database database;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dashboard_fragment, container, false);

        Button buttonActivities = view.findViewById(R.id.button2);
        Button buttonStats = view.findViewById(R.id.button3);
        Button buttonRecord = view.findViewById(R.id.button1);

        sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFERENCES, getContext().MODE_PRIVATE);
        newActivity = sharedPreferences.getBoolean(RECORDING_PREF, true);
        routeID = sharedPreferences.getInt(NAME_OF_ACTIVITY, 0);

        database = new Database(getContext());

        buttonStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), StatsActivity.class);
                startActivity(intent);
            }
        });

        buttonActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), RoutesActivity.class);
                startActivity(intent);
            }
        });

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

                    Intent intent = new Intent(view.getContext(), ServiceGPS.class);
                    intent.putExtra(EXTRA, routeID);
                    getContext().startService(intent);

                    Log.d("RECORD_LC", "Starting new Service GPS");
                }
                Intent intent = new Intent(getContext(), RecordActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }




}
