package com.example.sportstracker;

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

    public Point(int idActivity, int id, double lat, double lon, double ele,
                 double time, double speed, double hdop, double vdop, double course) {
        this.idActivity = idActivity;
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.ele = ele;
        this.time = time;
        this.speed = speed;
        this.hdop = hdop;
        this.vdop = vdop;
        this.course = course;
    }

    public Point() {
    }

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
                ", hdop=" + hdop +
                ", vdop=" + vdop +
                ", course=" + course +
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

    public double getEle() {
        return ele;
    }

    public void setEle(double ele) {
        this.ele = ele;
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

    public double getHdop() {
        return hdop;
    }

    public void setHdop(double hdop) {
        this.hdop = hdop;
    }

    public double getCourse() {
        return course;
    }

    public void setCourse(double course) {
        this.course = course;
    }

    public double getVdop() {
        return vdop;
    }

    public void setVdop(double vdop) {
        this.vdop = vdop;
    }
}
