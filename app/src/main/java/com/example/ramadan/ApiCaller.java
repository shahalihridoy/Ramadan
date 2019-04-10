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

    List<PrayerTime> monthlyPrayerTimeList = new ArrayList<>();
    List<String> dailyPrayerTimeList = new ArrayList<>();
    PrayerTimeCallback prayerTimeCallback;

    SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
    SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");

    ApiCaller(Context context, PrayerTimeCallback prayerTimeCallback) {
        this.context = context;
        this.prayerTimeCallback = prayerTimeCallback;
        calendar = Calendar.getInstance(TimeZone.getDefault());
    }

    public void getDailyPrayerTime(Double lat, Double lon) {

// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        String url ="http://api.aladhan.com/v1/timings/"
                +System.currentTimeMillis()/1000
                +"?latitude="
                +lat
                +"&longitude="
                +lon
                +"&method=1";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response).getJSONObject("data").getJSONObject("timings");
                            dailyPrayerTimeList.add(convert24hTo12h(jsonObject.getString("Fajr")));
                            dailyPrayerTimeList.add(convert24hTo12h(jsonObject.getString("Dhuhr")));
                            dailyPrayerTimeList.add(convert24hTo12h(jsonObject.getString("Asr")));
                            dailyPrayerTimeList.add(convert24hTo12h(jsonObject.getString("Maghrib")));
                            dailyPrayerTimeList.add(convert24hTo12h(jsonObject.getString("Isha")));

                            prayerTimeCallback.onDailyPrayerTimeRecieved(dailyPrayerTimeList);

                        } catch (JSONException e) {
                            prayerTimeCallback.onError();
                        }
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

        String url ="http://api.aladhan.com/v1/calendar?latitude=" +
                lat +
                "&longitude=" +
                lon +
                "&method=1&month=" +
                (calendar.get(Calendar.MONTH)+1)+
                "&year=" +
                (calendar.get(Calendar.YEAR));


// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray jsonArray = jsonObject.getJSONArray("data");
//
//                            System.out.println(jsonArray.getJSONObject(0)
//                                    .getJSONObject("date").
//                                            getJSONObject("gregorian").getString("date"));

                            for (int i=0; i<jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i).getJSONObject("timings");
                                PrayerTime prayerTime = new PrayerTime();
                                prayerTime.date = jsonArray.getJSONObject(i).getJSONObject("date").getJSONObject("gregorian").getString("date");
                                System.out.println(prayerTime.date);
                                prayerTime.sunrise = convert24hTo12h(jsonObject.getString("Sunrise"));
                                System.out.println(prayerTime.sunrise);
                                prayerTime.fajr = convert24hTo12h(jsonObject.getString("Fajr"));
                                System.out.println(prayerTime.fajr);
                                prayerTime.juhor = convert24hTo12h(jsonObject.getString("Dhuhr"));
                                System.out.println(prayerTime.juhor);
                                prayerTime.asr = convert24hTo12h(jsonObject.getString("Asr"));
                                prayerTime.magrib = convert24hTo12h(jsonObject.getString("Maghrib"));
                                prayerTime.isha = convert24hTo12h(jsonObject.getString("Isha"));
                                prayerTime.sunset = convert24hTo12h(jsonObject.getString("Sunset"));

                                monthlyPrayerTimeList.add(prayerTime);
                            }

                            prayerTimeCallback.onMonthlyPrayerTimeRecieved(monthlyPrayerTimeList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            prayerTimeCallback.onError();
                        }
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

    public class PrayerTime {
        String fajr,juhor,asr,magrib,isha,sunrise,sunset,date;
    }

    String convert24hTo12h(String time) {
        try {
            Date _24HourDt = _24HourSDF.parse(time);
            return _12HourSDF.format(_24HourDt);
        } catch (Exception e) {
        }
        return time;
    }

    public interface PrayerTimeCallback {
        public void onDailyPrayerTimeRecieved(List<String> data);
        public void onMonthlyPrayerTimeRecieved(List<PrayerTime> data);
        public void onError();
    }
}
