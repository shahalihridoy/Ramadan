package com.example.ramadan;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Toast;

import java.util.List;

public class MonthlyPrayerTime extends Fragment {

    ListView namajListView;

    LayoutInflater layoutInflater;
    LocationTracker locationTracker;
    SpinnerCircle spinner;
    String address;

    NamajListAdapter namajListAdapter;
    List<ApiCaller.PrayerTime> dataList;

    static Double lat,lon;
    Boolean isLocationTracked = false;


    public MonthlyPrayerTime() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_monthly_prayer_time, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        setting activity to portrait
        init();
    }

    public void init() {

        namajListView = getView().findViewById(R.id.list_view);
        spinner = new SpinnerCircle(getContext());
        layoutInflater = getLayoutInflater();

        final ApiCaller apiCaller =  new ApiCaller(getContext(), new ApiCaller.PrayerTimeCallback() {
            @Override
            public void onDailyPrayerTimeRecieved(List<String> data) {

            }

            @Override
            public void onMonthlyPrayerTimeRecieved(List<ApiCaller.PrayerTime> data) {
                dataList = data;
                namajListAdapter = new NamajListAdapter();
                setDataToList();
                spinner.progressDialog.dismiss();
            }

            @Override
            public void onError() {
                spinner.progressDialog.dismiss();
                Toast.makeText(getContext(),"Check internet connection. Trying again",Toast.LENGTH_LONG).show();

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
                                apiCaller.getMonthlyPrayerTime(latitude,longitude);
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
        namajListView.setAdapter(namajListAdapter);
    }

    class NamajListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = layoutInflater.inflate(R.layout.monthly_prayer_time_list,null);
            ViewHelper viewHelper = new ViewHelper();

            viewHelper.date = (TextView) view.findViewById(R.id.date);
            viewHelper.fajr = (TextView) view.findViewById(R.id.fajr);
            viewHelper.juhor = (TextView) view.findViewById(R.id.juhor);
            viewHelper.asor = (TextView) view.findViewById(R.id.asor);
            viewHelper.magrib = (TextView) view.findViewById(R.id.magrib);
            viewHelper.isha = (TextView) view.findViewById(R.id.isha);
            viewHelper.sunrise = (TextView) view.findViewById(R.id.sunrise);
            viewHelper.sunset = (TextView) view.findViewById(R.id.sunset);

            viewHelper.date.setText(dataList.get(position).date);
            viewHelper.fajr.setText(dataList.get(position).fajr);
            viewHelper.sunrise.setText(dataList.get(position).sunrise);
            viewHelper.juhor.setText(dataList.get(position).juhor);
            viewHelper.asor.setText(dataList.get(position).asr);
            viewHelper.sunset.setText(dataList.get(position).sunset);
            viewHelper.magrib.setText(dataList.get(position).magrib);
            viewHelper.isha.setText(dataList.get(position).isha);

            return view;
        }

    }

    class ViewHelper {
        TextView date;
        TextView sunrise;
        TextView fajr;
        TextView juhor;
        TextView asor;
        TextView magrib;
        TextView isha;
        TextView sunset;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onDetach();
    }

}
