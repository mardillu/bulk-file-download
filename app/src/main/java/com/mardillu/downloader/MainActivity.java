package com.mardillu.downloader;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.mardillu.bulkdownloader.ImageDownloaderException;
import com.mardillu.bulkdownloader.ImageDownloaderHelper;
import com.mardillu.bulkdownloader.ProgressModel;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
//            new ImageDownloaderHelper().setDownloadStatus(getCallback())
//                    .setUrl("https://5bc9d0eb57adaa001375b1c6.mockapi.io/sampleget")
//                    .setCollectionId(1)
//                    .createImageDownloadWorkURl();

            ArrayList<String> urls = new ArrayList<>();
            urls.add("https://i.stack.imgur.com/NXVu6.jpg");
//            urls.add("https://i.stack.imgur.com/NXVu6.jpg");
//            urls.add("https://i.stack.imgur.com/NXVu6.jpg");
//            urls.add("https://i.stack.imgur.com/NXVu6.jpg");

            new ImageDownloaderHelper().setUrls(urls)
                    .setDownloadStatus(getCallback())
                    .setDownloadDir(Environment.getExternalStorageDirectory() + "/MERGDATA/reference_Images")
                    .setCollectionId(100)
                    .create();

        } catch (ImageDownloaderException e) {
            e.printStackTrace();
        }
    }

    public ImageDownloaderHelper.DownloadStatus getCallback() {
        return new ImageDownloaderHelper.DownloadStatus() {
            @Override
            public void DownloadedItems(int totalurls, int downloadPercentage, int successPercent, int failurePercent) {
//               You can get Total Image Downloaded progress here
                Log.d(TAG, "DownloadedItems: " + downloadPercentage);
            }

            @Override
            public void CurrentDownloadPercentage(LinkedHashMap<String, ProgressModel> trackRecord) {
//              You can get current file downloaded progress here.
//                Log.d(TAG, "CurrentDownloadPercentage: " + trackRecord.size());
//                for (ProgressModel progressModel : trackRecord.values()) {
////                   Percentage downloaded
//                    Log.d(TAG, "CurrentDownloadPercentage: getProgress: " + progressModel.getProgress());
////                    Downloaded Size in bytes
//                    Log.d(TAG, "CurrentDownloadPercentage: getDownloadedSize: " + progressModel.getDownloadedSize());
////                    Downloaded Size in MB
//                    Log.d(TAG, "CurrentDownloadPercentage: getDownloadedSizeInMB: " + progressModel.getDownloadedSizeInMB());
////                    File Size in bytes
//                    Log.d(TAG, "CurrentDownloadPercentage: getFileSize: " + progressModel.getFileSize());
////                    File Size in MB
//                    Log.d(TAG, "CurrentDownloadPercentage: getFileSizeInMB: " + progressModel.getFileSizeInMB());
//                }
            }

            @Override
            public void onRetrievedBitmap(@Nullable Bitmap bitmap) {
                Log.d(TAG, "onRetrievedBitmap: ");
            }

        };
    }
}
