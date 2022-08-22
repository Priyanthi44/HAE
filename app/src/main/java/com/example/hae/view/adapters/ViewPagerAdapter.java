package com.example.hae.view.adapters;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.hae.R;
import com.example.hae.model.AppList;
import com.example.hae.model.weather;

import java.util.List;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {
    private final ViewPager2 viewPager2;
    List<weather> weatherList;
    List<AppList> apps;
    private View.OnClickListener mOnItemClickListener;
    MutableLiveData<View> itemClicked = new MutableLiveData<>();
    public ViewPagerAdapter(ViewPager2 viewPager2, List<AppList> apps) {
        this.viewPager2 = viewPager2;
        this.apps = apps;

    }


    public ViewPagerAdapter(List<weather> weatherList, ViewPager2 viewPager2) {
        this.weatherList = weatherList;
        this.viewPager2 = viewPager2;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewPager2 != null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.country_layout, parent, false);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_item_layout, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(viewPager2 !=null) {
            holder.city.setText(weatherList.get(position).city);
            holder.country.setText(weatherList.get(position).country);
            holder.temperature.setText(weatherList.get(position).temperature + "\u2103");
            holder.description.setText(weatherList.get(position).description);

            if (position == weatherList.size() - 2) {
                viewPager2.post(runnable);
            }
        }else{
            holder.appName.setText(apps.get(position).name);
            int icon =apps.get(position).icon;
            if(icon>0) {
                try {
                    holder.appName.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
                }catch(Resources.NotFoundException e){
                    e.printStackTrace();
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemClicked.setValue(view);
                    }
                });
            }
        }
    }
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            weatherList.addAll(weatherList);
            notifyDataSetChanged();
        }
    };
    @Override
    public int getItemCount() {
        if (viewPager2 !=null) {
            return weatherList.size();
        } else{
            return apps.size();
        }
    }
    public LiveData<View> getItemClicked(){
        return itemClicked;
    }
    public void setOnItemClickListener(View.OnClickListener listener) {
        mOnItemClickListener = listener;
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        TextView city;
        TextView country;
        TextView temperature;
        TextView description;
        TextView appName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            city =itemView.findViewById(R.id.city);
            country =itemView.findViewById(R.id.country);
            temperature=itemView.findViewById(R.id.temp);
            description =itemView.findViewById(R.id.desc);
            appName =itemView.findViewById(R.id.app);

                itemView.setTag(this);
                itemView.setOnClickListener(mOnItemClickListener);
        }
    }


}
