package com.example.sportstracker.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.sportstracker.data.Database;
import com.example.sportstracker.R;
import com.example.sportstracker.data.Route;
import com.example.sportstracker.services.ServiceGPS;
import com.example.sportstracker.activities.RecordActivity;
import com.example.sportstracker.activities.RoutesActivity;
import com.example.sportstracker.activities.StatsActivity;

public class DashboardFragment extends Fragment implements View.OnClickListener {

    private SharedPreferences defaultSharedPreferences;
    private SharedPreferences sharedPreferences;
    private boolean newRoute = true;
    private int routeType = 1;
    private int routeID = 0;

    private Database database;

    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dashboard_fragment, container, false);

        CardView recordCard, activitiesCard, statsCard;

        recordCard = view.findViewById(R.id.record_card);
        activitiesCard = view.findViewById(R.id.activities_card);
        statsCard = view.findViewById(R.id.stats_card);

        recordCard.setOnClickListener(this);
        activitiesCard.setOnClickListener(this);
        statsCard.setOnClickListener(this);

        context = requireContext();

        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences = context.getSharedPreferences(getString(R.string.sharedPreferences), Context.MODE_PRIVATE);
        newRoute = sharedPreferences.getBoolean(getString(R.string.recordingPref), true);
        routeID = sharedPreferences.getInt(getString(R.string.routeNamePref), 0);

        database = new Database(context);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        routeType = Integer.parseInt(defaultSharedPreferences.getString(getString(R.string.routeTypePref), "1"));
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.record_card:
                if (ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    newRoute = sharedPreferences.getBoolean(getString(R.string.recordingPref), true);
                    if (newRoute) {
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
        newRoute = sharedPreferences.getBoolean(getString(R.string.recordingPref), true);
        if (newRoute) {
            double time = System.currentTimeMillis();
            Route route = new Route(routeType, time);

            boolean autoPause = defaultSharedPreferences.getBoolean(getString(R.string.autoPausePref), true);
            route.setAutoPause(autoPause);

            database.createActivity(route);

            newRoute = false;
            routeID = database.getLastActivityID();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.recordingPref), newRoute);
            editor.putInt(getString(R.string.routeNamePref), routeID);
            editor.putBoolean(getString(R.string.pausePref), false);
            editor.apply();

            Intent intent = new Intent(view.getContext(), ServiceGPS.class);
            intent.putExtra(getString(R.string.intentExtra), routeID);
            context.startService(intent);

            Log.d("RECORD_LC", "Starting new Service GPS");
        }
        openMap();
    }

    private void createActivityTypeDialog() {
        String[] types = database.getTypes();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Activity Type").setSingleChoiceItems(types, routeType - 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                routeType = which + 1;
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
