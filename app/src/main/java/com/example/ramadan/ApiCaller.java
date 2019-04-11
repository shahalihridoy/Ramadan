package com.example.ramadan;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ApiCaller {

    Context context;
    Calendar calendar;

    PrayerTimeCallback prayerTimeCallback;

    ApiCaller(Context context, PrayerTimeCallback prayerTimeCallback) {
        this.context = context;
        this.prayerTimeCallback = prayerTimeCallback;
        calendar = Calendar.getInstance(TimeZone.getDefault());
    }

    public void getDailyPrayerTime(Double lat, Double lon) {

// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        String url = "http://api.aladhan.com/v1/timings/"
                + System.currentTimeMillis() / 1000
                + "?latitude="
                + lat
                + "&longitude="
                + lon
                + "&method=1";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        prayerTimeCallback.onDailyPrayerTimeRecieved(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                prayerTimeCallback.onError();
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void getMonthlyPrayerTime(Double lat, Double lon) {

        RequestQueue queue = Volley.newRequestQueue(context);

        String url = "http://api.aladhan.com/v1/calendar?latitude=" +
                lat +
                "&longitude=" +
                lon +
                "&method=1&month=" +
                (calendar.get(Calendar.MONTH) + 1) +
                "&year=" +
                (calendar.get(Calendar.YEAR));


// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(String response) {
                        prayerTimeCallback.onMonthlyPrayerTimeRecieved(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                prayerTimeCallback.onError();
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public interface PrayerTimeCallback {
        public void onDailyPrayerTimeRecieved(String dResponse);

        public void onMonthlyPrayerTimeRecieved(String mResponse);

        public void onError();
    }
}
