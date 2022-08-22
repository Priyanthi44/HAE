package com.example.hae.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.hae.HAEApplication;
import com.example.hae.R;
import com.example.hae.model.AppList;
import com.example.hae.model.DownloadCallback;
import com.example.hae.model.WeatherData;
import com.example.hae.model.weather;
import com.example.hae.view.adapters.ViewPagerAdapter;
import com.example.hae.viewmodel.MainActivityViewModel;

import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView batteryLevel;
    private Handler sliderHandler = new Handler();
    private MainActivityViewModel mainActivityViewModel;
    private ViewPager2 viewPager2;
    private RecyclerView recyclerView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpViews();
        downloadAppsList();
        downloadData();

    }

    private void downloadAppsList() {
        PackageManager pm = getPackageManager();
        List<AppList> apps = new ArrayList<>();
        @SuppressLint("QueryPermissionsNeeded") List<PackageInfo> packs = getPackageManager().getInstalledPackages(PackageManager.GET_PERMISSIONS);
        for (int i = 0; i < packs.size(); i++) {
                PackageInfo p = packs.get(i);
                if (!isSystemPackage(p)) {
                    String appName = p.applicationInfo.loadLabel(getPackageManager()).toString();
                    int icon = p.applicationInfo.icon;
                    String packages = p.applicationInfo.packageName;
                    apps.add(new AppList(appName, icon, packages));
                }
            }
            mainActivityViewModel.setAppsList(apps);
        }

    private void downloadData() {
        for (int i = 0; i < WeatherData.numberOfCities(); i++) {
            UrlRequest.Builder requestBuilder = getHaeApplication().getCronetEngine().newUrlRequestBuilder(
                    WeatherData.getCity(i), new DownloadCallback() {
                        @Override
                        protected void onSucceeded(UrlRequest request, UrlResponseInfo info, String json) {
                            weather w = new weather();
                            json = json.replace("\\", "");
                            try {
                                JSONObject jsonObject = new JSONObject(json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1));
                                String array = jsonObject.getString("city");
                                w.city = array;
                                array = jsonObject.getString("country");
                                w.country = array;
                                int tmp = jsonObject.getInt("temperature");
                                w.temperature = tmp;
                                array = jsonObject.getString("description");
                                w.description = array;
                                mainActivityViewModel.setWeather(w);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                mainActivityViewModel.setWeather(w);
                            }
                        }

                        @Override
                        protected void onFailed(UrlRequest request, UrlResponseInfo info, String bodyBytes) {

                        }


                    }, getHaeApplication().getCronetCallbackExecutorService());

            UrlRequest request = requestBuilder.build();
            request.start();

        }
    }

    private HAEApplication getHaeApplication() {
        return (HAEApplication) getApplication();
    }

    private void setUpViews() {
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        recyclerView =findViewById(R.id.recycler_view);
        recyclerView.setVisibility(View.GONE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        Button button = findViewById(R.id.launch_apps);
        Context context =this;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //list the apps in the phone
                if(button.getText().toString().equalsIgnoreCase(getResources().getString(R.string.app_launcher))) {
                    recyclerView.setVisibility(View.VISIBLE);
                    ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(null, mainActivityViewModel.getAppsList());
                    viewPagerAdapter.setOnItemClickListener(onItemClickListener);
                    viewPagerAdapter.getItemClicked().observe((LifecycleOwner) context, new Observer<View>() {
                        @Override
                        public void onChanged(View view) {
                            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
                            int pos =viewHolder.getAdapterPosition();
                            Intent intent = getPackageManager().getLaunchIntentForPackage( mainActivityViewModel.getAppsList().get(pos).packages);
                            if(intent != null){
                                startActivity(intent);
                            }
                        }
                    });
                    recyclerView.setAdapter(viewPagerAdapter);
                    button.setText(getResources().getString(R.string.back));
                }else{
                    recyclerView.setVisibility(View.GONE);
                    button.setText(getResources().getString(R.string.app_launcher));
                }
            }


        });
        this.registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        batteryLevel = findViewById(R.id.bat_level);
        progressBar = findViewById(R.id.progressBar);
        viewPager2 = findViewById(R.id.view_pager);


        mainActivityViewModel.getWeatherListSet().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    // set up the recycler
                    ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(mainActivityViewModel.getWeatherList(), viewPager2);
                    viewPager2.setAdapter(viewPagerAdapter);
                    viewPager2.setClipToPadding(false);
                    viewPager2.setClipChildren(false);
                    viewPager2.setOffscreenPageLimit(3);
                    viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
                    CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                    compositePageTransformer.addTransformer(new MarginPageTransformer(40));
                    compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
                        @Override
                        public void transformPage(@NonNull View page, float position) {
                            float r = 1 - Math.abs(position);
                            page.setScaleY(0.85f + r * 0.15f);
                        }
                    });

                    viewPager2.setPageTransformer(compositePageTransformer);

                    viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            super.onPageSelected(position);
                            sliderHandler.removeCallbacks(sliderRunnable);
                            sliderHandler.postDelayed(sliderRunnable, 2000);
                        }

                    });
                }
            }
        });
    }
    private boolean isSystemPackage(PackageInfo p) {
       return (p.applicationInfo.flags == ApplicationInfo.FLAG_SYSTEM);
    }
    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            progressBar.setProgress(level);
            batteryLevel.setText("Battery Level is  " + level + " %");
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }
    private final View.OnClickListener onItemClickListener =new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };
}