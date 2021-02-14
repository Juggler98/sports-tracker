package com.example.sportstracker;

public class Activity {

    private int id;
    private int idType;
    private double timeStart;
    private double timeEnd = 0.0;
    private String title = "";
    private boolean autoPause = true;

    public Activity(int idType, double timeStart) {
        this.idType = idType;
        this.timeStart = timeStart;
    }

    public Activity() {
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", idType=" + idType +
                ", timeStart='" + timeStart + '\'' +
                ", timeEnd='" + timeEnd + '\'' +
                ", title='" + title + '\'' +
                ", autoPause='" + autoPause + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }

    public double getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(double timeStart) {
        this.timeStart = timeStart;
    }

    public double getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(double timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean getAutoPause() {
        return autoPause;
    }

    public void setAutoPause(boolean autoPause) {
        this.autoPause = autoPause;
    }
}