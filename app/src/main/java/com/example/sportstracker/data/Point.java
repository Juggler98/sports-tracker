package com.example.sportstracker.data;

import androidx.annotation.NonNull;

public class Point {

    private final int id;
    private final int idActivity;
    private final double lat;
    private final double lon;
    private final double ele;
    private final double time;
    private final double speed;
    private final double hacc;
    private final double vacc;
    private final double course;

    private boolean paused = false;

    public Point(int idActivity, int id, double lat, double lon, double ele,
                 double time, double speed, double hacc, double course, double vacc) {
        this.idActivity = idActivity;
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.ele = ele;
        this.time = time;
        this.speed = speed;
        this.hacc = hacc;
        this.course = course;
        this.vacc = vacc;
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
                ", hacc=" + hacc +
                ", course=" + course +
                ", vacc=" + vacc +
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

    public double getHacc() {
        return hacc;
    }

    public double getCourse() {
        return course;
    }

    public double getVacc() {
        return vacc;
    }

    public boolean getPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
