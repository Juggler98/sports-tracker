package com.belsoft.sportstracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.belsoft.sportstracker.data.Database;
import com.belsoft.sportstracker.data.Point;
import com.belsoft.sportstracker.R;
import com.belsoft.sportstracker.data.Route;
import com.belsoft.sportstracker.RoutesMethods;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class StatsTabFragment extends Fragment {

    private static final String PAGE_NUMBER = "pageNumber";

    public static StatsTabFragment newInstance(int index) {
        StatsTabFragment fragment = new StatsTabFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(PAGE_NUMBER, index);
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
//        RoutesMethods routesMethods = new RoutesMethods();
        ArrayList<Route> activities = database.getActivities();

        int sectionNumber = 1;
        if (getArguments() != null)
            sectionNumber = getArguments().getInt(PAGE_NUMBER);

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
                double distancePartial = RoutesMethods.getDistance(points);
                double timePartial = RoutesMethods.getHours(points, route.getAutoPause())[1];
                double elevationGainPartial = 0;
                if (route.getIdType() != 4) {
                    elevationGainPartial = RoutesMethods.getElevationGainLoss(points)[0];
                }
                distanceD += distancePartial;
                timeD += timePartial;
                elevationGainD += elevationGainPartial;
                activitiesCount++;

                int activityYear = Integer.parseInt(RoutesMethods.getDate(route.getTimeStart(), "yyyy"));
                int actualYear = Integer.parseInt(RoutesMethods.getDate(System.currentTimeMillis(), "yyyy"));

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
            elevationGain.setText("-");
            elevationGainAll.setText("-");
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