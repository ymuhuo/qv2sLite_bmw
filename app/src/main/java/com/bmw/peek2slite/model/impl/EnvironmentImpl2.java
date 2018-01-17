package com.bmw.peek2slite.model.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bmw.peek2slite.BaseApplication;
import com.bmw.peek2slite.model.Environment;
import com.bmw.peek2slite.model.model.EnvironmentMode;
import com.bmw.peek2slite.presenter.EnvironmentListener;
import com.bmw.peek2slite.utils.SocketUtilNew;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/9/19.
 */
public class EnvironmentImpl2 implements EnvironmentMode {

    private static final String TAG = "EnvironmentImpl";

    public final static String SHAREPREFERENCES = "ENVIRONMENT";
    public final static String QIYA_MIN = "QIYAMIN";
    public final static String QIYA_MAX = "QIYAMAX";
    private SocketUtilNew socketUtil;
    private byte[] commands;
    private Context context;
    private Handler handler = new Handler();

    private Handler successHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    public EnvironmentImpl2(Context context) {
        this.context = context;
        socketUtil =  SocketUtilNew.getInstance();
//        socketUtil = SocketUtil.getIntance();
//        socketUtil.initSocket();
    }


    //获取数据
    @Override
    public void getDatas(final EnvironmentListener environmentListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (socketUtil.isSocketNull()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                getDatasDetail(environmentListener);
            }
        }).start();


    }

    private void getDatasDetail(final EnvironmentListener environmentListener) {
        final List<Environment> list = new ArrayList<>();
        final Environment[] environment = {null};

        commands = new byte[]{(byte) 0x01, (byte) 0x1E, (byte) 0x02,
                (byte) 0x00, (byte) 0x21};
        if (socketUtil != null) {
            socketUtil.getReader(commands, 5, "获取环境数据",1);

            socketUtil.setOnDataReaderListener(new SocketUtilNew.OnDataReaderListener() {
                @Override
                public void result(final byte[] bytes) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "run: 成功获取环境数据！" + bytes);
                            if (bytes != null) {
                                int order = Integer.valueOf(bytes[1]);
                                int gaoba = Integer.valueOf(bytes[3]);
                                int diba = Integer.valueOf(bytes[4]) & 0xff;
                                Log.i(TAG, "run: 命令：" + order);
                                if (order == 0x1e) {
                                    int qiya_num = ((gaoba << 8) | diba);
                                    environment[0] = getEnvironmentDate("气压", getQiya(qiya_num));
                                    Log.i(TAG, "run: 环境数据 ：" + environment[0].toString());
                                    if (list != null) {
                                        if (list.size() != 0)
                                            list.clear();
                                        list.add(environment[0]);
                                        Log.i(TAG, "run: 已经成功添加环境数据！ " + list.get(0).toString());
                                        environmentListener.success(list);
                                    }
                                }
                            } else {
                                Log.e(TAG, "sendResult: get null bytes result!");
                            }
                        }
                    });
                }
            });
        }
    }

    public void toast(String msg) {
        BaseApplication.toast(msg);
    }

    @Override
    public void realese() {
//        socketUtil.release();
//        socketUtil = null;
    }

    private Environment getEnvironmentDate(String name, float num) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHAREPREFERENCES, Context.MODE_PRIVATE);
        Environment environment = new Environment();
        environment.setCurrent_num(num);
        environment.setName(name);
        switch (name) {
            case "气压":
                environment.setMin_num(sharedPreferences.getFloat(QIYA_MIN, (float) -1.0));
                environment.setMax_num(sharedPreferences.getFloat(QIYA_MAX, (float) -1.0));
                break;
        }
        return environment;
    }

    private float getQiya(int qiya) {
//        if((qiya&0x8000)!=0){
//            qiya = 0-(qiya&0x7fff);
//        }else qiya = (qiya&0x7fff);
//        float aa = (float) (qiya * 10.0f-101325.0);

        if ((qiya & 0x8000) != 0) {
            qiya = 0 - (qiya & 0x7fff);
        } else
            qiya = (qiya & 0x7fff);

        float result = (float) ((qiya * 10.0f - 101325.0) * 0.1450377F / 1000.0f);
        result =  (float) (Math.round(result * 100)) / 100;
//        if(result<=0)
//            return 0;
        return result;


   /*     float result = (qiya*0.1450377f)/100.0f;
        return (float)(Math.round(result*100))/100;
*/

    }

}
