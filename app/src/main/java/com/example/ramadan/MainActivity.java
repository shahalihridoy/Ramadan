package com.example.ramadan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Timer;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    int fragmentContainer;

    static MonthlyPrayerTime monthlyPrayerTime;
    static DailyPrayerTime dailyPrayerTime;
    static RamadanTime ramadanTime;
    static Dashboard dashboard;

    LocationTracker locationTracker;
    SpinnerCircle spinnerCircle = null;

    SharedPreferences.Editor editor;
    SharedPreferences preferences;
    private static final String MY_PREFS_NAME = "Ramadan";
    String address;

    Double lat,lon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        init();
    }

    void init() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentContainer = R.id.fragmentContainer;

        dashboard = new Dashboard();
        dailyPrayerTime = new DailyPrayerTime();
        ramadanTime = new RamadanTime();
        monthlyPrayerTime = new MonthlyPrayerTime();

        preferences = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

        fragmentTransaction.replace(fragmentContainer, dashboard).commit();

        if(!preferences.contains("address")) {
            spinnerCircle = new SpinnerCircle(this);
            spinnerCircle.spin("Loading...","Fetching data");
        }
        new Thread() {
            @Override
            public void run() {
                getRequiredData();
            }
        }.start();

    }

    void getRequiredData() {

        final ApiCaller apiCaller = new ApiCaller(this, new ApiCaller.PrayerTimeCallback() {
            @Override
            public void onDailyPrayerTimeRecieved(String response) {
                editor.putString("daily", response);
                editor.commit();

//                notify daily prayer fragment
                if(dailyPrayerTime.isViewCreated)
                    dailyPrayerTime.init();
            }

            @Override
            public void onMonthlyPrayerTimeRecieved(String response) {
                editor.putString("monthly",response);
                editor.commit();

//                notify daily prayer fragment
                if(monthlyPrayerTime.isViewCreated)
                    monthlyPrayerTime.init();
            }

            @Override
            public void onError() {
                Toast.makeText(MainActivity.this, "Check GPS and internet connection for data update",Toast.LENGTH_LONG).show();
            }
        });

        locationTracker = new LocationTracker(this, new LocationTracker.LocationTrackerCallback() {
            @Override
            public void onLocationTracked(Double latitude, Double longitude, String address) {

                editor.putString("address",address);
                editor.commit();

                apiCaller.getDailyPrayerTime(latitude,longitude);
                apiCaller.getMonthlyPrayerTime(latitude,longitude);

                new RamadanTimeCollector(MainActivity.this, new RamadanTimeCollector.RamadanCallback() {
                    @Override
                    public void onRamadanDataListRecieved(String response) {
                        editor.putString("ramadan",response);
                        editor.commit();

//                        dismiss spinner if exists
                        if (spinnerCircle != null)
                            spinnerCircle.progressDialog.dismiss();

//                        notify ramadan fragment
                        if(ramadanTime.isViewCreated)
                            ramadanTime.init();
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(MainActivity.this,"Check internet connection for data update",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        locationTracker.startLocationUpdates();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, dailyPrayerTime);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_gallery) {

            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, ramadanTime);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_slideshow) {

            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, monthlyPrayerTime);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_manage) {

            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, dashboard);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
//        locationTracker.startLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        locationTracker.startLocationUpdates();
    }


}
