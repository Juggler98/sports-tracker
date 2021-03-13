package com.example.sportstracker;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ServiceImport extends Service {
    private ArrayList<Point> points;
    private Database database;

    ServiceImport(ArrayList<Point> points) {
        this.points = points;
        this.database = new Database(this);
    }

    ServiceImport() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        for (Point point : points) {
            database.addPoint(point);
        }
        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
        stopSelf();
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
