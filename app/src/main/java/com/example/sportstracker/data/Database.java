package com.example.sportstracker.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {

    private static final String TABLE_TYPE = "type_activity";
    private static final String TABLE_ACTIVITY = "activity";
    private static final String TABLE_POINT = "point";

    public Database(@Nullable Context context) {
        super(context, "tracking.db", null, 4);
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
                "auto_pause INTEGER," +
                "FOREIGN KEY(type_id) REFERENCES " + TABLE_TYPE + "(id_type_activity))";

        String createTablePoint = "CREATE TABLE " + TABLE_POINT +
                "(id_activity INTEGER NOT NULL," +
                "id_point INTEGER NOT NULL, " +
                "lat REAL NOT NULL," +
                "lon REAL NOT NULL," +
                "ele REAL NOT NULL," +
                "time REAL NOT NULL," +
                "speed REAL," +
                "hacc REAL," +
                "course REAL," +
                "vacc REAL," +
                "paused INTEGER," +
                "FOREIGN KEY(id_activity) REFERENCES " + TABLE_ACTIVITY + "(id_activity)," +
                "PRIMARY KEY (id_point, id_activity))";


        db.execSQL(createTableTypeActivity);
        db.execSQL(createTableActivity);
        db.execSQL(createTablePoint);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TYPE);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITY);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POINT);
