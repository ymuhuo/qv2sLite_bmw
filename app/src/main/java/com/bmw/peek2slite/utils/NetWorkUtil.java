package com.bmw.peek2slite.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;

/**
 * Created by admin on 2016/9/21.
 */
public class NetWorkUtil {


    public static int isNetworkAvailable(Context context)
    {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null)
        {
            return 1;
        }
        else
        {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0)
            {
                for (int i = 0; i < networkInfo.length; i++)
                {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        System.out.println(i + "网络===状态===" + networkInfo[i].getState());
                        System.out.println(i + "网络===类型===" + networkInfo[i].getTypeName());
                        if(networkInfo[i].getTypeName().equals("WIFI")){
                            OkHttpClient client = new OkHttpClient();
                            try {
                                URL url = new URL("https://www.baidu.com/");
                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                conn.setRequestMethod("GET");
                                if(conn.getResponseCode() == 200){

                                    System.out.println(i + "网络===wifi验证联网成功===");
                                    return 0;
                                }else{
                                    System.out.println(i + "网络===wifi验证联网失败===");
                                    return 2;
                                }
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                                System.out.println(i + "网络===wifi验证联网失败异常1==="+e);
                            } catch (IOException e) {
                                e.printStackTrace();
                                System.out.println(i + "网络===wifi验证联网失败异常2==="+e);
                            }
                            return 2;
                        }

                        System.out.println(i + "网络===验证联网成功===");
                        return 0;
                    }
                }
            }
        }

        System.out.println("===false===");
        return 1;
    }
}
