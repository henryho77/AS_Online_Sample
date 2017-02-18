package com.example.floginsample.apiTool;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


public class ApiTool {

    // log
    public final static boolean isDebug = true;
    public final static String TAG = "debug";
    public static  SharedPreferences settings;
    public static  SharedPreferences.Editor spEditor;
    public static Context context ;

    public static String TOKEN = "token";
    public static String EMAIL = "email";
    public static String ID = "id";


    public ApiTool(Context con) {
        context = con;
        settings = context.getSharedPreferences("setting", 0);
        spEditor = settings.edit();
    }


    public static Boolean saveValue(String key, String val) {
//        Config.LOGD("spEditor.putString(key,val): " + key + " , " + val);
        spEditor.putString(key,val);
        spEditor.commit();
        return true;
    }

    public static String loadVaule(String key) {
        return settings.getString(key, null);
    }

    public static void clearValue() {
        spEditor.clear();
        spEditor.commit();
    }
}
