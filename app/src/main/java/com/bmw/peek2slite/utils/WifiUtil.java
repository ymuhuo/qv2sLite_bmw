package com.bmw.peek2slite.utils;


import static com.bmw.peek2slite.utils.InternetUtil.pingHost;

/**
 * Created by admin on 2017/6/30.
 */

public class WifiUtil {

    private static String TAG = "MyIntentService";

    public static boolean isWifiConnectPing(WifiAdmin wifiAdmin, String ssid, String testIp) {
        if (!wifiAdmin.getSSID().contains(ssid)) {
            return false;
        }
        if (wifiAdmin.getIPAddress() == 0) {
            return false;
        }

        if(testIp == null)
            return true;

        if (pingHost(testIp) == 0) {
            return true;
        }
        return false;
    }

    public static boolean isWifiConnect(WifiAdmin wifiAdmin, String ssid){
        if (!wifiAdmin.getSSID().contains(ssid))
            return false;
        if(wifiAdmin.getIPAddress()==0){
            return false;
        }
        if(wifiAdmin.getLinkSpeed() == -1)
            return false;
        if(wifiAdmin.getRssi() == -127)
            return false;
        return true;

    }


}
