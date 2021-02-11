package com.example.sportstracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {

    private static final String TABLE_TYPE = "type_activity";
    private static final String TABLE_ACTIVITY = "activity";
    private static final String TABLE_POINT = "point";

    public Database(@Nullable Context context) {
        super(context, "tracking.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableTypeActivity = "CREATE TABLE " + TABLE_TYPE +
                "(id_type_activity INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "type TEXT NOT NULL)";

        String createTableActivity = "CREATE TABLE " + TABLE_ACTIVITY +
                "(id_activity INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "type_id INTEGER NOT NULL," +
                "time_start TEXT NOT NULL," +
                "time_end TEXT," +
                "title TEXT," +
                "FOREIGN KEY(type_id) REFERENCES " + TABLE_TYPE + "(id_type_activity))";

        String createTablePoint = "CREATE TABLE " + TABLE_POINT +
                "(id_point INTEGER NOT NULL, " +
                "id_activity INTEGER NOT NULL," +
                "lat REAL NOT NULL," +
                "lon REAL NOT NULL," +
                "ele REAL NOT NULL," +
                "time TEXT NOT NULL," +
                "speed REAL," +
                "hdop REAL," +
                "course REAL," +
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

    private boolean createActivity(Activity activity) {
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
            return true;
        }
    }

    private boolean addPoint(Activity activity, Point point) {
        Log.d("DB_LC", "DB_ADD_Point");

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("id_point", point.getId());
        cv.put("id_activity", activity.getId());
        cv.put("lat", point.getLat());
        cv.put("lon", point.getLon());
        cv.put("ele", point.getEle());
        cv.put("time", point.getTime());
        if (point.getSpeed() != -1)
            cv.put("speed", point.getSpeed());
        if (point.getHdop() != -1)
            cv.put("hdop", point.getHdop());
        if (point.getCourse() != -1)
            cv.put("course", point.getCourse());
        long insert = db.insert(TABLE_POINT, null, cv);
        db.close();

        if (insert == -1) {
            return false;
        } else {
            return true;
        }
    }



    public void checkTypes() {
        String queryString = "SELECT * FROM " + TABLE_TYPE;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);
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
        Cursor cursor = db.rawQuery(queryString,null);
        String typeStr = "";
        if (cursor.moveToFirst()) {
            typeStr = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return typeStr;
    }


}
