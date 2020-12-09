package com.mardillu.bulkdownloader;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class MessagerHandler {

    /**
     * This method used to send current status of downloaded image
     *
     * @param messageID unique id
     * @param params    message to sent
     */
    public static void sendMessage(int messageID, @Nullable String params, @Nullable Bundle bundle) {
        Messenger mActivityMessenger = null;
        if (ImageDownloaderHelper.mHandler != null) {
            mActivityMessenger = new Messenger(ImageDownloaderHelper.mHandler);
        } else {
            switch (messageID) {
                case 1:
                    //Logic for progressing the notification
                    Intent intent = new Intent(BaseApplication.BULK_DOWNLOADER_NOTIFICATION);
                    intent.putExtra("downloadStatusModel",bundle.getParcelable("downloadStatusModel"));
                    LocalBroadcastManager.getInstance(BaseApplication.getAppContext()).sendBroadcast(intent);
            }
        }
        // If this service is launched by the JobScheduler, there's no callback Messenger. It
        // only exists when the MainActivity calls startService() with the callback in the Intent.

        if (mActivityMessenger == null)
            return;

        Message m = Message.obtain();
        m.what = messageID;
        m.obj = params;
        m.setData(bundle);
        try {
            mActivityMessenger.send(m);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * This handler is used for getting Success / Failure message from Work Manager Downloading image
     */
    protected static class IncomingMessageHandler extends Handler {
        int total, success = 0, failure = 0, percentage;
        ImageDownloaderHelper.DownloadStatus downloadStatus;
        private String TAG = "IncomingMessageHandler";
        private static LinkedHashMap<String, ProgressModel> trackRecord = new LinkedHashMap<>();
        private ArrayList<ProgressModel> list = new ArrayList<>();
        private String downloadDir;

        public IncomingMessageHandler(int total, ImageDownloaderHelper.DownloadStatus status, String downloadDir) {
            this.downloadStatus = status;
            this.total = total;
            this.downloadDir = downloadDir;
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String imageName = null;
            if (bundle != null){
                imageName = bundle.getString("image_name");
            }
            switch (msg.what) {
                case 1:
                    if (msg.getData() != null) {
                        if (msg.getData().getBoolean("state", false)) {
                            ResponseBodySerializable responseBodySerializable = (ResponseBodySerializable) msg.getData().getSerializable("stream");
                            Bitmap myBitmap = BitmapFactory.decodeStream(responseBodySerializable != null ? responseBodySerializable.getResponseBody() : null);
                            if (downloadStatus != null){
                                downloadStatus.onRetrievedBitmap(myBitmap);
                            }

                            if (myBitmap != null && imageName != null){
                                saveImageToDir(downloadDir, imageName, myBitmap);
                            }
                            success++;
                        } else {
                            failure++;
                        }
                        DownloadStatusModel downloadStatusModel = msg.getData().getParcelable("downloadStatusModel");
                        if (downloadStatus != null)
                            downloadStatus.DownloadedItems(downloadStatusModel.getTotal(), ((downloadStatusModel.getSuccess() + downloadStatusModel.getFailure()) * 100) / downloadStatusModel.getTotal(), downloadStatusModel.getSuccess(), downloadStatusModel.getFailure());
                    }
                    break;
                case 2:
                    if (msg.getData() != null) {
                        trackRecord.put(msg.getData().getString("url"), new ProgressModel(msg.getData().getFloat("fileSize", (float) 0.0), msg.getData().getFloat("currentSize", (float) 0.0)));
                        if (downloadStatus != null) {
                            downloadStatus.CurrentDownloadPercentage(trackRecord);
                        }
                    }
            }
        }

        void saveImageToDir(String dir, String imageName, Bitmap bitmap){
            String fn = dir + "/" + imageName;
            File pictureFile = new File(fn);
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }

}
