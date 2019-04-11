package com.example.ramadan;

import android.content.Context;
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

    ListView listView;
    TextView addressView;
    RamadanTimeAdapter ramadanTimeAdapter;
    List<DataFormatter.RamadanData> ramadanDataList;

    boolean isViewCreated = false;

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
        isViewCreated = true;
        init();
    }

    public void init() {
        DataFormatter dataFormatter = new DataFormatter(getContext());
        ramadanDataList = dataFormatter.getRamadanDataList();
        ramadanTimeAdapter = new RamadanTimeAdapter();

        addressView = (TextView) getView().findViewById(R.id.address);
        addressView.setText(dataFormatter.getAdress());

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
            return ramadanDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return ramadanDataList.get(position);
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
            vh.date.setText(ramadanDataList.get(position).date);
            vh.sehri.setText(ramadanDataList.get(position).sehri);
            vh.iftar.setText(ramadanDataList.get(position).iftar);

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
