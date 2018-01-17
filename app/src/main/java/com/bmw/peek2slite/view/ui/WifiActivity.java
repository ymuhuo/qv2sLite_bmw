package com.bmw.peek2slite.view.ui;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.bmw.peek2slite.Constant;
import com.bmw.peek2slite.R;
import com.bmw.peek2slite.model.Login_info;
import com.bmw.peek2slite.service.MyIntentService;
import com.bmw.peek2slite.utils.LogUtil;
import com.bmw.peek2slite.utils.WifiAdmin;
import com.bmw.peek2slite.utils.WifiUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WifiActivity extends AppCompatActivity {


    @Bind(R.id.mainFrameState)
    TextView mainFrameState_tv;
    @Bind(R.id.repeaterState)
    TextView repeaterState_tv;
    @Bind(R.id.wifiConnectGroupChoose)
    RadioGroup wifiConnectRg;
    @Bind(R.id.switch_dialog_wifiLinkAuto)
    Switch autoWifi;
    @Bind(R.id.tv_autoWifiTitle)
    TextView autoTitle;


    private WifiAdmin wifiAdmin;
    private boolean isFinish;
    private String connectingSsid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        ButterKnife.bind(this);


        initBroadcastReceiver();
        wifiAdmin = new WifiAdmin(this);

        if (Login_info.getInstance().isWifiIsRepeater()) {
            wifiConnectRg.check(R.id.repeater);
        } else {
            wifiConnectRg.check(R.id.mainFrame);
        }

        initAutoConnect();

        wifiConnectRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.mainFrame:
                        Login_info.getInstance().setWifiIsRepeater(false);
                        Login_info.getInstance().setWifiIsRepeater(false);
                        connectingSsid = null;
                        initConect();
                        break;
                    case R.id.repeater:
                        Login_info.getInstance().setWifiIsRepeater(true);
                        Login_info.getInstance().setWifiIsRepeater(true);
                        connectingSsid = null;
                        initConect();
                        break;
                }
            }
        });


        initWifiStateText(Login_info.baseMainFrameWifiSSID, mainFrameState_tv);
        initWifiStateText(Login_info.baseRepeaterWifiSSID, repeaterState_tv);


        initConect();

        startScanThread();

    }

    private void startScanThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isFinish) {
                    wifiAdmin.startScan();
                    List<ScanResult> list = wifiAdmin.getWifiList();
                    if (list != null) {
                        final int mainFrameState = getWifiState(Login_info.baseMainFrameWifiSSID, list);
                        final int repeaterState = getWifiState(Login_info.baseRepeaterWifiSSID, list);
                        if(mainFrameState == 0 && !Login_info.getInstance().isWifiIsRepeater()){
                            connectingSsid = null;
                        }
                        if(repeaterState == 0 && Login_info.getInstance().isWifiIsRepeater()){
                            connectingSsid = null;
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mainFrameState_tv != null) {
                                    updateTv(mainFrameState, mainFrameState_tv);
                                    updateTv(repeaterState, repeaterState_tv);
                                }
                            }
                        });
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    protected void onResume() {
        super.onResume();
//        wifiAdmin.openWifi();
//        initWifiState();
    }


    private void initWifiStateText(String ssid, TextView textView) {
        if (WifiUtil.isWifiConnect(wifiAdmin,ssid)) {
            updateTv(2, textView);
        } else {
            updateTv(0, textView);
        }
    }


    private int getWifiState(String ssid, List<ScanResult> list) {
        if (WifiUtil.isWifiConnect(wifiAdmin,ssid))
            return 2;
        if (list != null && list.size() > 0)
            for (ScanResult scanResult : list) {
                if (scanResult.SSID.contains(ssid)) {
                    if (!TextUtils.isEmpty(connectingSsid) && connectingSsid.contains(ssid)) {
                        return 3;
                    } else {
//                        if (!wifiAdmin.getSSID().contains(ssid))
                            return 1;
                    }
                }
            }
        return 0;
    }


    private void updateTv(int state, TextView textView) {
        if (state == 0) {
            textView.setText(R.string.noSignal);
            textView.setTextColor(Color.RED);
        } else if (state == 1) {
            textView.setText(R.string.hasSignal);
            textView.setTextColor(getResources().getColor(R.color.colorText));
        } else if (state == 2) {
            textView.setText(R.string.alreadyConnect);
            textView.setTextColor(Color.GREEN);
        } else {
            textView.setText(R.string.isConnecting);
            textView.setTextColor(getResources().getColor(R.color.colorText));
        }
    }

    private void initConect() {
            /*initWifiImage();
            WifiConnectNew.getInstance().starConnect();*/
        if (Login_info.getInstance().isWifi_auto()) {
            MyIntentService.stopIntentService(this);
            String ssid = Login_info.getInstance().isWifiIsRepeater() ? Login_info.baseRepeaterWifiSSID : Login_info.baseMainFrameWifiSSID;
            MyIntentService.startActionWifiConnect(this, ssid, Login_info.baseRepeaterWifiPassword, Login_info.getInstance().getSocket_ip());
        }
    }

    private void initAutoConnect() {
        autoWifi.setChecked(Login_info.getInstance().isWifi_auto());
        isAutoVisible(Login_info.getInstance().isWifi_auto());

        autoWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
                Login_info.getInstance().setWifi_auto(b, true);
                if (autoWifi != null) {
                    autoWifi.post(new Runnable() {
                        @Override
                        public void run() {
                            isAutoVisible(b);
                        }
                    });
                }
            }
        });
    }

    private void isAutoVisible(boolean wifi_auto) {
        if (wifi_auto) {
            wifiConnectRg.setVisibility(View.VISIBLE);
            autoTitle.setVisibility(View.VISIBLE);
            initConect();
        } else {
            wifiConnectRg.setVisibility(View.GONE);
            autoTitle.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        isFinish = true;
        ButterKnife.unbind(this);
        unregisterReceiver(wifConnectBReceiver);
    }


    //设置返回控制
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            this.finish();
            return true;
        }
        if (keyCode == event.KEYCODE_HOME) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    private void initBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter(Constant.BROADCAST_JUST_IS_CONNECTING);
        registerReceiver(wifConnectBReceiver, intentFilter);
    }

    private BroadcastReceiver wifConnectBReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String connectWifiSsid = intent.getStringExtra(Constant.KEY_WIFI_CONNECT_DOING);
            if (!TextUtils.isEmpty(connectWifiSsid)) {
                connectingSsid = connectWifiSsid;
            }


            boolean isWifiConnectFinish = intent.getBooleanExtra(Constant.KEY_WIFI_CONNECT_FINISH, false);
            if (isWifiConnectFinish) {
                if (WifiUtil.isWifiConnect(wifiAdmin,Login_info.baseMainFrameWifiSSID)&& mainFrameState_tv != null) {
                    updateTv(2, mainFrameState_tv);
                }
                if (WifiUtil.isWifiConnect(wifiAdmin,Login_info.baseRepeaterWifiSSID)&& repeaterState_tv != null) {
                    updateTv(2, repeaterState_tv);
                }
            }
        }
    };
}
