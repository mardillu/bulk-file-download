package com.mardillu.bulkdownloader;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDexApplication;

public class BaseApplication extends MultiDexApplication {
    private static Context context;
    public static String BULK_DOWNLOADER_NOTIFICATION = "bulk_downloader.notification";
    static LocalData localData;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        localData = new LocalData(getAppContext());
    }

    public static Context getAppContext() {
        return context;
    }

    public static LocalData getLocalData() {
        return localData;
    }
}
