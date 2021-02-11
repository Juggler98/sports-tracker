package com.example.sportstracker;

public class Point {

    private int id;
    private int idActivity;
    private double lat;
    private double lon;
    private double ele;
    private double time;
    private double speed = -1;
    private double hdop = -1;
    private double course = -1;

    public Point(int id, int idActivity, double lat, double lon, double ele, double time) {
        this.id = id;
        this.idActivity = idActivity;
        this.lat = lat;
        this.lon = lon;
        this.ele = ele;
        this.time = time;
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
                ", ele=" + ele +
                ", time=" + time +
                ", speed=" + speed +
                ", hdop=" + hdop +
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
}
