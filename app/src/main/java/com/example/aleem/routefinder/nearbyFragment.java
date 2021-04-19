package com.example.aleem.routefinder;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;

import java.util.ArrayList;
import java.util.List;


public class nearbyFragment extends Fragment{

    List<Integer> images = new ArrayList<>();
    HorizontalInfiniteCycleViewPager pager;

    public nearbyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nearby, container, false);

        initData();

        pager = view.findViewById(R.id.horizontal_cycle);
        MyAdapter myAdapter = new MyAdapter(images, getContext());
        pager.setAdapter(myAdapter);


        return view;
    }

    private void initData() {
        images.add(R.drawable.hospital);
        images.add(R.drawable.banks);
        images.add(R.drawable.atms);
        images.add(R.drawable.pharmacy);
        images.add(R.drawable.market);
        images.add(R.drawable.resturants);
        images.add(R.drawable.mosque);
        images.add(R.drawable.school);
        images.add(R.drawable.university);
        images.add(R.drawable.bus);
        images.add(R.drawable.gastation);
        images.add(R.drawable.movie);
    }
}
