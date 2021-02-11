package com.example.sportstracker;

public class Activity {

    private int id;
    private int idType;
    private String timeStart;
    private String timeEnd = null;
    private String title = null;

    public Activity(int id, int idType, String timeStart) {
        this.id = id;
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

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}