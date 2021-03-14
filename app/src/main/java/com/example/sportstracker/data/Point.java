package com.example.sportstracker.data;

import androidx.annotation.NonNull;

public class Point {

    private int id;
    private int idActivity;
    private double lat;
    private double lon;
    private double ele;
    private double time;
    private double speed;
    private double hdop;
    private double vdop;
    private double course;

    private boolean paused = false;

    public Point(int idActivity, int id, double lat, double lon, double ele,
                 double time, double speed, double course, double hdop, double vdop) {
        this.idActivity = idActivity;
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.ele = ele;
        this.time = time;
        this.speed = speed;
        this.course = course;
        this.hdop = hdop;
        this.vdop = vdop;
    }

    public Point() {
    }

    @NonNull
    @Override
    public String toString() {
        return "Point{" +
                "idActivity=" + idActivity +
                ",id=" + id +
                ", lat=" + lat +
                ", lon=" + lon +
                ", ele=" + ele +
                ", time=" + time +
                ", speed=" + speed +
                ", course=" + course +
                ", hdop=" + hdop +
                ", vdop=" + vdop +
                ", paused=" + paused +
                '}';
    }

    public int getId() {
        return id;
    }

    int getIdActivity() {
        return idActivity;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public double getEle() {
        return ele;
    }

    public double getTime() {
        return time;
    }

    public double getSpeed() {
        return speed;
    }

    public double getHdop() {
        return hdop;
    }

    public double getCourse() {
        return course;
    }

    public double getVdop() {
        return vdop;
    }

    public boolean getPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
