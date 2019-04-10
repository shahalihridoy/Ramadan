package com.example.ramadan;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;


public class RamadanTime extends Fragment {

    List<RamadanTimeCollector.RamadanData> dataList;
    Boolean isDataLoaded = false;
    SpinnerCircle spinner;
    ListView listView;
    RamadanTimeAdapter ramadanTimeAdapter;

    public RamadanTime() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ramadan_time, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        init();
    }

    private void init() {

        spinner = new SpinnerCircle(getContext());

        Thread t = new Thread() {
            @Override
            public void run() {
                super.run();
                new RamadanTimeCollector(getContext(), new RamadanTimeCollector.RamadanCallback() {
                    @Override
                    public void onRamadanDataListRecieved(List<RamadanTimeCollector.RamadanData> ramadanDataList) {
                        dataList = ramadanDataList;
                        isDataLoaded = true;
                        ramadanTimeAdapter = new RamadanTimeAdapter();
                        setDataToList();
                        spinner.progressDialog.dismiss();
                    }
                });
            }
        };


        if(!isDataLoaded) {
            spinner.spin("Loading...","Fetching data");
            t.start();

        } else setDataToList();

    }

    void setDataToList() {
        listView = (ListView) getView().findViewById(R.id.list_view);
        listView.setAdapter(ramadanTimeAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private class RamadanTimeAdapter extends BaseAdapter {

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

            View view = getLayoutInflater().inflate(R.layout.ramdan_list,null);
            ViewHelper vh = new ViewHelper();

            vh.serial = view.findViewById(R.id.serial);
            vh.date = view.findViewById(R.id.date);
            vh.sehri = view.findViewById(R.id.sehri);
            vh.iftar = view.findViewById(R.id.iftar);

            vh.serial.setText(Integer.toString(position+1));
            vh.date.setText(dataList.get(position).date);
            vh.sehri.setText(dataList.get(position).sehri);
            vh.iftar.setText(dataList.get(position).iftar);

            return view;
        }
    }

    private class ViewHelper {
        TextView serial;
        TextView date;
        TextView sehri;
        TextView iftar;
    }

}
