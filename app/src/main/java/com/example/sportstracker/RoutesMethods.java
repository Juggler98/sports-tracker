package com.example.sportstracker;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static java.lang.Math.round;

/**
 * Class for methods using over application.
 */
public class RoutesMethods {


    public RoutesMethods() {

    }

    /**
     *
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
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            if (i != 0) {
                lat2 = point.getLat();
                lon2 = point.getLon();
                distance += haversineFormula(lat1, lat2, lon1, lon2);
                lat1 = lat2;
                lon1 = lon2;
            } else {
                lat1 = point.getLat();
                lon1 = point.getLon();
            }
        }
        return round(distance / 10) / 100.0;
    }

    /**
     *
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
     *
     * @param points
     * @return time of doing activity
     */
    public double getHours(ArrayList<Point> points) {
        double ms = 0.0;
        if (points.size() > 0) {
            ms = points.get(points.size() - 1).getTime() - points.get(0).getTime();
        }
        return ms / 1000 / 3600;
    }

    /**
     *
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

}