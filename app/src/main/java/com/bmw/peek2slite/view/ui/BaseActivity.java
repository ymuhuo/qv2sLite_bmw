package com.bmw.peek2slite.view.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.bmw.peek2slite.BaseApplication;

public class BaseActivity extends AppCompatActivity {

    public static String TAG = "peek2s_debug";
    private BaseApplication application;
    private BaseActivity oContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (application == null) {
            // 得到Application对象
            application = (BaseApplication) getApplication();
        }

        oContext = this;// 把当前的上下文对象赋值给BaseActivity
        addActivity();// 调用添加方法
    }


    public Context context(){
        return oContext;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    Toast mToast;

    public void mToast(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (mToast == null) {
                mToast = Toast.makeText(getApplicationContext(), text,
                        Toast.LENGTH_SHORT);
            } else {
                mToast.setText(text);
            }
            mToast.show();
        }
    }

    public void mToast(int resId) {
        if (mToast == null) {
            mToast = Toast.makeText(getApplicationContext(), resId,
                    Toast.LENGTH_SHORT);
        } else {
            mToast.setText(resId);
        }
        mToast.show();
    }

    public static void log(String msg) {
        Log.i(TAG,"===============================================================================");
        Log.i(TAG, msg);
    }
    public static void error(String msg) {
        Log.e(TAG,"===============================================================================");
        Log.e(TAG, msg);
    }



    //添加Activity方法
    public void addActivity() {
        application.addActivity_(oContext);// 调用myApplication的添加Activity方法
    }

    //销毁当个Activity方法
    public void removeActivity() {
        application.removeActivity_(oContext);// 调用myApplication的销毁单个Activity方法
    }

    //销毁所有Activity方法
    public void removeALLActivity() {
//        SystemTransformJNAInstance.getInstance().SYSTRANS_Stop(SystemTransformByJNA.handle.getValue());
//        SystemTransformJNAInstance.getInstance().SYSTRANS_Release(SystemTransformByJNA.handle.getValue());
        application.removeALLActivity_();// 调用myApplication的销毁所有Activity方法
        System.exit(0);
    }

    public void removeOtherActivity(){
        application.removeOtherActivity(oContext);
    }


    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;

//        return super.getResources();
    }

}
