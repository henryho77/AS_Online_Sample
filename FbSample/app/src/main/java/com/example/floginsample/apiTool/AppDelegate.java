package com.example.floginsample.apiTool;

import android.app.Application;

public class AppDelegate extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ApiTool ap = new ApiTool(getApplicationContext());
    }
}
