package com.bmw.peek2slite.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.io.IOException;

/**
 * Created by admin on 2017/7/6.
 */

public class InternetUtil {


    public static String getWifiIp(Context context) {
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
//            wifiManager.setWifiEnabled(true);
            return null;
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);
        return ip;
    }


    private static String intToIp(int i) {

        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }

    public static int pingHost(String str) {
        int resault = -1;
        try {
            // TODO: Hardcoded for now, make it UI configurable
            Process p = Runtime.getRuntime().exec("ping -c 1 -w 3 " + str);
            int status = p.waitFor();
            if (status == 0) {
                //  mTextView.setText("success") ;
                resault = 0;
            } else {
                resault = 1;
                //  mTextView.setText("fail");
            }
        } catch (IOException e) {
            //  mTextView.setText("Fail: IOException"+"\n");
        } catch (InterruptedException e) {
            //  mTextView.setText("Fail: InterruptedException"+"\n");
        }

        return resault;
    }
}
