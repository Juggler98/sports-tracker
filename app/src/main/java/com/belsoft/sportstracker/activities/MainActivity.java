package com.belsoft.sportstracker.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.belsoft.sportstracker.dialogs.AboutAppDialog;
import com.belsoft.sportstracker.fragments.DashboardFragment;
import com.belsoft.sportstracker.data.Database;
import com.belsoft.sportstracker.dialogs.LoadingDialog;
import com.belsoft.sportstracker.fragments.MapFragment;
import com.belsoft.sportstracker.dialogs.PermissionDialog;
import com.belsoft.sportstracker.data.Point;
import com.belsoft.sportstracker.R;
import com.belsoft.sportstracker.data.Route;
import com.google.android.material.navigation.NavigationView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Entry activity for application.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PICK_GPX_FILE = 1;
    private DrawerLayout drawer;
    private Database database;

    private volatile LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MAIN_LC", "onCreate");
        setContentView(R.layout.activity_main);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getString(R.string.title_activity_main));

        database = new Database(MainActivity.this);
        database.checkTypes();

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }

        // permit start app only with getting GPS permission
        permission();

        loadingDialog = new LoadingDialog(MainActivity.this);
        loadingDialog.setText("Importing...");

    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int nightMode = Integer.parseInt(defaultSharedPreferences.getString(getString(R.string.nightModePref), "0"));

        switch (nightMode) {
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingDialog.dismissDialog();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d("Main_LC", "NavigationBar clicked");
        switch (item.getItemId()) {
            case R.id.nav_dashboard:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle("Dashboard");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();
                break;
            case R.id.nav_map:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (getSupportActionBar() != null)
                        getSupportActionBar().setTitle("Map");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapFragment()).commit();
                }
                break;
            case R.id.nav_import:
                openExplorer();
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_info:
                AboutAppDialog aboutAppDialog = new AboutAppDialog();
                aboutAppDialog.show(getSupportFragmentManager(), "about app");
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // check and ask for permission
    private void permission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.showProminentDisclosure();
        }
    }

    /**
     * Check if user permit GPS access. If yes, app will continue and he can start recording and if not app is closed.
     *
     * @param requestCode  requestCode
     * @param permissions  permissions
     * @param grantResults grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                String forbidden;
                if (shouldShowRequestPermissionRationale(permissions[0])) {
                    forbidden = "\n" + "GPS access is necessary for running app." + "\n";

                } else {
                    forbidden = "\n" + "GPS access is necessary for running app." + "\n\n" +
                            "For activation go to Settings -> Apps. " +
                            "Find Sports Tracker and activate GPS permission.";
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("GPS Permission").setMessage(forbidden).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        permission();
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                PermissionDialog permissionDialog = new PermissionDialog();
                permissionDialog.show(getSupportFragmentManager(), "permission_dialog");
            }
        }
    }

    private void showProminentDisclosure() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("GPS Permission").setMessage("Sports Tracker collects location data locally on your device in the background to show your routes on a map and calculate statistics. " +
                "It's necessary to permit access location for using app").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void openExplorer() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_GPX_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_GPX_FILE && resultCode == RESULT_OK) {
            if (data != null) {
                createRoute(data.getData());
            }
        }
    }

    private void createRoute(Uri uri) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int activityType = Integer.parseInt(defaultSharedPreferences.getString(getString(R.string.routeTypePref), "1"));

        ArrayList<Point> points = getPointsFromFile(uri, activityType);

        if (points.size() > 0) {
            database.updateActivity(database.getLastActivityID(), 0, points.get(points.size() - 1).getTime(), "");
            String routeName = this.getNameFromFile(uri);
            database.updateActivity(database.getLastActivityID(), 0, 0, routeName);

            //Import is running in new Thread
            ImportRunnable runnable = new ImportRunnable(points, routeName);
            new Thread(runnable).start();

        } else {
            Toast.makeText(this, "Import Failed", Toast.LENGTH_LONG).show();
        }
    }

    private class ImportRunnable implements Runnable {
        private final ArrayList<Point> points;
        private final String routeName;

        ImportRunnable(ArrayList<Point> points, String routeName) {
            this.points = points;
            this.routeName = routeName;
        }

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingDialog.startLoadingDialog();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                }
            });
            for (Point point : points) {
                database.addPoint(point);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingDialog.dismissDialog();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    Toast.makeText(getApplicationContext(), routeName + ": Import Successful", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private ArrayList<Point> getPointsFromFile(Uri uri, int activityType) {
        ArrayList<Point> points = new ArrayList<>();
        ArrayList<SimpleDateFormat> patterns = this.getDateFormats();
        int activityID = database.getLastActivityID() + 1;
        int pointID = 1;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream inputStream = getContentResolver().openInputStream(uri);

            if (inputStream != null) {
                Document document = builder.parse(inputStream);
                Element element = document.getDocumentElement();
                NodeList trksegList = element.getElementsByTagName("trkseg");
                for (int trkseg = 0; trkseg < trksegList.getLength(); trkseg++) {
                    if (trkseg > 0) {
                        points.get((points.size() - 1)).setPaused(true);
                    }
                    Node trksegNode = trksegList.item(trkseg);
                    if (trksegNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element trksegElement = (Element) trksegNode;
                        NodeList trkptList = trksegElement.getElementsByTagName("trkpt");

                        for (int i = 0; i < trkptList.getLength(); i++) {
                            double lat = 0;
                            double lon = 0;
                            double ele = 0;
                            double time = 0;
                            double speed = -1;
                            double course = -1;
                            Node trkpt = trkptList.item(i);
                            if (trkpt.getNodeType() == Node.ELEMENT_NODE) {
                                Element trkptElement = (Element) trkpt;
                                lat = Double.parseDouble(trkptElement.getAttribute("lat"));
                                lon = Double.parseDouble(trkptElement.getAttribute("lon"));

                                NodeList trkptChild = trkptElement.getElementsByTagName("ele");
                                ele = this.getDataFromNode(trkptChild);

                                trkptChild = trkptElement.getElementsByTagName("time");
                                if (trkptChild.getLength() > 0) {
                                    String content = trkptChild.item(0).getTextContent();
                                    Date date = null;
                                    for (SimpleDateFormat pattern : patterns) {
                                        try {
                                            date = pattern.parse(content);
                                            break;
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (date != null) {
                                        time = date.getTime();
                                    }
                                }

                                trkptChild = trkptElement.getElementsByTagName("speed");
                                trkptChild = trkptChild.getLength() == 0 ? trkptElement.getElementsByTagName("gpxtpx:speed") : trkptChild;
                                speed = this.getDataFromNode(trkptChild);

                                trkptChild = trkptElement.getElementsByTagName("course");
                                trkptChild = trkptChild.getLength() == 0 ? trkptElement.getElementsByTagName("gpxtpx:course") : trkptChild;
                                course = this.getDataFromNode(trkptChild);
                            }
                            if (trkseg == 0 && i == 0) {
                                Route route = new Route(activityType, time);
                                database.createActivity(route);
                                activityID = database.getLastActivityID();
                            }
                            Point point = new Point(activityID, pointID++, lat, lon, ele, time, speed, -1, course, -1);
                            points.add(point);
                        }
                    }
                }
                inputStream.close();
            }
        } catch (ParserConfigurationException | SAXException | NumberFormatException | IOException e) {
            e.printStackTrace();
            //Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return points;
    }

    private String getNameFromFile(Uri uri) {
        String routeName = "";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream inputStream = getContentResolver().openInputStream(uri);

            if (inputStream != null) {
                Document document = builder.parse(inputStream);
                Element element = document.getDocumentElement();
                NodeList trkList = element.getElementsByTagName("trk");
                if (trkList.getLength() > 0) {
                    Node trkNode = trkList.item(0);
                    if (trkNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element trkElement = (Element) trkNode;
                        NodeList nameList = trkElement.getElementsByTagName("name");
                        if (nameList.getLength() > 0) {
                            routeName = nameList.item(0).getTextContent();
                        }
                    }
                }
                inputStream.close();
            }
        } catch (ParserConfigurationException | SAXException | NumberFormatException | IOException e) {
            e.printStackTrace();
            //Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return routeName;
    }

    private double getDataFromNode(NodeList nodeList) {
        if (nodeList.getLength() > 0) {
            String content = nodeList.item(0).getTextContent();
            return Double.parseDouble(content);
        }
        return -1;
    }

    //List of Date pattern commonly used in gpx files according ISO_8601
    //https://en.wikipedia.org/wiki/ISO_8601
    //https://developer.android.com/reference/kotlin/java/text/SimpleDateFormat
    private ArrayList<SimpleDateFormat> getDateFormats() {
        ArrayList<SimpleDateFormat> patterns = new ArrayList<>();
        patterns.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()));
        patterns.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()));
        patterns.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault()));
        patterns.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault()));
        patterns.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault()));
        patterns.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault()));
        patterns.add(new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.getDefault()));
        patterns.add(new SimpleDateFormat("yyyyMMdd'T'HHmmssZ", Locale.getDefault()));
        patterns.add(new SimpleDateFormat("yyyyMMdd'T'HHmmssXXX", Locale.getDefault()));
        return patterns;
    }

}
