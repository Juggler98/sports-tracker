package com.example.sportstracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.sportstracker.data.Database;
import com.example.sportstracker.data.Point;
import com.example.sportstracker.R;
import com.example.sportstracker.data.Route;
import com.example.sportstracker.RoutesMethods;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.stats_fragment, container, false);
        final TextView activitiesName1 = root.findViewById(R.id.activitiesName);
        final TextView activitiesName2 = root.findViewById(R.id.activitiesAllTimeName);

        TextView activityCount = root.findViewById(R.id.activitiesCount);
        TextView distance = root.findViewById(R.id.distance);
        TextView time = root.findViewById(R.id.time);
        TextView elevationGain = root.findViewById(R.id.elevationGain);

        TextView activityCountAll = root.findViewById(R.id.activitiesAllTimeCount);
        TextView distanceAll = root.findViewById(R.id.distanceAllTime);
        TextView timeAll = root.findViewById(R.id.timeAllTime);
        TextView elevationGainAll = root.findViewById(R.id.elevationGainAllTime);

        Database database = new Database(getContext());
        RoutesMethods routesMethods = new RoutesMethods();
        ArrayList<Route> activities = database.getActivities();

        int sectionNumber = 1;
        if (getArguments() != null)
            sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);

        int activitiesCount = 0;
        double distanceD = 0;
        double timeD = 0;
        double elevationGainD = 0;

        int activitiesCountYear = 0;
        double distanceDYear = 0;
        double timeDYear = 0;
        double elevationGainDYear = 0;

        for (Route route : activities) {
            if (route.getIdType() == sectionNumber || sectionNumber == 8) {
                int activityID = route.getId();
                ArrayList<Point> points = database.getPoints(activityID);
                double distancePartial = routesMethods.getDistance(points);
                double timePartial = routesMethods.getHours(points, route.getAutoPause())[1];
                double elevationGainPartial = routesMethods.getElevationGainLoss(points)[0];
                distanceD += distancePartial;
                timeD += timePartial;
                elevationGainD += elevationGainPartial;
                activitiesCount++;

                int activityYear = Integer.parseInt(routesMethods.getDate(route.getTimeStart(), "yyyy"));
                int actualYear = Integer.parseInt(routesMethods.getDate(System.currentTimeMillis(), "yyyy"));

                if (activityYear == actualYear) {
                    distanceDYear += distancePartial;
                    timeDYear += timePartial;
                    elevationGainDYear += elevationGainPartial;
                    activitiesCountYear++;
                }
            }
        }

        int hours = (int) timeD;
        double minutesD = (timeD - hours) * 60.0;
        int minutes = (int) minutesD;

        int hoursYear = (int) timeDYear;
        double minutesDYear = (timeDYear - hoursYear) * 60.0;
        int minutesYear = (int) minutesDYear;

        activityCount.setText(getString(R.string.stats_count, activitiesCountYear));
        distance.setText(getString(R.string.stats_distance, Math.round(distanceDYear / 1000.0)));
        time.setText(getString(R.string.stats_time, hoursYear, minutesYear));

        if (sectionNumber != 4) {
            elevationGain.setText(getString(R.string.metres, Math.round(elevationGainDYear)));
            elevationGainAll.setText(getString(R.string.metres, Math.round(elevationGainD)));
        } else {
            elevationGain.setText(getString(R.string.metres, 0));
            elevationGainAll.setText(getString(R.string.metres, 0));
        }

        activityCountAll.setText(getString(R.string.stats_count, activitiesCount));
        distanceAll.setText(getString(R.string.stats_distance, Math.round(distanceD / 1000.0)));
        timeAll.setText(getString(R.string.stats_time, hours, minutes));


        String activityType = database.getType(sectionNumber);
        switch (activityType) {
            case "":
                activityType = "All Activities";
                break;
            case "Bike":
                activityType = "Rides";
                break;
            default:
                activityType = activityType + "s";
        }
        activitiesName1.setText(activityType);
        activitiesName2.setText(activityType);

        return root;
    }

}