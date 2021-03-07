package com.example.sportstracker;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static java.lang.Math.min;
import static java.lang.Math.round;

/**
 * Class for methods using over application.
 */
public class RoutesMethods {


    public RoutesMethods() {

    }

    /**
     * @param points
     * @return distance of route
     */
    public double getDistance(ArrayList<Point> points) {
        Log.d("DB_LC", "DB_getDistance");
        double lat1 = 0;
        double lat2 = 0;
        double lon1 = 0;
        double lon2 = 0;
        double distance = 0;
        Point point = null;
        Point pastPoint;
        for (int i = 0; i < points.size(); i++) {
            if (i != 0) {
                pastPoint = point;
                lat1 = pastPoint.getLat();
                lon1 = pastPoint.getLon();
                point = points.get(i);
                lat2 = point.getLat();
                lon2 = point.getLon();
                if (!pastPoint.getPaused())
                    distance += haversineFormula(lat1, lat2, lon1, lon2);
            } else {
                point = points.get(i);
            }
        }
        return round(distance / 10) / 100.0;
    }

    /**
     * @param points
     * @return elevation gain of route
     */
    public double[] getElevationGainLoss(ArrayList<Point> points) {
        double[] elevationGainLoss = new double[2];
        double ele1 = 0.0;
        double ele2 = 0.0;
        double elevationGain = 0.0;
        double elevationLoss = 0.0;
        double elevationDifference = 0.0;
        for (int i = 0; i < points.size(); i = i + 3) {
            Point point = points.get(i);
            if (i != 0 && point.getVdop() < 8) {
                ele2 = point.getEle();
                elevationDifference = ele2 - ele1;
                if (elevationDifference > 5) {
                    elevationGain += elevationDifference;
                } else if (elevationDifference < -5) {
                    elevationLoss += elevationDifference * -1;
                }
                ele1 = ele2;
            } else {
                ele1 = point.getEle();
            }
        }
        elevationGainLoss[0] = elevationGain;
        elevationGainLoss[1] = elevationLoss;
        return elevationGainLoss;
    }

    /**
     * @param points
     * @return min max altitue
     */
    public double[] getAltitudeMaxMin(ArrayList<Point> points) {
        double[] altitudeMaxMin = new double[2];
        double maxAltitude = Integer.MIN_VALUE;
        double minAltitude = Integer.MAX_VALUE;
        for (Point point : points) {
            if (point.getVdop() < 8) {
                if (point.getEle() > maxAltitude)
                    maxAltitude = point.getEle();
                if (point.getEle() < minAltitude)
                    minAltitude = point.getEle();
            }
        }
        altitudeMaxMin[0] = maxAltitude == Integer.MIN_VALUE ? 0 : maxAltitude;
        altitudeMaxMin[1] = minAltitude == Integer.MAX_VALUE ? 0 : minAltitude;
        return altitudeMaxMin;
    }

    public double getMaxSpeed(ArrayList<Point> points) {
        double maxSpeed = Integer.MIN_VALUE;
        for (Point point : points) {
            if (point.getSpeed() > maxSpeed)
                maxSpeed = point.getSpeed();
        }
        return maxSpeed == Integer.MIN_VALUE ? 0 : maxSpeed;
    }

    /**
     * @param points
     * @return time of doing activity
     */
    public double[] getHours(ArrayList<Point> points) {
        double[] hours = new double[2];
        //normal time
        hours[0] = 0.0;
        if (points.size() > 0) {
            hours[0] = points.get(points.size() - 1).getTime() - points.get(0).getTime();
        }

        //moving time
        hours[1] = hours[0];

        Point point = null;
        Point pastPoint;
        for (int i = 0; i < points.size(); i++) {
            if (i != 0) {
                pastPoint = point;
                double timePast = pastPoint.getTime();
                point = points.get(i);
                double time = point.getTime();
                double timeDifference = time - timePast;
                if (pastPoint.getPaused())
                    hours[0] -= timeDifference;
                if (timeDifference > 30*1000)
                    hours[1] -= timeDifference;
            } else {
                point = points.get(i);
            }
        }

        hours[0] =  hours[0] / 1000 / 3600;
        hours[1] =  hours[1] / 1000 / 3600;

        return hours;
    }

    /**
     * @param points
     * @return arrayList with all coordinates
     */
    public ArrayList<LatLng> getLatLng(ArrayList<Point> points) {
        ArrayList<LatLng> latLng = new ArrayList<>();
        for (Point point : points) {
            double lat = point.getLat();
            double lon = point.getLon();
            LatLng latlng = new LatLng(lat, lon);
            latLng.add(latlng);
        }
        return latLng;
    }

    //this is haversine Formula for calculating distance between two coordinates
    private double haversineFormula(double lat1, double lat2, double lon1, double lon2) {
        double r = 6371000;
        double fi1 = lat1 * Math.PI / 180;
        double fi2 = lat2 * Math.PI / 180;
        double deltaFi = (lat2 - lat1) * Math.PI / 180;
        double deltaLambda = (lon2 - lon1) * Math.PI / 180;
        double a = Math.sin(deltaFi / 2) * Math.sin(deltaFi / 2) + Math.cos(fi1) *
                Math.cos(fi2) * Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return r * c;
    }

    public String getDate(double time) {
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        Date date = new Date((long) time);
        return format.format(date);
    }

    public int[] getHoursMinutesSeconds(double time) {
        int hours = (int) time;
        double minutesD = (time - hours) * 60.0;
        int minutes = (int) minutesD;
        double secondsD = (minutesD - minutes) * 60.0;
        int seconds = (int) secondsD;
        return new int[]{hours, minutes, seconds};
    }

    public int getIcon(int type) {
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