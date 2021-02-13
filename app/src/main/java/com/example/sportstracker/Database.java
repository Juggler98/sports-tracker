package com.example.sportstracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import static java.lang.Math.round;

public class Database extends SQLiteOpenHelper {

    private static final String TABLE_TYPE = "type_activity";
    private static final String TABLE_ACTIVITY = "activity";
    private static final String TABLE_POINT = "point";

    public Database(@Nullable Context context) {
        super(context, "tracking.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableTypeActivity = "CREATE TABLE " + TABLE_TYPE +
                "(id_type_activity INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "type TEXT NOT NULL)";

        String createTableActivity = "CREATE TABLE " + TABLE_ACTIVITY +
                "(id_activity INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "type_id INTEGER NOT NULL," +
                "time_start REAL NOT NULL," +
                "time_end REAL," +
                "title TEXT," +
                "FOREIGN KEY(type_id) REFERENCES " + TABLE_TYPE + "(id_type_activity))";

        String createTablePoint = "CREATE TABLE " + TABLE_POINT +
                "(id_activity INTEGER NOT NULL," +
                "id_point INTEGER NOT NULL, " +
                "lat REAL NOT NULL," +
                "lon REAL NOT NULL," +
                "ele REAL NOT NULL," +
                "time REAL NOT NULL," +
                "speed REAL NOT NULL," +
                "course REAL NOT NULL," +
                "hdop REAL NOT NULL," +
                "vdop REAL NOT NULL," +
                "FOREIGN KEY(id_activity) REFERENCES " + TABLE_ACTIVITY + "(id_activity)," +
                "PRIMARY KEY (id_point, id_activity))";


        db.execSQL(createTableTypeActivity);
        db.execSQL(createTableActivity);
        db.execSQL(createTablePoint);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TYPE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POINT);

