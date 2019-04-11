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
                            ramadanCallback.onRamadanDataListRecieved(response);
                        } catch (Exception e) {
                            ramadanCallback.onError();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ramadanCallback.onError();
            }
        });

        queue.add(stringRequest);
    }

    public interface RamadanCallback {
        public void onRamadanDataListRecieved(String response);
        public void onError();
    }

}
