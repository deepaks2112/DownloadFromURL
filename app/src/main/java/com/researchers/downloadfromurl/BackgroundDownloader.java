package com.researchers.downloadfromurl;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.JobIntentService;
import android.support.v4.view.KeyEventDispatcher;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class BackgroundDownloader extends JobIntentService {

    public static class IntentReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("IntentReceiver received the intent");
            BackgroundDownloader.enqueueWork(context, intent);
        }
    }

    static final int JOB_ID = 1000;

    /*public static class IntentReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("IntentReceiver received the intent");
            BackgroundDownloader.enqueueWork(context, intent);
        }
    }*/
    public static final String ACTION = "com.researchers.action.START_BACKGROUND_DOWNLOADER";



    public static IntentReceiver receiver;

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("Method: onCreate of the intent service");
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION);
        this.registerReceiver(this.receiver, filter);
    }


    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, BackgroundDownloader.class, JOB_ID, work);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //this.unregisterReceiver(this.receiver);
    }

    /*public BackgroundDownloader() {
        super("BackgroundDownloader");
    }*/
    @Override
    protected void onHandleWork(Intent intent) {
        String strUrl = intent.getStringExtra("URL");
        System.out.println(strUrl + " is received");
        System.out.println("Received intent....");
        int count;

        try {
            URL url = new URL(strUrl);
            //String name = f_url[1];
            URLConnection conn = url.openConnection();
            conn.connect();
            System.out.println(strUrl + " connected successfully!");
            //Toast.makeText(getApplicationContext(),"Start downloading", Toast.LENGTH_SHORT);
            //Toast.makeText(MainActivity.this, strUrl, Toast.LENGTH_SHORT);
            //Toast.makeText(mContext, f_url[1], Toast.LENGTH_SHORT);

            long fileSize = (long)conn.getContentLengthLong();
            //System.out.println("File size is " + fileSize);
            String name = strUrl.substring(strUrl.lastIndexOf("/") + 1);// + strUrl.substring(strUrl.lastIndexOf("."));
            String path = Environment.getExternalStorageDirectory().toString() + "/Download/" + name;
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().toString());
            long bytesFree = (long)stat.getAvailableBlocksLong() * (long)stat.getBlockSizeLong();
            File file = new File(path);
            if (file.exists()) {
                return;
            } else if (bytesFree < fileSize) {
                return;
            } else {
                InputStream in = new BufferedInputStream(url.openStream(), 8192);
                //Toast.makeText(MainActivity.this, "File is stored as " + Environment.getExternalStorageState().toString() + "/Download/Kailash.jpg", Toast.LENGTH_SHORT).show();
                OutputStream out = new FileOutputStream(path);
                //System.out.println(Environment.getExternalStorageDirectory() + "/Download/Kailash.jpg");
                byte data[] = new byte[1024];

                long total = 0;


                while ((count = in.read(data)) != -1) {
                    //publishProgress("Downloading : " + total*100/fileSize + "% done.");
                    total += count;
                    out.write(data, 0, count);
                }

                out.flush();

                out.close();
                in.close();
            }
            //Toast.makeText(getApplicationContext(), "Finished downloading", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //error = true;
            Log.e("Error: ", e.getMessage());
        }
    }
}
