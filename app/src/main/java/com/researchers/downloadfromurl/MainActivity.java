package com.researchers.downloadfromurl;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StatFs;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.WriteAbortedException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.stream.Stream;


public class MainActivity extends AppCompatActivity {
    //ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.display);
    TextInputEditText edit;
    Button btn;//, btn2;
    String strUrl;
    boolean error = false;
    SharedPreferences pref;// = this.getSharedPreferences("com.researchers.downloadfromurl", MODE_PRIVATE);
    boolean rwPerm;// = pref.getBoolean("rwPerm", false);
    int noDwnlds;
    String[] perm = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    final private int permReqCode = 1;
    private ProgressBar pDialog;
    private long total, fileSize;
    boolean sizeError = false, existsError = false;
    //private BroadcastReceiver receiver;
    private String TAG = "DownloadFromURL";
    //private Button mButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*IntentFilter filter = new IntentFilter();
        filter.addAction(BackgroundDownloader.ACTION);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive");
                System.out.println("IntentReceiver received the intent");
                BackgroundDownloader.enqueueWork(context, intent);
            }
        };
        registerReceiver(receiver, filter);*/

        System.out.println("Method: onCreate of MainActivity.java");
        pref = this.getSharedPreferences("com.researchers.downloadfromurl", MODE_PRIVATE);
        rwPerm = pref.getBoolean("rwPerm", false);
        noDwnlds = pref.getInt("noDwnlds", 0);
        pDialog = (ProgressBar) findViewById(R.id.pBar);
        pDialog.setVisibility(View.INVISIBLE);
        if(!rwPerm) {
            //int i = 0;
            requestPermissions(perm, permReqCode);

        }
        edit = (TextInputEditText) findViewById(R.id.field);

        btn = (Button) findViewById(R.id.button);

        /*Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {

            }
        }*/
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strUrl = retString(edit.getText().toString());
                new DownloadFromURL().execute();
            }
        });
        /*btn2 = (Button) findViewById(R.id.button2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, " " + Environment.getExternalStorageDirectory().toString(), Toast.LENGTH_SHORT).show();
                Intent in = new Intent(BackgroundDownloader.ACTION);
                in.putExtra("URL", "https://www.alpenwild.com/Switzerlandtravel/wp-content/uploads/2018/02/switzerland-berghotel-weisshorn.jpg");
                BackgroundDownloader.enqueueWork(getApplicationContext(), in);
            }
        });
        //myMethod();
        /*if(existsError) {
            new AlertDialog.Builder(this)
                    .setTitle("ERROR")
                    .setMessage("File with same name already exists in Download")
                    .show();
        } else if(sizeError) {
            new AlertDialog.Builder(this)
                    .setTitle("ERROR")
                    .setMessage("Storage full")
                    .show();

        }*/
    }

    /*private void performDownload() {
        Intent intent = getIntent();
        String address = intent.getStringExtra("url");
        String file = intent.getStringExtra("file");
        URL url = new URL(address);
        url.S(url.openStream(), file);
    }*/
    /*public void myMethod(String URL) {
        //Intent intent = getIntent();
        //String address = intent.getStringExtra("url");
        Toast.makeText(getApplicationContext(), "Download started", Toast.LENGTH_LONG).show();
        new DownloadFromURL().execute(URL);
    }*/


    /*public static class IntentReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("IntentReceiver received the intent");
            BackgroundDownloader.enqueueWork(context, intent);
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case permReqCode:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    pref.edit().putBoolean("rwPerm", true).apply();
                }
                break;
        }
    }

    public String retString(String str) {
        return str;
    }

    public class DownloadFromURL extends AsyncTask<String, String, String> {

        //private Context mContext;

        /*public void DownloadFromURL(Context context) {
            mContext = context;
        }*/

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /*pDialog.setMessage("Loading...Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();*/
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.setVisibility(View.INVISIBLE);
            if (existsError) {
                Toast.makeText(MainActivity.this, "File with same name exists", Toast.LENGTH_SHORT).show();
                existsError = false;
            } else if (sizeError) {
                Toast.makeText(MainActivity.this, "Storage full", Toast.LENGTH_SHORT).show();
                sizeError = false;
            }
            if (!error) {
                noDwnlds += 1;
                Toast.makeText(MainActivity.this, "Downloading from " + strUrl + " finished", Toast.LENGTH_SHORT).show();
                pref.edit().putInt("noDwnlds",noDwnlds).apply();
            }
            else
                Toast.makeText(MainActivity.this, "Downloading from " + strUrl + " unsuccessful", Toast.LENGTH_SHORT).show();
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(String... s) {
            pDialog.setProgress((int)(total*100/fileSize));
            //pDialog.setIndeterminate(false);
            pDialog.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(String... f_url) {
            int count;

            try {
                URL url = new URL(strUrl);
                //String name = f_url[1];
                URLConnection conn = url.openConnection();
                conn.connect();
                System.out.println("Connection done!");
                //Toast.makeText(MainActivity.this, strUrl, Toast.LENGTH_SHORT);
                //Toast.makeText(mContext, f_url[1], Toast.LENGTH_SHORT);

                fileSize = (long)conn.getContentLengthLong();
                StatFs stat = new StatFs(Environment.getExternalStorageDirectory().toString());
                long bytesFree = (long)stat.getAvailableBlocksLong() * (long)stat.getBlockSizeLong();

                System.out.println("File size is " + fileSize);
                String name = strUrl.substring(strUrl.lastIndexOf("/") + 1);// + strUrl.substring(strUrl.lastIndexOf("."));
                String path = Environment.getExternalStorageDirectory().toString() + "/Download/" + name;
                File file = new File(path);
                if(file.exists()) {
                    publishProgress("File already exists...Aborting!");
                    existsError = true;
                    error = true;
                    return null;
                } else if (fileSize > bytesFree) {
                    publishProgress("Space not available...Aborting!");
                    error = true;
                    sizeError = true;
                    return null;
                } else {
                    InputStream in = new BufferedInputStream(url.openStream(), 8192);
                    //Toast.makeText(MainActivity.this, "File is stored as " + Environment.getExternalStorageState().toString() + "/Download/Kailash.jpg", Toast.LENGTH_SHORT).show();
                    System.out.println(path);
                    OutputStream out = new FileOutputStream(path);
                    //System.out.println(Environment.getExternalStorageDirectory() + "/Download/Kailash.jpg");
                    byte data[] = new byte[1024];

                    total = 0;


                    while ((count = in.read(data)) != -1) {
                        publishProgress("Downloading : " + total * 100 / fileSize + "% done.");
                        total += count;
                        out.write(data, 0, count);
                    }

                    out.flush();

                    out.close();
                    in.close();
                }
                //Toast.makeText(MainActivity.this, "Finished downloading", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                error = true;
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        System.out.println("Exited activity!");
        //unregisterReceiver(receiver);
    }
}
