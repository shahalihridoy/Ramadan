package com.example.ramadan;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;


public class DailyPrayerTime extends Fragment {

    ListView namajListView;
    TextView addressView;

    LayoutInflater layoutInflater;
    LocationTracker locationTracker;
    SpinnerCircle spinner;
    String address;

    static Double lat,lon;
    Boolean isLocationTracked = false;

    NamajListAdapter namajListAdapter;

    List<String> prayerTime;

    Gson gson;

    int[] icon = {R.drawable.ic_menu_send,R.drawable.ic_menu_gallery,R.drawable.ic_menu_camera,R.drawable.ic_menu_gallery,R.drawable.ic_menu_camera};
    String[] names = {"Fajr","Juhor","Asr","Magrib","Isha"};

    public DailyPrayerTime() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prayerTime = MainActivity.dailyPrayerTime.prayerTime;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
    }

    public void init() {

        spinner = new SpinnerCircle(getContext());
        layoutInflater = getLayoutInflater();

        namajListView = (ListView) getView().findViewById(R.id.list_view);
        addressView = (TextView) getView().findViewById(R.id.address);

        final ApiCaller apiCaller =  new ApiCaller(getContext(), new ApiCaller.PrayerTimeCallback() {
            @Override
            public void onDailyPrayerTimeRecieved(List<String> data) {
                prayerTime = data;
                namajListAdapter = new NamajListAdapter();
                setDataToList();
                spinner.progressDialog.dismiss();
            }

            @Override
            public void onMonthlyPrayerTimeRecieved(List<ApiCaller.PrayerTime> data) {

            }

            @Override
            public void onError() {
                spinner.progressDialog.dismiss();
            }
        });


//        fetching data with a new thread
        Thread locationTrackerThread = new Thread() {
            @Override
            public void run() {
                locationTracker = new LocationTracker(getContext(),
                        new LocationTracker.LocationTrackerCallback() {
                            @Override
                            public void onLocationTracked(Double latitude, Double longitude, String addres) {
                                lat = latitude;
                                lon = longitude;
                                address = addres;
                                isLocationTracked = true;
                                apiCaller.getDailyPrayerTime(latitude,longitude);
                            }
                        });
                locationTracker.startLocationUpdates();
            }
        };

        if(!isLocationTracked) {
            spinner.spin("Loading...","Fetching Data");
            locationTrackerThread.start();
        } else setDataToList();

    }

    void setDataToList() {
        addressView.setText(address);
        namajListView.setAdapter(namajListAdapter);
    }

    class NamajListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = layoutInflater.inflate(R.layout.daily_namaj_list,null);
            ViewHelper viewHelper = new ViewHelper();

            viewHelper.prayerName = (TextView) view.findViewById(R.id.prayer_name);
            viewHelper.prayerIcon = (ImageView) view.findViewById(R.id.prayer_icon);
            viewHelper.prayerTime = (TextView) view.findViewById(R.id.prayer_time);

            viewHelper.prayerName.setText(names[position]);
            viewHelper.prayerIcon.setImageResource(icon[position]);
            viewHelper.prayerTime.setText(prayerTime.get(position));

            return view;
        }

    }

    class ViewHelper {
        TextView prayerName;
        ImageView prayerIcon;
        TextView prayerTime;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dialy_prayer_time, container, false);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

}
