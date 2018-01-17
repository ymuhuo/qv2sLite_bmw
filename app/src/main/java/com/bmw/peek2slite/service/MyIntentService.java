package com.bmw.peek2slite.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.util.Log;


import com.bmw.peek2slite.Constant;
import com.bmw.peek2slite.utils.LogUtil;
import com.bmw.peek2slite.utils.WifiAdmin;
import com.bmw.peek2slite.utils.WifiUtil;

import java.util.List;
import java.util.Random;


public class MyIntentService extends IntentService {
//    private static String TAG = MyIntentService.class.getSimpleName();

    private static final String ACTION_WIFI = "example.bmw.wifiservice.MyIntentService.wifiConnect";
    private static final String PARAM_WIFI_SSID = "example.bmw.wifiservice.MyIntentService.wifiConnect_params_ssid";
    private static final String PARAM_WIFI_PADDWORD = "example.bmw.wifiservice.MyIntentService.wifiConnect_params_password";
    private static final String PARAM_WIFI_TESTIP = "example.bmw.wifiservice.MyIntentService.wifiConnect_params_testIp";

    private int test;
    private boolean isFinish;
    private Intent intentBR;


    public MyIntentService() {
        super("MyIntentService");
    }

    public static void startActionWifiConnect(Context context, String ssid, String password, String ip) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_WIFI);
        intent.putExtra(PARAM_WIFI_SSID, ssid);
        intent.putExtra(PARAM_WIFI_PADDWORD, password);
        intent.putExtra(PARAM_WIFI_TESTIP, ip);
        context.startService(intent);

    }


    private void initBrocastIntent() {
        intentBR = new Intent(Constant.BROADCAST_JUST_IS_CONNECTING);
    }

    public static void stopIntentService(Context context) {
        Intent intent = new Intent(context, MyIntentService.class);
        context.stopService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (action.equals(ACTION_WIFI)) {
                String ssid = intent.getStringExtra(PARAM_WIFI_SSID);
                String password = intent.getStringExtra(PARAM_WIFI_PADDWORD);
                String ip = intent.getStringExtra(PARAM_WIFI_TESTIP);
                WifiAdmin wifiAdmin = new WifiAdmin(this);
                if (!isWifiAlreadyConnect(wifiAdmin, ssid, ip)) {
                    connectWifi(wifiAdmin, ssid, password, ip);
                }
            }
        }
    }

    private void connectWifi(WifiAdmin wifiAdmin, String ssid, String password, String ip) {
        wifiAdmin.openWifi();
        List<ScanResult> list = null;
        int connectNum = 0;
//        Log.d(TAG, "connectWifi:ssid = " + ssid + " password = " + password);
        while (!WifiUtil.isWifiConnectPing(wifiAdmin, ssid, ip) && !isFinish) {
//            Log.d(TAG, "connectWifi: wifi is Scanning!!!  " + ssid);

            wifiAdmin.openWifi();
            if (!wifiAdmin.getSSID().contains(ssid)) {
                LogUtil.log("Disconnect wifi ssid = "+wifiAdmin.getSSID()+" thread:"+test);
                wifiAdmin.disconnectWifi(wifiAdmin.getNetworkId());

            }
            wifiAdmin.startScan();
            list = wifiAdmin.getWifiList();
//            Log.d(TAG, "connectWifi: just scanREsult!" + (list == null ? list : list.size()));

            if (list != null && list.size() > 0) {
                for (ScanResult scanResult : list) {

                    if (scanResult.SSID.equals(ssid)) {
                        LogUtil.log("connectWifi: connect ssid :" + ssid);
                        sendBrocastWifiConnecting(ssid);

                        if(connectNum>=3){
                           LogUtil.log("connectWifi: remove Config: "+ssid);
                            wifiAdmin.removeConfig(ssid);
                            connectNum = 0;
                        }

                        wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(ssid, password, 3));
                        sleep(6000);
                        connectNum++;
//                        Log.d(TAG, "connectWifi: wifi is connected!!!  " + ssid);
                        break;
                    }

                }
            } else {
                LogUtil.error(" scan wifi list is null!");
                wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(ssid, password, 3));
                sleep(6000);
            }
            sleep(3000);
        }
        if (isFinish) {
            LogUtil.log("connectWifi: wifiThread is finish!!!"+test);
        } else
            LogUtil.log("connectWifi: wifi already connected!!!"+test);

        intentBR.putExtra(Constant.KEY_WIFI_CONNECT_DOING, "");
        intentBR.putExtra(Constant.KEY_WIFI_CONNECT_FINISH, true);

        sendBroadcast(intentBR);

    }


    private void sendBrocastWifiConnecting(String ssid) {
        intentBR.putExtra(Constant.KEY_WIFI_CONNECT_FINISH, false);
        intentBR.putExtra(Constant.KEY_WIFI_CONNECT_DOING, ssid);

        sendBroadcast(intentBR);
    }


    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean isWifiAlreadyConnect(WifiAdmin wifiAdmin, String ssid, String ip) {
        return WifiUtil.isWifiConnectPing(wifiAdmin, ssid, ip);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        test = new Random().nextInt();
        LogUtil.log( "onCreate: " + test);
        initBrocastIntent();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        LogUtil.log("onStart: " + test);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isFinish = true;
        LogUtil.log( "onDestroy: " + test);
    }
}
