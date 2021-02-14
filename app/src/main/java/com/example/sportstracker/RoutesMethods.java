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

    private final String TXT = ".txt";


    public RoutesMethods() {
    }

//    /**
//     * Write to file.
//     *
//     * @param name of file
//     * @param text to write
//     * @param context application context
//     */
//    public void write(String name, String text, Context context) {
//        try {
//            FileOutputStream fileOutputStream = context.openFileOutput(name + TXT, Context.MODE_APPEND);
//            fileOutputStream.write(text.getBytes());
//            fileOutputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    /**
//     *
//     * @param name
//     * @param context
//     * @return time of doing activity
//     */
//    public double getTime(int name, Context context) {
//        try {
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.openFileInput(name + TXT)));
//
//            String line;
//            String firstDate = "";
//            String lastDate = "";
//            boolean firstLineCheck = true;
//            while ((line = bufferedReader.readLine()) != null) {
//                String[] tokens = line.split(",");
//                if (firstLineCheck)
//                    firstDate = tokens[3];
//                firstLineCheck = false;
//                lastDate = tokens[3];
//            }
//
//            Date date1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).parse(firstDate);
//            Date date2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).parse(lastDate);
//            double time = date2.getTime() - date1.getTime();
//            return round(time / 1000 / 3600 * 100000.0) / 100000.0;
//        } catch (ParseException | IOException e) {
//            e.printStackTrace();
//        }
//        return 0.0;
//    }

//    /**
//     *
//     * @param name
//     * @param context
//     * @return distance of route
//     */
//    public double getDistance(int name, Context context) {
//        double lat1 = 0;
//        double lat2 = 0;
//        double lon1 = 0;
//        double lon2 = 0;
//
//        double distance = 0;
//        try {
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.openFileInput(name + TXT)));
//
//            boolean firstIterationCheck = true;
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                String[] tokens = line.split(",");
//                if (!firstIterationCheck) {
//                    lat2 = Double.parseDouble(tokens[0]);
//                    lon2 = Double.parseDouble(tokens[1]);
//                    distance += haversineFormula(lat1, lat2, lon1, lon2);
//                    lat1 = lat2;
//                    lon1 = lon2;
//                } else {
//                    lat1 = Double.parseDouble(tokens[0]);
//                    lon1 = Double.parseDouble(tokens[1]);
//                }
//                firstIterationCheck = false;
//            }
//            return round(distance / 10) / 100.0;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return 0.0;
//    }

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

    public double getElevationGain(ArrayList<Point> points) {
        double ele1 = 0.0;
        double ele2 = 0.0;
        double elevationGain = 0.0;
        double elevationDifference = 0.0;
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            if (i != 0) {
                ele2 = point.getEle();
                elevationDifference = ele2 - ele1;
                if (elevationDifference > 3.9) {
                    elevationGain += elevationDifference;
                }
                ele1 = ele2;
            } else {
                ele1 = point.getEle();
            }
        }
        return elevationGain;
    }

    public double getHours(ArrayList<Point> points) {
        double ms = 0.0;
        if (points.size() > 0) {
            ms = points.get(points.size() - 1).getTime() - points.get(0).getTime();
        }
        return ms / 1000 / 3600;
    }

    public ArrayList<LatLng> getLatLng(ArrayList<Point> points) {
//        ArrayList<Point> points = this.getPoints(activityID);
        ArrayList<LatLng> latLng = new ArrayList<>();
        for (Point point : points) {
            double lat = point.getLat();
            double lon = point.getLon();
            LatLng latlng = new LatLng(lat, lon);
            latLng.add(latlng);
        }
        return latLng;
    }

//    /**
//     *
//     * @param name
//     * @param context
//     * @return elevation gain of route
//     */
//    public double getElevationGain(int name, Context context) {
//        double ele1 = 0;
//        double ele2 = 0;
//
//        double elevationGain = 0;
//        try {
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.openFileInput(name + TXT)));
//
//            double elevationDifference = 0;
//            boolean firstIterationCheck = true;
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                String[] tokens = line.split(",");
//                if (!firstIterationCheck) {
//                    ele2 = Double.parseDouble(tokens[2]);
//                    elevationDifference = ele2 - ele1;
//                    if (elevationDifference > 0) {
//                        elevationGain += elevationDifference;
//                    }
//                    ele1 = ele2;
//                } else {
//                    ele1 = Double.parseDouble(tokens[2]);
//                }
//                firstIterationCheck = false;
//            }
//            return round(elevationGain);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }

     //this is haversine Formula for calculating distance between two coordinates
    private double haversineFormula(double lat1, double lat2, double lon1, double lon2) {
        double r = 6371000;
        double fi1 = lat1 * Math.PI / 180;
        double fi2 = lat2 * Math.PI / 180;
        double deltaFi = (lat2 - lat1) * Math.PI / 180;
        double deltaLambda = (lon2 - lon1) * Math.PI / 180;
        double a = Math.sin(deltaFi / 2) * Math.sin(deltaFi / 2) + Math.cos(fi1) * Math.cos(fi2) * Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return r * c;
    }


//    /**
//     *
//     * @param name
//     * @param context
//     * @return arrayList with all coordinates
//     */
//    public ArrayList<LatLng> loadLatLng(int name, Context context) {
//        double lat = 0;
//        double lon = 0;
//        ArrayList<LatLng> arrayList = new ArrayList<>();
//        try {
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.openFileInput(name + TXT)));
//
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                String[] tokens = line.split(",");
//                lat = Double.parseDouble(tokens[0]);
//                lon = Double.parseDouble(tokens[1]);
//                LatLng latlng = new LatLng(lat, lon);
//                arrayList.add(latlng);
//            }
//            return arrayList;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return arrayList;
//    }

    /**
     *
     * @param context
     * @return arrayList with id of route, date and name
     */
//    public ArrayList<String> loadData(Context context) {
//        ArrayList<String> arrayList = new ArrayList<>();
//        try {
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.openFileInput( "data.txt")));
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                arrayList.add(line);
//            }
//            return arrayList;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return arrayList;
//    }

    /**
     * delete specific route
     * @param name of route to delete
     * @param context
     */
//    public void delete(int name, Context context) {
//        ArrayList<String> arrayList = this.loadData(context);
//        context.deleteFile(name + TXT);
//        for (int i = 0; i < arrayList.size(); ++i) {
//            String[] tokens = arrayList.get(i).split(",");
//            if (tokens[0].equals(name))
//            {
//                arrayList.remove(i);
//                Log.d("RouteInfo_LC", "Removing: " + arrayList.get(i));
//                break;
//            }
//        }
//        context.deleteFile( "data" + TXT);
//        for (int i = 0; i < arrayList.size(); i++)
//        {
//            this.write("data", arrayList.get(i) + "\n", context);
//        }
//
//    }

}