package com.example.sportstracker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;


public class DashboardFragment extends Fragment implements View.OnClickListener {

    private CardView recordCard, activitiesCard, statsCard;

    private int routeID = 0;
    private boolean newActivity = true;
    private SharedPreferences sharedPreferences;

    private Database database;

    private int activityType = 1;


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

        sharedPreferences = this.getActivity().getSharedPreferences(getString(R.string.sharedPreferences), getContext().MODE_PRIVATE);
        newActivity = sharedPreferences.getBoolean(getString(R.string.recordingPref), true);
        routeID = sharedPreferences.getInt(getString(R.string.routeNamePref), 0);

        database = new Database(getContext());

        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.record_card:
                if (ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    newActivity = sharedPreferences.getBoolean(getString(R.string.recordingPref), true);
                    if (newActivity) {
                        createActivityTypeDialog();
                    } else {
                        openMap();
                    }
                }
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
        newActivity = sharedPreferences.getBoolean(getString(R.string.recordingPref), true);
        if (newActivity) {

            double time = System.currentTimeMillis();
            Activity activity = new Activity(activityType, time);

            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            boolean autoPause = defaultSharedPreferences.getBoolean(getString(R.string.autoPausePref),true);
            activity.setAutoPause(autoPause);

            database.createActivity(activity);

            newActivity = false;
            routeID = database.getLastActivityID();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.recordingPref), newActivity);
            editor.putInt(getString(R.string.routeNamePref), routeID);
            editor.apply();

            Intent intent = new Intent(view.getContext(), ServiceGPS.class);
            intent.putExtra(getString(R.string.intentExtra), routeID);
            getContext().startService(intent);

            Log.d("RECORD_LC", "Starting new Service GPS");
        }
        openMap();
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

//        activityType = 1;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Activity Type").setSingleChoiceItems(types, activityType - 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activityType = which + 1;
            }
        }).setPositiveButton("START", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                record(getView());
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void openMap() {
        Intent intent = new Intent(getContext(), RecordActivity.class);
        startActivity(intent);
    }


}
