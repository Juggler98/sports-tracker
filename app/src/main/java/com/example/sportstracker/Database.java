package com.example.sportstracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

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
                "hdop REAL NOT NULL," +
                "course REAL NOT NULL," +
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
        Log.d("DB_LC", "DB_AddPoint");

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("id_activity", point.getIdActivity());
        cv.put("id_point", point.getId());
        cv.put("lat", point.getLat());
        cv.put("lon", point.getLon());
        cv.put("ele", point.getEle());
        cv.put("time", point.getTime());
        cv.put("speed", point.getSpeed());
        cv.put("hdop", point.getHdop());
        cv.put("course", point.getCourse());
        long insert = db.insert(TABLE_POINT, null, cv);
        db.close();

        if (insert == -1) {
            return false;
        } else {
            Log.d("DB_LC", "DB_Add_Point_Succes");
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

    public double getDistance(int idActivity) {
        double lat1 = 0;
        double lat2 = 0;
        double lon1 = 0;
        double lon2 = 0;
        double distance = 0;

        String queryString = "SELECT * FROM " + TABLE_ACTIVITY + " WHERE id_activity = " + idActivity;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        boolean firstIterationCheck = true;
        if (cursor.moveToFirst()) {
            do {
            if (!firstIterationCheck) {
                lat2 = cursor.getDouble(2);
                lon2 = cursor.getDouble(3);
                distance += haversineFormula(lat1, lat2, lon1, lon2);
                lat1 = lat2;
                lon1 = lon2;
            } else {
                lat1 = cursor.getDouble(2);
                lon1 = cursor.getDouble(3);
            }
            firstIterationCheck = false;
                Log.d("DB_LC", "DB_getDistance");
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return round(distance / 10) / 100.0;
    }


    // this is haversine Formula for calculating distance between two coordinates
    private double haversineFormula(double lat1, double lat2, double lon1, double lon2) {
        double r = 6371000;
        double fi1 = lat1 * Math.PI / 180;
        double fi2 = lat2 * Math.PI / 180;
        double deltaFi = (lat2 - lat1) * Math.PI / 180;
        double deltaLambda = (lon2 - lon1) * Math.PI / 180;
        double a = Math.sin(deltaFi / 2) * Math.sin(deltaFi / 2) + Math.cos(fi1) * Math.cos(fi2) * Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return r * c;
    }


}
