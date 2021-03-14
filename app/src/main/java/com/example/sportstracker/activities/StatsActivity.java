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

    private final RoutesMethods routesMethods = new RoutesMethods();
    private Database database;

    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        database = new Database(this);

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
                viewPager.setAdapter(sectionsPagerAdapter);
                for (int i = 0; i < 8; i++) {
                    TabLayout.Tab tab = tabLayout.getTabAt(i);
                    if (tab != null) {
                        if (i < 7)
                            tab.setIcon(routesMethods.getIcon(i + 1));
                        else
                            tab.setIcon(R.drawable.ic_all);
                        if (tab.getIcon() != null)
                            tab.getIcon().setTint(getColor(R.color.colorIcon));
                    }
                }
                loadingDialog.dismissDialog();
            }
        });

    }

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
                            database.deleteAll();
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
