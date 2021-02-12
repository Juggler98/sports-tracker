package com.example.sportstracker;

public class Point {

    private int id;
    private int idActivity;
    private double lat;
    private double lon;
    private double alt;
    private double time;
    private double speed;
    private double acc;
    private double bear;

    public Point(int id, int idActivity, double lat, double lon, double alt, double time, double speed, double acc, double bear) {
        this.id = id;
        this.idActivity = idActivity;
        this.lat = lat;
        this.lon = lon;
        this.alt = alt;
        this.time = time;
        this.speed = speed;
        this.acc = acc;
        this.bear = bear;
    }

    public Point() {
    }

    @Override
    public String toString() {
        return "Point{" +
                "id=" + id +
                ", idActivity=" + idActivity +
                ", lat=" + lat +
                ", lon=" + lon +
                ", alt=" + alt +
                ", time=" + time +
                ", speed=" + speed +
                ", acc=" + acc +
                ", bear=" + bear +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdActivity() {
        return idActivity;
    }

    public void setIdActivity(int idActivity) {
        this.idActivity = idActivity;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getAlt() {
        return alt;
    }

    public void setAlt(double alt) {
        this.alt = alt;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getAcc() {
        return acc;
    }

    public void setAcc(double acc) {
        this.acc = acc;
    }

    public double getBear() {
        return bear;
    }

    public void setBear(double bear) {
        this.bear = bear;
    }
}
