package com.example.aleem.routefinder;


import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

public class MyAdapter extends PagerAdapter {

    List<Integer> images;
    Context context;
    LayoutInflater layoutInflater;

    public MyAdapter(List<Integer> images, Context context) {
        this.images = images;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        View view = layoutInflater.inflate(R.layout.card_item, container, false);
        final ImageView imageView = view.findViewById(R.id.imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position == 0) {
                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("HOSPITAL", "hospital");
                    context.startActivity(intent);
                }else if(position == 1) {
                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("BANK", "bank");
                    context.startActivity(intent);
                }else if(position == 2) {
                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("ATM", "atm");
                    context.startActivity(intent);
                }else if(position == 3) {
                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("PHARMACY", "pharmacy");
                    context.startActivity(intent);
                }else if(position == 4) {
                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("MARKET", "supermarket");
                    context.startActivity(intent);
                }else if(position == 5) {
                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("RESTAURANTS", "restaurant");
                    context.startActivity(intent);
                }else if(position == 6) {
                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("MOSQUES", "mosque");
                    context.startActivity(intent);
                }else if(position == 7) {
                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("SCHOOL", "school");
                    context.startActivity(intent);
                }else if(position == 8) {
                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("UNIVERSITY", "university");
                    context.startActivity(intent);
                }else if(position == 9) {
                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("BUS", "bus_station");
                    context.startActivity(intent);
                }else if(position == 10) {
                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("PETROL", "gas_station");
                    context.startActivity(intent);
                }else if(position == 11) {
                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("MOVIE", "movie_theater");
                    context.startActivity(intent);
                }
            }
        });

        imageView.setImageResource(images.get(position));
        container.addView(view);
        return view;
    }
}
