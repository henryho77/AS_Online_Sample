package com.example.floginsample.app;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Config {

    public final static boolean isDebug = true;
    public final static String TAG = "debug";

    public static void LOGD(final String msg) {
        if (isDebug) {
            Log.d(TAG, msg);
        }
    }

    public static void TOAST(final Context context, final String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}
