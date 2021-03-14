package com.example.sportstracker.data;

public class RouteItem {
    private int icon;
    private String date;
    private String title;

    public RouteItem(int icon, String date, String title) {
        this.icon = icon;
        this.date = date;
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
