package com.example.sportstracker;

import com.example.sportstracker.data.Point;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Class for methods using over application.
 */
public class RoutesMethods {

    public RoutesMethods() {
    }

    /**
     * @param points points
     * @return distance of route in metres
     */
    public double getDistance(ArrayList<Point> points) {
        double lat1;
        double lat2;
        double lon1;
        double lon2;
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
        return distance;
    }

    /**
     * @param points points
     * @return elevation gain of route
     */
    public double[] getElevationGainLoss(ArrayList<Point> points) {
        double[] elevationGainLoss = new double[2];
        double ele1 = 0.0;
        double ele2;
        double elevationGain = 0.0;
        double elevationLoss = 0.0;
        double elevationDifference;
        int difference = 8;
        for (int j = 0; j < 2; j++) {
            double[] altitudeMaxMin = getAltitudeMaxMin(points);
            if (altitudeMaxMin[0] - altitudeMaxMin[1] > elevationGain) {
                difference = j == 1 ? 0 : difference;
                elevationGain = 0.0;
                elevationLoss = 0.0;
                for (int i = 0; i < points.size(); i = i + 10) {
                    Point point = points.get(i);
                    if (i != 0 && point.getVacc() < 8) {
                        ele2 = point.getEle();
                        elevationDifference = ele2 - ele1;
                        if (elevationDifference > difference) {
                            elevationGain += elevationDifference;
                        } else if (elevationDifference < difference * -1) {
                            elevationLoss += elevationDifference;
                        }
                        ele1 = ele2;
                    } else {
                        ele1 = point.getEle();
                    }
                }
            }
        }
        elevationGainLoss[0] = elevationGain;
        elevationGainLoss[1] = elevationLoss;
        return elevationGainLoss;
    }

    /**
     * @param points points
     * @return min max altitude
     */
    public double[] getAltitudeMaxMin(ArrayList<Point> points) {
        double[] altitudeMaxMin = new double[2];
        double maxAltitude = Integer.MIN_VALUE;
        double minAltitude = Integer.MAX_VALUE;
        for (Point point : points) {
            if (point.getVacc() < 8) {
                double altitude = point.getEle();
                if (altitude > maxAltitude)
                    maxAltitude = altitude;
                if (altitude < minAltitude)
                    minAltitude = altitude;
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
        if (maxSpeed <= 0) {
            ArrayList<Point> twoPoints = new ArrayList<>();
            for (int i = 0; i < points.size(); i++) {
                if (i != 0) {
                    twoPoints.add(points.get(i));
                    double speed = this.getSpeed(twoPoints);
                    if (speed > maxSpeed) {
                        maxSpeed = speed;
                    }
                    twoPoints.remove(0);
                } else {
                    twoPoints.add(points.get(i));
                }
            }
        }
        return maxSpeed == Integer.MIN_VALUE ? 0 : maxSpeed;
    }

    public double getSpeed(ArrayList<Point> points) {
        double timePast = points.get(0).getTime();
        double time = points.get(1).getTime();
        double timeDifference = (time - timePast) / 1000.0;
        return timeDifference == 0 ? 0 : this.getDistance(points) / timeDifference;
    }

    /**
     * @param points points
     * @return time of doing activity in hours
     */
    public double[] getHours(ArrayList<Point> points, boolean autoPause) {
        double[] hours = new double[2];
        //normal time
        hours[0] = 0.0;
        if (points.size() > 0) {
            hours[0] = points.get(points.size() - 1).getTime() - points.get(0).getTime();
        }

        //moving time
        hours[1] = hours[0];

        ArrayList<Point> twoPoints = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            if (i != 0) {
                double timePast = twoPoints.get(0).getTime();
                twoPoints.add(points.get(i));
                double time = twoPoints.get(1).getTime();
                double timeDifference = time - timePast;
                if (twoPoints.get(0).getPaused())
                    hours[0] -= timeDifference;
                double speed = this.getDistance(twoPoints) / (timeDifference / 1000.0);
                if (speed < 0.15 || twoPoints.get(0).getPaused())
                    hours[1] -= timeDifference;
                twoPoints.remove(0);
            } else {
                twoPoints.add(points.get(i));
            }
        }

        hours[0] = hours[0] / 1000 / 3600;
        if (!autoPause)
            hours[1] = hours[0];
        else
            hours[1] = hours[1] / 1000 / 3600;

        return hours;
    }

    /**
     * @param points points
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


    /**
     * This is Haversine Formula for calculating distance between two coordinates
     * https://en.wikipedia.org/wiki/Haversine_formula
     * https://www.movable-type.co.uk/scripts/latlong.html
     *
     * @param lat1 latitude of first point
     * @param lat2 latitude of second point
     * @param lon1 longitude of first point
     * @param lon2 longitude of second point
     * @return metres between two points
     */
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

    /**
     * @param time    in ms
     * @param pattern pattern of date
     * @return Date in string according pattern
     */
    public String getDate(double time, String pattern) {
        DateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        Date date = new Date((long) time);
        return dateFormat.format(date);
    }

    public String[] getPace(double speed) {
        String[] pace = new String[2];
        double minutesD = 60 / speed;
        int minutes = (int) minutesD;
        double secondsD = (minutesD - minutes) * 60.0;
        int seconds = (int) secondsD;
        String secondsStr = seconds < 10 ? "0" + seconds : "" + seconds;
        pace[0] = minutes + "";
        pace[1] = secondsStr;
        return pace;
    }

    public int[] getHoursMinutesSeconds(double time) {
        int hours = (int) time;
        double minutesD = (time - hours) * 60.0;
        int minutes = (int) minutesD;
        double secondsD = (minutesD - minutes) * 60.0;
        int seconds = (int) secondsD;
        return new int[]{hours, minutes, seconds};
    }

    public String[] getMinutesSeconds(int minutes, int seconds) {
        String[] minutesSeconds = new String[2];
        minutesSeconds[0] = minutes < 10 ? "0" + minutes : "" + minutes;
        minutesSeconds[1] = seconds < 10 ? "0" + seconds : "" + seconds;
        return minutesSeconds;
    }

    public int getIcon(int type) {
        switch (type) {
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