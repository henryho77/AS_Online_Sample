package com.example.mapsample.app;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

/**
 * Created by LITT on 2016/12/10.
 */

public class Config {

    public final static boolean isDebug = true;
    public final static String TAG = "debug";
    private static ACProgressFlower dialog;

    public static void LOGD(final String msg) {
        if (isDebug) {
            Log.d(TAG, msg);
        }
    }

    public static void TOAST(final Context context, final String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showProgress(Context context) {
        dialog = new ACProgressFlower.Builder(context)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE).bgAlpha(0)
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);
        dialog.show();


    }

    public final static void dismissProgress() {
        dialog.hide();
    }

}
