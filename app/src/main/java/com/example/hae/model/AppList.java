package com.example.hae.model;

import android.graphics.drawable.Drawable;

public class AppList {
    public String name;
    public Drawable icon;
    public String packages;

    public AppList(String name, Drawable icon, String packages) {
        this.name = name;
        this.icon = icon;
        this.packages = packages;
    }
}