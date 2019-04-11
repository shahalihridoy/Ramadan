package com.example.ramadan;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class Dashboard extends Fragment {

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    public Dashboard() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setCardClick();
    }

    private void setCardClick() {

        CardView ramadan = (CardView) getView().findViewById(R.id.ramadan);
        CardView dailyPrayer = (CardView) getView().findViewById(R.id.dailyPrayer);
        CardView monthlyPrayer = (CardView) getView().findViewById(R.id.monthlyPrayer);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(v.getId());
                switch (v.getId()) {
                    case R.id.ramadan:
                        fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragmentContainer, MainActivity.ramadanTime);
                        fragmentTransaction.commit();
                        break;
                    case R.id.dailyPrayer:
                        fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragmentContainer, MainActivity.dailyPrayerTime);
                        fragmentTransaction.commit();
                        break;
                    case R.id.monthlyPrayer:
                        fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragmentContainer, MainActivity.monthlyPrayerTime);
                        fragmentTransaction.commit();
                        break;
                    default:
                        break;
                }
            }
        };

        ramadan.setOnClickListener(listener);
        dailyPrayer.setOnClickListener(listener);
        monthlyPrayer.setOnClickListener(listener);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
