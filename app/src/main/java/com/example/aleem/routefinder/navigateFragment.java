package com.example.aleem.routefinder;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;

import java.util.ArrayList;
import java.util.List;


public class navigateFragment extends Fragment {

    List<Integer> images = new ArrayList<>();
    HorizontalInfiniteCycleViewPager pager;

    public navigateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigate, container, false);

        initData();

        pager = view.findViewById(R.id.horizontal_cycle);
        MyAdapterNavigate myAdapter = new MyAdapterNavigate(images, getContext());
        pager.setAdapter(myAdapter);


        return view;
    }

    private void initData() {
        images.add(R.drawable.navigate);
        images.add(R.drawable.share);
    }

}
