package com.example.ramadan;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class DataFormatter {

    SharedPreferences.Editor editor;
    SharedPreferences preferences;
    private static final String MY_PREFS_NAME = "Ramadan";

    List<PrayerTime> monthlyPrayerTimeList = new ArrayList<>();
    List<String> dailyPrayerTimeList = new ArrayList<>();
    List<RamadanData> ramadanDataList = new ArrayList<>();

    List<RamadanData> dataList;

    SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
    SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");

    Context context;
    String response;

    DataFormatter(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
    }


    public List<String> getDailyPrayerData() {
        if(!preferences.contains("daily"))
            return dailyPrayerTimeList;

        response = preferences.getString("daily","");

        try {
            JSONObject jsonObject = new JSONObject(response).getJSONObject("data").getJSONObject("timings");
            dailyPrayerTimeList.add(convert24hTo12h(jsonObject.getString("Fajr")));
            dailyPrayerTimeList.add(convert24hTo12h(jsonObject.getString("Dhuhr")));
            dailyPrayerTimeList.add(convert24hTo12h(jsonObject.getString("Asr")));
            dailyPrayerTimeList.add(convert24hTo12h(jsonObject.getString("Maghrib")));
            dailyPrayerTimeList.add(convert24hTo12h(jsonObject.getString("Isha")));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dailyPrayerTimeList;
    }

    public List<PrayerTime> getMonthlyPrayerData() {

        if(!preferences.contains("monthly"))
            return monthlyPrayerTimeList;

        response = preferences.getString("monthly","");

        try {
            JSONObject jsonObject = new JSONObject(response);

            JSONArray jsonArray = jsonObject.getJSONArray("data");

            for (int i=0; i<jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i).getJSONObject("timings");
                PrayerTime prayerTime = new PrayerTime();
                prayerTime.date = jsonArray.getJSONObject(i).getJSONObject("date").getJSONObject("gregorian").getString("date");
                prayerTime.sunrise = convert24hTo12h(jsonObject.getString("Sunrise"));
                prayerTime.fajr = convert24hTo12h(jsonObject.getString("Fajr"));
                prayerTime.juhor = convert24hTo12h(jsonObject.getString("Dhuhr"));
                prayerTime.asr = convert24hTo12h(jsonObject.getString("Asr"));
                prayerTime.magrib = convert24hTo12h(jsonObject.getString("Maghrib"));
                prayerTime.isha = convert24hTo12h(jsonObject.getString("Isha"));
                prayerTime.sunset = convert24hTo12h(jsonObject.getString("Sunset"));

                monthlyPrayerTimeList.add(prayerTime);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return monthlyPrayerTimeList;
    }

    public String getAdress() {
        if(!preferences.contains("daily"))
            return "";
        return preferences.getString("address","");
    }

    List<RamadanData> getRamadanDataList() {

        if(!preferences.contains("ramadan"))
            return ramadanDataList;

        response = preferences.getString("ramadan","");

        Document doc = null;
        doc = Jsoup.parse(response);
        Elements rows = doc.select(".row-body");

        List<String> temp = new ArrayList<>();

        for (Element row : rows) {
            temp.clear();
            int i=1;
            for (Element data: row.select("td")) {
                if(i==2 || i==3 || i==6)
                    temp.add(data.text());
                i++;
            }

            RamadanData ramadanData = new RamadanData(
                    temp.get(0),
                    temp.get(1),
                    temp.get(2)
            );

            ramadanDataList.add(ramadanData);
        }

        return ramadanDataList;
    }

    public class PrayerTime {
        String fajr,juhor,asr,magrib,isha,sunrise,sunset,date;
    }

    public class RamadanData {
        String date;
        String iftar;
        String sehri;

        RamadanData(String date, String sehri, String iftar) {
            this.date = date;
            this.sehri = sehri;
            this.iftar = iftar;
        }
    }

    String convert24hTo12h(String time) {
        try {
            Date _24HourDt = _24HourSDF.parse(time);
            return _12HourSDF.format(_24HourDt);
        } catch (Exception e) {
        }
        return time;
    }




}
