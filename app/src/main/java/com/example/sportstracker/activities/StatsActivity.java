package com.example.sportstracker.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.sportstracker.data.Database;
import com.example.sportstracker.dialogs.LoadingDialog;
import com.example.sportstracker.R;
import com.example.sportstracker.RoutesMethods;
import com.example.sportstracker.adapters.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

/**
 * Activity for showing global stats.
 */
public class StatsActivity extends AppCompatActivity {

    private RoutesMethods routesMethods = new RoutesMethods();

    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        Database database = new Database(this);

        //loadingDialog.startLoadingDialog();

        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(7);
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), 0);

        for (int i = 1; i <= 8; i++) {
            if (i < 8)
                sectionsPagerAdapter.addPage(database.getType(i));
            else
                sectionsPagerAdapter.addPage("All");
        }

        loadingDialog = new LoadingDialog(StatsActivity.this);
        loadingDialog.startLoadingDialog();

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
//                ImportRunnable importRunnable = new ImportRunnable(sectionsPagerAdapter);
//                new Thread(importRunnable).start();

//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                viewPager.setAdapter(sectionsPagerAdapter);
                for (int i = 0; i < 8; i++) {
                    TabLayout.Tab tab = tabLayout.getTabAt(i);
                    if (tab != null) {
                        if (i < 7)
                            tab.setIcon(routesMethods.getIcon(i + 1));
                        else
                            tab.setIcon(R.drawable.ic_all);
//                      tab.setTabLabelVisibility(TabLayout.TAB_LABEL_VISIBILITY_UNLABELED);
                        if (tab.getIcon() != null)
                            tab.getIcon().setTint(getColor(R.color.colorIcon));
                    }
                }
                loadingDialog.dismissDialog();
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                //Toast.makeText(getApplicationContext(), "Load Successful: ", Toast.LENGTH_LONG).show();

            }
        });


//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                //loadingDialog.startLoadingDialog();
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                //loadingDialog.startLoadingDialog();
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                loadingDialog.dismissDialog();
//            }
//        });

    }

//    private void load(SectionsPagerAdapter sectionsPagerAdapter) {
//        ImportRunnable importRunnable = new ImportRunnable(sectionsPagerAdapter);
//        new Thread(importRunnable).start();
//
//        //loadingDialog.startLoadingDialog();
//        //viewPager.setAdapter(sectionsPagerAdapter);
//        //loadingDialog.dismissDialog();
//    }

//    private class ImportRunnable implements Runnable {
//        private SectionsPagerAdapter sectionsPagerAdapter;
//
//        ImportRunnable(SectionsPagerAdapter sectionsPagerAdapter) {
//            this.sectionsPagerAdapter = sectionsPagerAdapter;
//        }
//
//        @Override
//        public void run() {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    //loadingDialog.startLoadingDialog();
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
//                }
//            });
//            viewPager.post(new Runnable() {
//                @Override
//                public void run() {
//                    viewPager.setAdapter(sectionsPagerAdapter);
//                }
//            });
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    loadingDialog.dismissDialog();
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
//                    Toast.makeText(getApplicationContext(), "Load Successful: ", Toast.LENGTH_LONG).show();
//                }
//            });
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_stats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(StatsActivity.this);
            builder.setMessage("Delete all activities?").setCancelable(true).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(StatsActivity.this);
                    builder.setTitle("Are You Sure?");
                    builder.setMessage("All activities of each type will be deleted.\nThis cannot be undone.").setCancelable(true).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //database.deleteAll();
                            finish();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            Log.d("Stats_LC", "All Item deleted");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
