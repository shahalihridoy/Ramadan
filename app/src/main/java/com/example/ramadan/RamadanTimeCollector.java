package com.example.ramadan;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class RamadanTimeCollector extends Thread{

    Context context;
    RamadanCallback ramadanCallback;
    List<RamadanData> ramadanDataList = new ArrayList<>();

    RamadanTimeCollector(Context context, RamadanCallback ramadanCallback) {
        this.context = context;
        this.ramadanCallback = ramadanCallback;
        loadHtml();
    }


    public void loadHtml() {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://www.islamicfinder.org/ramadan-calendar/";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            createRamadanDataList(response);
                            ramadanCallback.onRamadanDataListRecieved(ramadanDataList);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(stringRequest);
    }

    void createRamadanDataList(String response) {

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

    public interface RamadanCallback {
        public void onRamadanDataListRecieved(List<RamadanData> ramadanDataList);
    }

}
