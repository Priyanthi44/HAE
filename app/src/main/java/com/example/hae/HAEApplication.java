package com.example.hae;

import android.app.Application;
import android.content.Context;

import org.chromium.net.CronetEngine;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HAEApplication extends Application {
    private CronetEngine cronetEngine;
    private ExecutorService cronetCallbackExecutorService;


    @Override
    public void onCreate() {
        super.onCreate();
        cronetEngine = createDefaultCronetEngine(getApplicationContext());
        cronetCallbackExecutorService = Executors.newFixedThreadPool(8);

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
