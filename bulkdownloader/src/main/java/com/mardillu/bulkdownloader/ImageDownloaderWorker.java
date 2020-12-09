package com.mardillu.bulkdownloader;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImageDownloaderWorker extends Worker {
    private static Gson gson = new Gson();
    private String TAG = getClass().getSimpleName();
    private List<String> g;
    private LocalData localData;
    private String fileName;
    private Context context;

    public ImageDownloaderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    /**
     * a method to convert Gson to String
     *
     * @param arrayList list of String to convert json
     * @return it return the json
     */
    public static synchronized String gsonToString(List<String> arrayList) {
        return gson.toJson(arrayList);
    }

    @NonNull
    @Override
    public Result doWork() {
        String[] value = getInputData().getStringArray("value");
        if (value == null) return Result.failure();
        final String downloadDir = getInputData().getString("download_dir");
        fileName = value[0];
        localData = new LocalData(context);
        g = toList(localData.getStringPreferenceValue(value[0]));
        final DownloadStatusModel downloadStatusModel = new DownloadStatusModel(BulkDownloaderConstant.DownloadStatusFileName(Integer.parseInt(value[1])), localData);
        final CountDownLatch startSignal = new CountDownLatch(g.size());
        for (final String s : g) {
            Log.d(TAG, "run: " + android.util.Patterns.WEB_URL.matcher(s).matches());

            final Result[] result = {Result.success()};
            final Request request = new Request.Builder()
                    .url(s)
                    .build();

            OkHttpClient client = CustomDownloadClient.getClient(true);

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    downloadStatusModel.increaseFailure();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("state", false);
                    bundle.putString("download_dir", downloadDir);
                    bundle.putString("image_name", "");
                    bundle.putParcelable("downloadStatusModel", downloadStatusModel);
                    MessagerHandler.sendMessage(1, "status", bundle);
                    result[0] = Result.failure();
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("state", true);
                    String[] urlParts = s.split("/");
                    String imageName = urlParts[urlParts.length-1];
                    bundle.putString("image_name", imageName);

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//                    Buffer of response inputStream length
                    byte[] buffer = new byte[(int) response.body().contentLength()];

                    int len;
//                    Read response inputStream aka byteStream and write it to buffer
                    while ((len = response.body().byteStream().read(buffer)) > -1) {
                        outputStream.write(buffer, 0, len);
                    }
//                   Frees space for future write operation
                    outputStream.flush();
                    InputStream is1 = new ByteArrayInputStream(outputStream.toByteArray());

                    downloadStatusModel.increaseSuccess();
                    bundle.putSerializable("stream", new ResponseBodySerializable(is1));
                    bundle.putParcelable("downloadStatusModel", downloadStatusModel);
                    bundle.putString("download_dir", downloadDir);
//                    Closes the stream permanently to ensure that
                    outputStream.close();

                    Log.d(TAG, "onResponse: " + downloadStatusModel.toString());

                    MessagerHandler.sendMessage(1, "status", bundle);
                    removeFromList(call.request().url().toString());
                    result[0] = Result.success();
                    startSignal.countDown();
                }
            });
//            To clear SharedPreference
        }
        try {
            startSignal.await();
            downloadStatusModel.resetState();
            if (toList(localData.getStringPreferenceValue(fileName)).size() == 0)
                ImageDownloaderHelper.removeFromWork(Integer.parseInt(value[1]));
            return Result.failure();
        } catch (InterruptedException e) {
            Log.d("InterruptedException", "doWork: ");
            e.printStackTrace();
            return Result.retry();
        }
    }

    List<String> toList(String g) {
        Gson gson = new Gson();
        return gson.fromJson(g,
                new TypeToken<List<String>>() {
                }.getType());
    }

    private synchronized void removeFromList(String s) {
        List<String> g = toList(localData.getStringPreferenceValue(fileName));
        g.remove(s);
        localData.setStringPreferenceValue(fileName, gsonToString(g));
    }

}