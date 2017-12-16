package com.example.aman.myapplication;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by aman on 16/12/17.
 */

public class WifiService extends Service {

    private static final String TAG = "com.example.WifiService";
    private static final Integer PERIOD = 5000;

    WifiManager wifi;
    WifiScanReceiver wifiReceiver;
    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");

        wifi=(WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiScanReceiver();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");

        registerReceiver(
                wifiReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        );

        callAsynchronousTask();
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Service onDestroy");
        unregisterReceiver(wifiReceiver);
    }
    private class WifiScanReceiver extends BroadcastReceiver {
        JSONArray jsonArray = new JSONArray();
        public void onReceive(Context c, Intent intent) {
            Log.v(TAG,"called");
            List<ScanResult> wifiScanList = wifi.getScanResults();
            for(int i = 0; i < wifiScanList.size(); i++){
                ScanResult scanResult = wifiScanList.get(i);
                if (scanResult.SSID.equals("practoM") || scanResult.SSID.equals("practo") || scanResult.SSID.equals("PractoSandbox")) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("mac_id",scanResult.BSSID);
                        jsonObject.put("strength", scanResult.level);
                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.v(TAG, jsonArray.toString());
        }
    }
    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            PerformBackgroundTask performBackgroundTask = new PerformBackgroundTask();
                            performBackgroundTask.execute();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, PERIOD); //execute in every 50000 ms
    }

    private class PerformBackgroundTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            wifi.startScan();
            return null;
        }
    }
}


