package com.example.hae.viewmodel;

import android.content.pm.ApplicationInfo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hae.model.AppList;
import com.example.hae.model.WeatherData;
import com.example.hae.model.weather;

import java.util.ArrayList;
import java.util.List;

public class MainActivityViewModel extends ViewModel {
    MutableLiveData<Boolean> weatherListSet = new MutableLiveData<>();
    List<weather> weatherList = new ArrayList<>();
    List<AppList> apps;
    int count;

    public void setWeather(weather w) {
        count++;
        if (w.city !=null) {
            weatherList.add(w);
        }
        if (count == WeatherData.numberOfCities()) {
            weatherListSet.postValue(true);
        }
    }

    public LiveData<Boolean> getWeatherListSet() {
        return weatherListSet;
    }

    public List<weather> getWeatherList() {
        return weatherList;
    }

    public void setAppsList(List<AppList> a) {
        apps = a;
    }

    public List<AppList> getAppsList() {
        return apps;
    }
}
