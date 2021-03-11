package com.example.sportstracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
        ArrayList<Activity> activities = database.getActivities();

        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);

        int activitiesCount = 0;
        double distanceD = 0;
        double timeD = 0;
        double elevationGainD = 0;

        int activitiesCountYear = 0;
        double distanceDYear = 0;
        double timeDYear = 0;
        double elevationGainDYear = 0;

        for (Activity activity : activities) {
            if (activity.getIdType() == sectionNumber || sectionNumber == 8) {
                int activityID = activity.getId();
                ArrayList<Point> points = database.getPoints(activityID);
                double distancePartial = routesMethods.getDistance(points);
                double timePartial = routesMethods.getHours(points, activity.getAutoPause())[1];
                double elevationGainPartial = routesMethods.getElevationGainLoss(points)[0];
                distanceD += distancePartial;
                timeD += timePartial;
                elevationGainD += elevationGainPartial;
                activitiesCount++;

                DateFormat format = new SimpleDateFormat("yyyy", Locale.getDefault());
                Date activityDate = new Date((long) activity.getTimeStart());
                String yearStr = format.format(activityDate);
                int activityYear = Integer.parseInt(yearStr);

                Date actualDate = new Date(System.currentTimeMillis());
                yearStr = format.format(actualDate);
                int actualYear = Integer.parseInt(yearStr);

                if (activityYear == actualYear) {
                    double distancePartialYear = routesMethods.getDistance(points);
                    double timePartialYear = routesMethods.getHours(points, activity.getAutoPause())[1];
                    double elevationGainPartialYear = routesMethods.getElevationGainLoss(points)[0];
                    distanceDYear += distancePartialYear;
                    timeDYear += timePartialYear;
                    elevationGainDYear += elevationGainPartialYear;
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

        activityCount.setText(getString(R.string.activitiesCount, activitiesCountYear));
        distance.setText(getString(R.string.distance_stats,Math.round(distanceDYear/1000.0)));
        time.setText(getString(R.string.time_data_stats, hoursYear, minutesYear));

        if (sectionNumber != 4) {
            elevationGain.setText(getString(R.string.metres, Math.round(elevationGainDYear)));
            elevationGainAll.setText(getString(R.string.metres, Math.round(elevationGainD)));
        } else {
            elevationGain.setText(getString(R.string.metres, 0));
            elevationGainAll.setText(getString(R.string.metres, 0));
        }

        activityCountAll.setText(getString(R.string.activitiesCount, activitiesCount));
        distanceAll.setText(getString(R.string.distance_stats,Math.round(distanceD/1000.0)));
        timeAll.setText(getString(R.string.time_data_stats, hours, minutes));


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