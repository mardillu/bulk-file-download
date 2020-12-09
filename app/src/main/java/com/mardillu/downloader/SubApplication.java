package com.mardillu.downloader;

import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.mardillu.bulkdownloader.BaseApplication;

public class SubApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Receiver receiver = new Receiver();

        IntentFilter filter = new IntentFilter(BaseApplication.BULK_DOWNLOADER_NOTIFICATION);

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }
}
