package com.bmw.peek2slite.view.dialog;

/**
 * Created by admin on 2016/9/19.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.bmw.peek2slite.R;
import com.bmw.peek2slite.model.Login_info;
import com.bmw.peek2slite.utils.LogUtil;
import com.bmw.peek2slite.utils.WifiAdmin;

import java.util.List;


public class WifiLinkDialog implements View.OnClickListener {

    private static final String TAG = "YMH";
    private final WifiAdmin wifiAdmin;
    private AlertDialog dialog;

    private TextView mainFrameStateTv, mainFrameConnectBtn, repeaterStateTv, repeaterConnectBtn;


    public WifiLinkDialog(Context context) {


        dialog = new AlertDialog.Builder(context).create();
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.dialog_anim);
//        dialog.setView(new EditText(context));//实现弹出虚拟键盘
        dialog.show();
        WindowManager manager = (WindowManager) context.
                getSystemService(Context.WINDOW_SERVICE);

        //为获取屏幕宽、高
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
        p.height = (int) (dm.heightPixels);   //高度设置为屏幕的0.3
        p.width = (int) (dm.widthPixels);    //宽度设置为全屏
        //设置生效
        window.setAttributes(p);

        window.setBackgroundDrawableResource(android.R.color.transparent);//加上这句实现满屏效果
        window.setGravity(Gravity.CENTER); // 非常重要：设置对话框弹出的位置
        window.setContentView(R.layout.wifi_link);
        mainFrameStateTv = (TextView) window.findViewById(R.id.mainFrameState);
        mainFrameConnectBtn = (TextView) window.findViewById(R.id.mainFrameConnect);
        repeaterStateTv = (TextView) window.findViewById(R.id.repeaterState);
        repeaterConnectBtn = (TextView) window.findViewById(R.id.repeaterConnect);

        mainFrameConnectBtn.setOnClickListener(this);
        repeaterConnectBtn.setOnClickListener(this);

        wifiAdmin = new WifiAdmin(context);
        wifiAdmin.openWifi();

        initWifiState();

    }

    private void initWifiState() {

        wifiAdmin.startScan();
        List<ScanResult> wifiList = wifiAdmin.getWifiList();

        for (int i = 0; i < wifiList.size(); i++) {

            if (wifiList.get(i).SSID.contains(Login_info.baseMainFrameWifiSSID)) {

                LogUtil.log("wifi:  connect  ",wifiAdmin.getSSID());
                if (wifiAdmin.getSSID().contains(Login_info.baseMainFrameWifiSSID)) {
                    mainFrameStateTv.setText("已连接");
                    mainFrameStateTv.setTextColor(Color.GREEN);
                    mainFrameConnectBtn.setVisibility(View.GONE);
                } else {
                    mainFrameStateTv.setText("未连接");
                    mainFrameConnectBtn.setVisibility(View.VISIBLE);
                }
            }

            if (wifiList.get(i).SSID.contains(Login_info.baseRepeaterWifiSSID)) {
                if (wifiAdmin.getSSID().contains(Login_info.baseRepeaterWifiSSID)) {
                    repeaterStateTv.setText("已连接");
                    repeaterStateTv.setTextColor(Color.GREEN);
                    repeaterConnectBtn.setVisibility(View.GONE);
                } else {
                    repeaterStateTv.setText("未连接");
                    repeaterConnectBtn.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    /**
     * 关闭对话框
     */
    public void dismiss() {
        dialog.dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mainFrameConnect:
                break;
            case R.id.repeaterConnect:
                wifiAdmin.CreateWifiInfo(Login_info.baseMainFrameWifiSSID,Login_info.baseMainFrameWifiPassword,3);
                repeaterStateTv.setText("正在连接...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(wifiAdmin.getSSID().contains(Login_info.baseMainFrameWifiSSID)){
                            repeaterStateTv.post(new Runnable() {
                                @Override
                                public void run() {
                                    repeaterStateTv.setText("已连接");
                                    repeaterStateTv.setTextColor(Color.GREEN);
                                    repeaterConnectBtn.setVisibility(View.GONE);
                                }
                            });
                        }else{
                            repeaterStateTv.post(new Runnable() {
                                @Override
                                public void run() {
                                    repeaterStateTv.setText("未连接");
                                }
                            });
                        }
                    }
                }).start();
                break;
        }

    }
}