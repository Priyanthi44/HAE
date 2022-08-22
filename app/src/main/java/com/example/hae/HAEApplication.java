package com.example.hae;

import android.app.Application;
import android.content.Context;

import org.chromium.net.CronetEngine;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HAEApplication extends Application {
    private CronetEngine cronetEngine;
    private ExecutorService cronetCallbackExecutorService;
    private static HAEApplication haeApplication;
    public static HAEApplication getInstance() {
        return haeApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        cronetEngine = createDefaultCronetEngine(getApplicationContext());
        cronetCallbackExecutorService = Executors.newFixedThreadPool(4);
        if(haeApplication == null){
            haeApplication =new HAEApplication();
        }
    }

    private CronetEngine createDefaultCronetEngine(Context haeApplication) {
        CronetEngine.Builder myBuilder = new CronetEngine.Builder(haeApplication);
        return myBuilder.build();
    }

    public CronetEngine getCronetEngine() {
        return cronetEngine;
    }

    public ExecutorService getCronetCallbackExecutorService() {
        return cronetCallbackExecutorService;
    }

}
