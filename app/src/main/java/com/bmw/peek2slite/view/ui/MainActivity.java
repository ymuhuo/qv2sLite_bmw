package com.bmw.peek2slite.view.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bmw.peek2slite.Constant;
import com.bmw.peek2slite.R;
import com.bmw.peek2slite.model.AdvanceSetInfo;
import com.bmw.peek2slite.model.Login_info;
import com.bmw.peek2slite.service.MyIntentService;
import com.bmw.peek2slite.utils.DbHelper;
import com.bmw.peek2slite.utils.LogUtil;
import com.bmw.peek2slite.utils.SocketUtilNew;
import com.bmw.peek2slite.utils.WifiAdmin;
import com.bmw.peek2slite.utils.WifiUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hikvision.netsdk.HCNetSDK;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends BaseActivity {

    private final static String TAG = "mainActivity";
    @Bind(R.id.wifiLink)
    ImageButton wifiLink;
    @Bind(R.id.bominwell_logo)
    ImageView img_logo;
    private Login_info login_info;

    private int key_back;
    private long key_back_time;
    private Toast mToast;
    private WifiAdmin wifiAdmin;
    private boolean isFinish;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Log.d(TAG, "onCreate: mainActivity");


        initAdvanceSet(); //初始化高级设置
        login_info = Login_info.getInstance();
        login_info.initLoginInfo(this);
        SocketUtilNew.getInstance();
        mToast = Toast.makeText(this, "再按一次返回键退出客户端！", Toast.LENGTH_SHORT);
        initSDK();
        wifiAdmin = new WifiAdmin(this);
        initBroadcastReceiver();
        startWifiScanThread();

    }

    private void showLogoImage() {
        if (img_logo == null)
            return;
        File file = new File(Constant.LOGO_PATH);
        if (!file.exists())
            img_logo.setVisibility(View.GONE);
        else {
            Glide
                    .with(context())
                    .load(Constant.LOGO_PATH)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .centerCrop()
                    .crossFade()
                    .into(img_logo);
            img_logo.setVisibility(View.VISIBLE);
        }
    }

    private void startWifiScanThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isFinish) {
                    String ssid = Login_info.getInstance().isWifiIsRepeater() ? Login_info.baseRepeaterWifiSSID : Login_info.baseMainFrameWifiSSID;
                    if (!wifiAdmin.getSSID().contains(ssid)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (wifiLink != null)
                                    wifiLink.setImageResource(R.mipmap.wifi1);
                            }
                        });
                        startWifiService();

                    }
                    try {
                        Thread.sleep(1000 * 30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    private void initSDK() {
        // init net sdk
        if (!HCNetSDK.getInstance().NET_DVR_Init()) {
            LogUtil.error("HCNetSDK init is failed!", "");
//
//            viewImpl.stop();
        }
        HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, "/mnt/sdcard/sdklog/",
                true);
        LogUtil.log("HCNetSDK init is success!");
    }

    @Override
    protected void onResume() {
        log("onresume");
        initWifiImage();
        startWifiService();
        super.onResume();
        if (Constant.IS_NEUTRAL_VERSION) {
            showLogoImage();
        }
    }

    private void startWifiService() {
        if (login_info.isWifi_auto()) {
            MyIntentService.stopIntentService(this);
            String ssid = Login_info.getInstance().isWifiIsRepeater() ? Login_info.baseRepeaterWifiSSID : Login_info.baseMainFrameWifiSSID;
            MyIntentService.startActionWifiConnect(this, ssid, Login_info.baseRepeaterWifiPassword, Login_info.getInstance().getSocket_ip());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        log("onStop");
    }

    private void initWifiImage() {
//        log("change wifi vector color!!!");
        if (WifiUtil.isWifiConnect(wifiAdmin, Login_info.baseMainFrameWifiSSID) || WifiUtil.isWifiConnect(wifiAdmin, Login_info.baseRepeaterWifiSSID)) {
            wifiLink.setImageResource(R.mipmap.wifi3);
        } else {
            wifiLink.setImageResource(R.mipmap.wifi1);
        }
    }


    private void initAdvanceSet() {
        SharedPreferences sharedPreferences = getSharedPreferences(AdvanceSetInfo.ANVANCE_SHARE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(AdvanceSetInfo.GAOGANLIGHT, false);
        editor.putBoolean(AdvanceSetInfo.KUANDONGTAI, false);
        editor.putBoolean(AdvanceSetInfo.LIGHT_YIZHI, false);
        editor.putBoolean(AdvanceSetInfo.PIC_FANGDOU, false);
        editor.putBoolean(AdvanceSetInfo.TOUWU, false);
        editor.putBoolean(AdvanceSetInfo.ISDONE, true);
        editor.putInt(AdvanceSetInfo.LOW_LIGHT, 0);
        editor.putInt(AdvanceSetInfo.HIGH_LIGHT, 0);
        editor.commit();
        editor.clear();
    }

    @OnClick({R.id.pre_view, R.id.play_back, R.id.picture, R.id.environment_stat, R.id.setting, R.id.wifiLink})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.pre_view:
                intent = new Intent(this, PreviewActivity.class);
//                setBtnNoUse();
                break;
            case R.id.play_back:
                intent = new Intent(this, FileActivity.class);
                intent.putExtra("picture", false);
                break;
            case R.id.picture:
                intent = new Intent(this, FileActivity.class);
//                startActivity(intent);
                break;
            case R.id.environment_stat:
                intent = new Intent(this, EnvironmentActivity.class);
                break;
            case R.id.setting:
                intent = new Intent(this, SettingActivity.class);
//                startActivity(intent);
                break;

            case R.id.wifiLink:
                intent = new Intent(this, WifiActivity.class);

                break;
        }

        if (intent != null)
            startActivity(intent);


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        log("mainActivity destroy!");
        isFinish = true;
        unregisterReceiver(wifConnectBReceiver);
        SocketUtilNew.getInstance().release();
        DbHelper.getDbUtils().close();

        super.onDestroy();

        HCNetSDK.getInstance().NET_DVR_Cleanup();
        ButterKnife.unbind(this);
    }


    //设置返回控制
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            if ((System.currentTimeMillis() - key_back_time) >= 5000) {
                key_back = 0;
            }
            if (key_back != 1) {
//                toast("再按一次返回键退出客户端！");
                mToast.show();
                key_back_time = System.currentTimeMillis();
            }
            if (key_back == 1) {
                mToast.cancel();
                System.exit(0);
            }
            key_back++;

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
            boolean isWifiConnectFinish = intent.getBooleanExtra(Constant.KEY_WIFI_CONNECT_FINISH, false);
            if (isWifiConnectFinish) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initWifiImage();
                    }
                });
            }
        }
    };

}
