package com.example.hae.model;

public class WeatherData {
    private static String[] cityUrls= {
            "https://weather.bfsah.com/beijing",
            "https://weather.bfsah.com/berlin",
            "https://weather.bfsah.com/cardiff",
            "https://weather.bfsah.com/edinburgh",
            "https://weather.bfsah.com/london",
            "https://weather.bfsah.com/nottingham"
    };

    public static int numberOfCities() {
        return cityUrls.length;
    }

    public static String getCity(int position) {
        return cityUrls[position];
    }
}