//
//        onCreate(db);

        //db.execSQL("ALTER TABLE " + TABLE_ACTIVITY + " ADD COLUMN auto_pause INTEGER;");
        //db.execSQL("ALTER TABLE " + TABLE_POINT + " ADD COLUMN paused INTEGER;");

        String createTablePoint = "CREATE TABLE " + "point2" +
                "(id_activity INTEGER NOT NULL," +
                "id_point INTEGER NOT NULL, " +
                "lat REAL NOT NULL," +
                "lon REAL NOT NULL," +
                "ele REAL NOT NULL," +
                "time REAL NOT NULL," +
                "speed REAL," +
                "hacc REAL," +
                "course REAL," +
                "vacc REAL," +
                "paused INTEGER," +
                "FOREIGN KEY(id_activity) REFERENCES " + TABLE_ACTIVITY + "(id_activity)," +
                "PRIMARY KEY (id_point, id_activity))";

        String insert = "INSERT INTO " + "point2" + " SELECT * FROM " + TABLE_POINT;

        db.execSQL(createTablePoint);
        db.execSQL(insert);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POINT);
        db.execSQL("ALTER TABLE " + "point2" + " RENAME TO " + TABLE_POINT);
    }

    private void addTypes(String type) {
        Log.d("DB_LC", "DB_ADD_TYPES");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("type", type);
        db.insert(TABLE_TYPE, null, cv);
        db.close();
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
            this.addTypes("Walk");
            this.addTypes("Skate");
        }
        cursor.close();
        db.close();
    }

    public void createActivity(Route route) {
        Log.d("DB_LC", "DB_ADD_Activity");

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("type_id", route.getIdType());
        cv.put("time_start", route.getTimeStart());
        if (!route.getAutoPause()) {
            cv.put("auto_pause", route.getAutoPause());
        }
        db.insert(TABLE_ACTIVITY, null, cv);
        db.close();
    }

    public void addPoint(Point point) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("id_activity", point.getIdActivity());
        cv.put("id_point", point.getId());
        cv.put("lat", point.getLat());
        cv.put("lon", point.getLon());
        cv.put("ele", point.getEle());
        cv.put("time", point.getTime());
        if (point.getSpeed() != -1)
            cv.put("speed", point.getSpeed());
        if (point.getHacc() != -1)
            cv.put("hacc", point.getHacc());
        if (point.getCourse() != -1)
            cv.put("course", point.getCourse());
        if (point.getVacc() != -1)
            cv.put("vacc", point.getVacc());
        if (point.getPaused()) {
            cv.put("paused", point.getPaused());
        }
        db.insert(TABLE_POINT, null, cv);
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

    public String[] getTypes() {
        String queryString = "SELECT type FROM " + TABLE_TYPE;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        String[] types = new String[cursor.getCount()];
        int row = 0;
        if (cursor.moveToFirst()) {
            do {
                types[row++] = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return types;
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

    public ArrayList<Route> getActivities() {
        ArrayList<Route> routes = new ArrayList<>();
        String queryString = "SELECT * FROM " + TABLE_ACTIVITY;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                int type = cursor.getInt(1);
                double timeStart = cursor.getDouble(2);
                Route route = new Route(type, timeStart);
                route.setId(id);
                if (cursor.getType(3) != 0) {
                    route.setTimeEnd(cursor.getDouble(3));
                }
                if (cursor.getType(4) != 0) {
                    route.setTitle(cursor.getString(4));
                }
                if (cursor.getType(5) != 0) {
                    if (cursor.getInt(5) == 1)
                        route.setAutoPause(true);
                    else
                        route.setAutoPause(false);
                }
                if (route.getTimeEnd() != 0.0 || route.getId() != this.getLastActivityID()) {
                    routes.add(route);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return routes;
    }

    public Route getActivity(int activityID) {
        Route route = null;
        String queryString = "SELECT * FROM " + TABLE_ACTIVITY + " WHERE id_activity = " + activityID;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            int type = cursor.getInt(1);
            double timeStart = cursor.getDouble(2);
            route = new Route(type, timeStart);
            route.setId(id);
            if (cursor.getType(3) != 0) {
                route.setTimeEnd(cursor.getDouble(3));
            }
            if (cursor.getType(4) != 0) {
                route.setTitle(cursor.getString(4));
            }
            if (cursor.getType(5) != 0) {
                if (cursor.getInt(5) == 1)
                    route.setAutoPause(true);
                else
                    route.setAutoPause(false);
            }
        }
        cursor.close();
        db.close();
        return route;
    }

    /**
     * delete specific route
     *
     * @param activityID id of route to delete
     */
    public void deleteActivity(int activityID) {
        String deletePoints = "DELETE FROM " + TABLE_POINT + " WHERE id_activity = " + activityID;
        String deleteActivity = "DELETE FROM " + TABLE_ACTIVITY + " WHERE id_activity = " + activityID;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(deletePoints);
        db.execSQL(deleteActivity);
        db.close();
        Log.d("DB_LC", "Delete Activity: " + activityID);
    }

    //Only For Debug
    /*private void deleteType(int typeID) {
        String deleteType = "DELETE FROM " + TABLE_TYPE + " WHERE id_type_activity = " + typeID;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(deleteType);
        db.close();
        Log.d("DB_LC", "Delete Type: " + typeID);
    }*/

    public void updateActivity(int activityID, int type, double endTime, String name) {
        String changeType = "UPDATE " + TABLE_ACTIVITY + " SET type_id = " + type + " WHERE id_activity = " + activityID;
        String setEndTime = "UPDATE " + TABLE_ACTIVITY + " SET time_end = " + endTime + " WHERE id_activity = " + activityID;

        SQLiteDatabase db = getWritableDatabase();

        if (endTime != 0.0) {
            db.execSQL(setEndTime);
        } else if (type != 0) {
            db.execSQL(changeType);
        } else {
            ContentValues cv = new ContentValues();
            cv.put("title", name);
            String[] whereArgs = {activityID + ""};
            db.update(TABLE_ACTIVITY, cv, "id_activity=?", whereArgs);
        }
        db.close();
        Log.d("DB_LC", "Change Activity: " + activityID + ", " + type + ", " + endTime + ", " + name);
    }

    public ArrayList<Point> getPoints(int activityID) {
        ArrayList<Point> points = new ArrayList<>();
        String queryString = "SELECT * FROM " + TABLE_POINT + " WHERE id_activity = " + activityID;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
            do {
                int idActivity = cursor.getInt(0);
                int idPoint = cursor.getInt(1);
                double lat = cursor.getDouble(2);
                double lon = cursor.getDouble(3);
                double ele = cursor.getDouble(4);
                double time = cursor.getDouble(5);
                double speed = cursor.getType(6) == 0 ? -1 : cursor.getDouble(6);
                double hacc = cursor.getType(7) == 0 ? -1 : cursor.getDouble(7);
                double course = cursor.getType(8) == 0 ? -1 : cursor.getDouble(8);
                double vacc = cursor.getType(9) == 0 ? -1 : cursor.getDouble(9);

                Point point = new Point(idActivity, idPoint, lat, lon, ele, time, speed, hacc, course, vacc);
                if (cursor.getType(10) != 0) {
                    if (cursor.getInt(10) == 1) {
                        point.setPaused(true);
                    } else {
                        point.setPaused(false);
                    }
                }
                points.add(point);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return points;
    }

    public void setPause(int activityID, int pointID) {
        String updatePause = "UPDATE " + TABLE_POINT + " SET paused = " + 1 + " WHERE id_activity = " + activityID +
                " AND id_point = " + pointID;

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(updatePause);
//            ContentValues cv = new ContentValues();
//            cv.put("paused", 1);
//            String[] whereArgs = {activityID + "", pointID + ""};
//            update = db.update(TABLE_POINT, cv, "id_activity=? AND id_point=?", whereArgs);
        db.close();
        Log.d("DB_LC", "setPause: " + activityID + ", " + pointID);
    }

    public void deleteAll() {
        String deletePoints = "DELETE FROM " + TABLE_POINT;
        String deleteActivity = "DELETE FROM " + TABLE_ACTIVITY + " WHERE time_end IS NOT NULL OR id_activity != " + this.getLastActivityID();
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(deletePoints);
        db.execSQL(deleteActivity);
        db.close();
        Log.d("DB_LC", "Delete All");
    }

}
