package com.bmw.peek2slite;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bmw.peek2slite.utils.DbHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by yMuhuo on 2016/12/12.
 */
public class BaseApplication extends Application{

    static Context mContext;
    static Resources mResources;
    private static final String PREF_M1S1 = "M1S1_PREF";
    private static long last_duration_time;
    private static String last_toast_msg;
    private List<Activity> oList;
    /**
     * 主要的线程池
     */
    public static Executor MAIN_EXECUTOR = Executors.newFixedThreadPool(5);

    /**
     * 文件发送单线程
     */
    public static Executor FILE_SENDER_EXECUTOR = Executors.newSingleThreadExecutor();


    @Override
    public void onCreate() {
        super.onCreate();
        DbHelper.init(getApplicationContext());
        mContext = getApplicationContext();
        mResources = mContext.getResources();
        oList = new ArrayList<Activity>();
    }

    public synchronized static BaseApplication context(){
        return (BaseApplication) mContext;
    }

    public static Resources resources(){
        return mResources;
    }

    public static SharedPreferences getSharedPreferences(){
        return context().getSharedPreferences(PREF_M1S1,Context.MODE_PRIVATE);
    }

    public static void toast(String msg){
        toast(msg, Toast.LENGTH_SHORT);
    }

    public static void toast(String msg,int duration){
        if(msg != null && !msg.equalsIgnoreCase("")){
            long current_time = System.currentTimeMillis();
            if( !msg.equalsIgnoreCase(last_toast_msg) || current_time - last_duration_time>2000){
                View view = LayoutInflater.from(context()).inflate(R.layout.toast_view,null);
                TextView textView = (TextView) view.findViewById(R.id.toast_tv);
                textView.setText(msg);
                Toast toast = new Toast(context());
                toast.setView(view);
                toast.setDuration(duration);
                toast.show();

                last_duration_time = System.currentTimeMillis();
                last_toast_msg = msg;
            }
        }
    }


    /**
     * 添加Activity
     */
    public void addActivity_(Activity activity) {
// 判断当前集合中不存在该Activity
        if (!oList.contains(activity)) {
            oList.add(activity);//把当前Activity添加到集合中
        }
    }

    /**
     * 销毁单个Activity
     */
    public void removeActivity_(Activity activity) {
//判断当前集合中存在该Activity
        if (oList.contains(activity)) {
            oList.remove(activity);//从集合中移除
            activity.finish();//销毁当前Activity
        }
    }

    /**
     * 销毁所有的Activity
     */
    public void removeALLActivity_() {
        //通过循环，把集合中的所有Activity销毁
        for (Activity activity : oList) {
            activity.finish();
        }


    }

    public void removeOtherActivity(Activity keepActivity){
        for(Activity activity : oList){
            if(activity != keepActivity)
                activity.finish();
        }
    }


}