        onCreate(db);
    }

    private boolean addTypes(String type) {
        Log.d("DB_LC", "DB_ADD_TYPES");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("type", type);
        long insert = db.insert(TABLE_TYPE, null, cv);
        db.close();

        if (insert == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean createActivity(Activity activity) {
        Log.d("DB_LC", "DB_ADD_Activity");

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("type_id", activity.getIdType());
        cv.put("time_start", activity.getTimeStart());
//        cv.put("title", "Hello");
        long insert = db.insert(TABLE_ACTIVITY, null, cv);
        db.close();

        if (insert == -1) {
            return false;
        } else {
            Log.d("DB_LC", "DB_ADD_Activity_Succes");
            return true;
        }
    }

    public boolean addPoint(Point point) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("id_activity", point.getIdActivity());
        cv.put("id_point", point.getId());
        cv.put("lat", point.getLat());
        cv.put("lon", point.getLon());
        cv.put("ele", point.getEle());
        cv.put("time", point.getTime());
        cv.put("speed", point.getSpeed());
        cv.put("course", point.getCourse());
        cv.put("hdop", point.getHdop());
        cv.put("vdop", point.getVdop());
        long insert = db.insert(TABLE_POINT, null, cv);
        db.close();

        if (insert == -1) {
            return false;
        } else {
            Log.d("DB_LC", "DB_Add_Point");
            return true;
        }
    }


    public void checkTypes() {
        String queryString = "SELECT * FROM " + TABLE_TYPE;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if (!cursor.moveToFirst()) {
            this.addTypes("Hike");
            this.addTypes("Bike");
            this.addTypes("Run");
            this.addTypes("Swim");
            this.addTypes("Ski");
        }
        cursor.close();
        db.close();
    }

    public String getType(int type) {
        String queryString = "SELECT type FROM " + TABLE_TYPE + " WHERE id_type_activity = " + type;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        String typeStr = "";
        if (cursor.moveToFirst()) {
            typeStr = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return typeStr;
    }

    public int getLastActivityID() {
        String queryString = "SELECT id_activity FROM " + TABLE_ACTIVITY;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        int lastID = 0;
        if (cursor.moveToLast()) {
            lastID = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return lastID;
    }

    public int getLastPointID(int idActivity) {
        String queryString = "SELECT id_point FROM " + TABLE_POINT + " WHERE id_activity = " + idActivity;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        int lastID = 0;
        if (cursor.moveToLast()) {
            lastID = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return lastID;
    }

//    public double getDistance2(int idActivity) {
//        double lat1 = 0;
//        double lat2 = 0;
//        double lon1 = 0;
//        double lon2 = 0;
//        double distance = 0;
//
//        String queryString = "SELECT * FROM " + TABLE_POINT + " WHERE id_activity = " + idActivity;
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor cursor = db.rawQuery(queryString, null);
//
//        boolean firstIterationCheck = true;
//        if (cursor.moveToFirst()) {
//            do {
//            if (!firstIterationCheck) {
//                lat2 = cursor.getDouble(2);
//                lon2 = cursor.getDouble(3);
//                distance += haversineFormula(lat1, lat2, lon1, lon2);
//                lat1 = lat2;
//                lon1 = lon2;
//            } else {
//                lat1 = cursor.getDouble(2);
//                lon1 = cursor.getDouble(3);
//            }
//            firstIterationCheck = false;
//                Log.d("DB_LC", "DB_getDistance");
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        db.close();
//        return round(distance / 10) / 100.0;
//    }

//    public double getDistance(int idActivity) {
//        Log.d("DB_LC", "DB_getDistance");
//        double lat1 = 0;
//        double lat2 = 0;
//        double lon1 = 0;
//        double lon2 = 0;
//        double distance = 0;
//        ArrayList<Point> points = this.getPoints(idActivity);
//        for (int i = 0; i < points.size(); i++) {
//            Point point = points.get(i);
//            if (i != 0) {
//                lat2 = point.getLat();
//                lon2 = point.getLon();
//                distance += haversineFormula(lat1, lat2, lon1, lon2);
//                lat1 = lat2;
//                lon1 = lon2;
//            } else {
//                lat1 = point.getLat();
//                lon1 = point.getLon();
//            }
//        }
//        return round(distance / 10) / 100.0;
//    }

//    public ArrayList<LatLng> getLatLng(int activityID) {
//        ArrayList<Point> points = this.getPoints(activityID);
//        ArrayList<LatLng> latLng = new ArrayList<>();
//        for (Point point : points) {
//            double lat = point.getLat();
//            double lon = point.getLon();
//            LatLng latlng = new LatLng(lat, lon);
//            latLng.add(latlng);
//        }
//        return latLng;
//    }


    // this is haversine Formula for calculating distance between two coordinates
//    private double haversineFormula(double lat1, double lat2, double lon1, double lon2) {
//        double r = 6371000;
//        double fi1 = lat1 * Math.PI / 180;
//        double fi2 = lat2 * Math.PI / 180;
//        double deltaFi = (lat2 - lat1) * Math.PI / 180;
//        double deltaLambda = (lon2 - lon1) * Math.PI / 180;
//        double a = Math.sin(deltaFi / 2) * Math.sin(deltaFi / 2) + Math.cos(fi1) * Math.cos(fi2) * Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//        return r * c;
//    }

    public ArrayList<Activity> getActivities() {
        ArrayList<Activity> activities = new ArrayList<>();
        String queryString = "SELECT * FROM " + TABLE_ACTIVITY;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
//            Log.d("DB_LC", "Column count: " + cursor.getColumnCount());
//            Log.d("DB_LC", "Row count: " + cursor.getCount());
            do {
                int id = cursor.getInt(0);
                int type = cursor.getInt(1);
                double timeStart = cursor.getDouble(2);
                Activity activity = new Activity(type, timeStart);
                activity.setId(id);
                if (cursor.getType(3) != 0) {
                    activity.setTimeEnd(cursor.getDouble(3));
//                    Log.d("DB_LC", "EndTime is: " + cursor.getDouble(3));
                }
                if (cursor.getType(4) != 0) {
                    activity.setTitle(cursor.getString(4));
//                    Log.d("DB_LC", "Title is: " + cursor.getString(4));
                }
//                if (activity.getTimeEnd() != 0.0) {
//                    activities.add(activity);
//                }
                activities.add(activity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return activities;
    }

    public Activity getActivity(int activityID) {
        ArrayList<Activity> activities = this.getActivities();
        for (int i = 0; i < activities.size(); i++) {
            if (activities.get(i).getId() == activityID)
                return activities.get(i);
        }
        return null;
    }

    public void deleteActivity(int activityID) {
        String deletePoints = "DELETE FROM " + TABLE_POINT + " WHERE id_activity = " + activityID;
        String deleteActivity = "DELETE FROM " + TABLE_ACTIVITY + " WHERE id_activity = " + activityID +
                " AND time_end IS NOT NULL";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(deletePoints);
        db.execSQL(deleteActivity);
        db.close();
        Log.d("DB_LC", "Delete Activity: " + activityID);
    }

    public boolean updateActivity(int activityID, int type, double endTime, String name) {
        String changeType = "UPDATE " + TABLE_ACTIVITY + " SET type_id = " + type + " WHERE id_activity = " + activityID;
        String setEndTime = "UPDATE " + TABLE_ACTIVITY + " SET time_end = " + endTime + " WHERE id_activity = " + activityID;
//        String setTitle = "UPDATE " + TABLE_ACTIVITY + " SET title = " + name + " WHERE id_activity = " + activityID;

        SQLiteDatabase db = getWritableDatabase();
        int update = 0;
        if (type != 0) {
            db.execSQL(changeType);
        }
        if (endTime != 0.0) {
            db.execSQL(setEndTime);
        }
        if (!name.equals("")) {
            ContentValues cv = new ContentValues();
            cv.put("title", name);
            String[] whereArgs = {activityID + ""};
            update = db.update(TABLE_ACTIVITY, cv, "id_activity=?", whereArgs);
//            db.execSQL(setTitle);
        }
        db.close();
        Log.d("DB_LC", "Change Activity: " + activityID + ", " + type + ", " + endTime + ", " + name);
        return update > 0;
    }

    public ArrayList<Point> getPoints(int activityID) {
        ArrayList<Point> points = new ArrayList<>();
        String queryString = "SELECT * FROM " + TABLE_POINT + " WHERE id_activity = " + activityID;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
//            Log.d("DB_LC", "Column count: " + cursor.getColumnCount());
//            Log.d("DB_LC", "Row count: " + cursor.getCount());
            do {
                int idActivity = cursor.getInt(0);
                int idPoint = cursor.getInt(1);
                double lat = cursor.getDouble(2);
                double lon = cursor.getDouble(3);
                double ele = cursor.getDouble(4);
                double time = cursor.getDouble(5);
                double speed = cursor.getDouble(6);
                double course = cursor.getDouble(8);
                double hdop = cursor.getDouble(7);
//                Log.d("DB_LC", "VDOP: " +  cursor.getDouble(8));
                double vdop = cursor.getDouble(9);
                //TODO v novej databaze treba preusporiadat

                Point point = new Point(idActivity, idPoint, lat, lon, ele, time, speed, course, hdop, vdop);
                points.add(point);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return points;
    }

//    public double getHours(int activityID) {
//        ArrayList<Point> points = this.getPoints(activityID);
//        double ms = 0.0;
//        if (points.size() > 0) {
//            ms = points.get(points.size() - 1).getTime() - points.get(0).getTime();
//        }
//        return ms / 1000 / 3600;
//    }

    public void deleteAll() {
        String deletePoints = "DELETE FROM " + TABLE_POINT;
        String deleteActivity = "DELETE FROM " + TABLE_ACTIVITY;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(deletePoints);
        db.execSQL(deleteActivity);
        db.close();
        Log.d("DB_LC", "Delete All");
    }

//    public double getElevationGain(int activityID) {
//        ArrayList<Point> points = this.getPoints(activityID);
//        double ele1 = 0.0;
//        double ele2 = 0.0;
//        double elevationGain = 0.0;
//        double elevationDifference = 0.0;
//        for (int i = 0; i < points.size(); i++) {
//            Point point = points.get(i);
//            if (i != 0) {
//                ele2 = point.getEle();
//                elevationDifference = ele2 - ele1;
//                if (elevationDifference > 5) {
//                    elevationGain += elevationDifference;
//                }
//                ele1 = ele2;
//            } else {
//                ele1 = point.getEle();
//            }
//        }
//        return elevationGain;
//    }


}
