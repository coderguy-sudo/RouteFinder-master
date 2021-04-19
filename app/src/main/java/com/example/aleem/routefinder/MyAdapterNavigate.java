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

public class MyAdapterNavigate extends PagerAdapter{
    List<Integer> images;
    Context context;
    LayoutInflater layoutInflater;

    public MyAdapterNavigate(List<Integer> images, Context context) {
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
                    Intent intent = new Intent(context, Navigater.class);
                    context.startActivity(intent);
                }else if(position == 1) {
                    Intent intent = new Intent(context, shareLocation.class);
                    context.startActivity(intent);
                }
            }
        });

        imageView.setImageResource(images.get(position));
        container.addView(view);
        return view;
    }
}
