package com.bmw.peek2slite.presenter.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bmw.peek2slite.R;
import com.bmw.peek2slite.model.AdvanceSetInfo;
import com.bmw.peek2slite.presenter.ControlPresenter;
import com.bmw.peek2slite.utils.LogUtil;
import com.bmw.peek2slite.utils.SocketUtilNew;
import com.bmw.peek2slite.view.viewImpl.PreviewImpl;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by admin on 2016/9/5.
 */
public class ControlPresentImpl2 implements ControlPresenter {


    private static final String TAG = "main/PreviewActivity/Cp";
    private PreviewImpl cView;
    private SocketUtilNew socketUtil;
    private String actionName;
    private SharedPreferences sharedPreferences;
    private boolean isNotFinish;
    private ScheduledExecutorService scheduledExecutorService;
    private Executor cacheThreadPool;

    public ControlPresentImpl2(PreviewImpl cView, Context context) {
        this.cView = cView;
        scheduledExecutorService = Executors.newScheduledThreadPool(3);
        cacheThreadPool = Executors.newCachedThreadPool();
        socketUtil = SocketUtilNew.getInstance();
        sharedPreferences = context.getSharedPreferences(AdvanceSetInfo.ANVANCE_SHARE, Context.MODE_PRIVATE);

        socketUtil.setOnDataReaderListener(new SocketUtilNew.OnDataReaderListener() {
            @Override
            public void result(byte[] bytes) {
                Message msg = new Message();
                msg.obj = bytes;
                handler.sendMessage(msg);
            }
        });

        isNotFinish = true;

        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                getBattery();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }


    @Override
    public void startThread() {
        initThread();
    }


    public void initThread() {
        cacheThreadPool.execute(new Runnable() {
            @Override
            public void run() {

                final boolean isFangdou = sharedPreferences.getBoolean(AdvanceSetInfo.PIC_FANGDOU, false);
                final boolean isKuanDongTai = sharedPreferences.getBoolean(AdvanceSetInfo.KUANDONGTAI, false);
                final boolean isQiangLight = sharedPreferences.getBoolean(AdvanceSetInfo.LIGHT_YIZHI, false);
                final boolean isTouWu = sharedPreferences.getBoolean(AdvanceSetInfo.TOUWU, false);
                final boolean isGanGanLight = sharedPreferences.getBoolean(AdvanceSetInfo.GAOGANLIGHT, false);

                while (isSocketNull()) {
                    sleep(50);
                }
                if (isFangdou) {
                    open_picfangdou();
                } else {
                    close_picfangdou();
                }
                sleep(3000);
                if (isKuanDongTai) {
                    open_kuandongtai();
                } else {
                    close_kuandongtai();
                }
                sleep(3500);
                if (isQiangLight) {
                    open_lightyizhi();
                } else {
                    close_lightyizhi();
                }
                sleep(5100);
                if (isTouWu) {
                    open_touwu();
                } else {
                    close_touwu();
                }
                sleep(6600);
                if (isGanGanLight) {
                    open_gaoganguang();
                } else {
                    close_gaoganguang();
                }
            }
        });
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void loginoutSocket() {
        socketUtil.release();

    }

    @Override
    public void
    resetSocket() {
        socketUtil.resetSocket();
    }


    private void set_commands(String name, int command, int arg_count, int[] args, boolean isSend) {
        actionName = name;

        int sum_arg = 0;
        for (int i = 0; i < arg_count - 1; i++) {
            sum_arg += args[i];
        }
        int sum = 0;
        sum = (0x01 + command + arg_count + sum_arg) % 0x100;

        byte[] commands = new byte[arg_count + 3];

        commands[0] = (byte) 0x01;
        commands[1] = (byte) command;
        commands[2] = (byte) arg_count;
        commands[arg_count + 2] = (byte) sum;

        for (int i = 3; i < arg_count + 2; i++) {
            commands[i] = (byte) args[i - 3];
        }

        if (isSend)
            sendCommands(commands);
        else
            getReader(commands);


    }

    @Override
    public void up() {//上命令
        set_commands("上命令", 0x10, 0x02, new int[]{0x00, 0x13}, true);
    }

    @Override
    public void down() { //下命令
        set_commands("下命令", 0x11, 0x02, new int[]{0x00, 0x14}, true);
    }

    @Override
    public void size_add() {//变倍变长
        set_commands("变倍变长", 0x14, 0x02, new int[]{0x00, 0x17}, true);
    }

    @Override
    public void size_sub() {//变倍变短
        set_commands("变倍变短", 0x15, 0x02, new int[]{0x00, 0x18}, true);
    }

    @Override
    public void zoom_add() {//聚焦近
        set_commands("聚焦近", 0x17, 0x02, new int[]{0x00, 0x1a}, true);
    }

    @Override
    public void zoom_sub() {//聚焦远
        set_commands("聚焦远", 0x16, 0x02, new int[]{0x00, 0x19}, true);
    }

    @Override
    public void stop() {//停止命令
        set_commands("停止命令", 0x1c, 0x02, new int[]{0x00, 0x1f}, true);
    }

    @Override
    public void low_beam_open(int strength) {//近光灯开
        set_commands("近光灯开", 0x19, 0x02, new int[]{strength}, true);
    }

    @Override
    public void low_beam_close() {//近光灯关
        set_commands("近光灯关", 0x18, 0x02, new int[]{0x00}, true);
    }

    @Override
    public void high_beam_open(int strength) {//远光灯开
        set_commands("远光灯开", 0x1B, 0x02, new int[]{strength}, true);
    }

    @Override
    public void high_beam_close() {//远光灯关
        set_commands("远光灯关", 0x1a, 0x02, new int[]{0x00}, true);
    }

    @Override
    public void autoHorizontal() {//自动水平
        set_commands("自动水平", 0x21, 0x02, new int[]{0x00}, true);
    }

    @Override
    public boolean isSocketNull() {
        if (socketUtil != null)
            return socketUtil.isSocketNull();
        else
            return true;
    }

    private void sendCommands(byte[] commands) {
        socketUtil.getReader(commands, 1, actionName, 0);
    }


    @Override
    public void getBattery() {//获取电量；
        set_commands("获取电量", 0x23, 0x02, new int[]{0x00}, false);

    }

    @Override
    public void getLaserRanging() {
        set_commands("激光测距", 0x20, 0x02, new int[]{0x00}, false);
    }

    @Override
    public void open_picfangdou() {
        set_commands("open_picfangdou", 0x26, 0x02, new int[]{0x01}, false);
    }

    @Override
    public void close_picfangdou() {
        set_commands("close_picfangdou", 0x26, 0x02, new int[]{0x00}, false);

    }

    @Override
    public void open_kuandongtai() {
        set_commands("open_kuandongtai", 0x27, 0x02, new int[]{0x01}, false);

    }

    @Override
    public void close_kuandongtai() {
        set_commands("close_kuandongtai", 0x27, 0x02, new int[]{0x00}, false);

    }

    @Override
    public void open_lightyizhi() {
        set_commands("open_lightyizhi", 0x28, 0x02, new int[]{0x03}, false);

    }

    @Override
    public void close_lightyizhi() {
        set_commands("close_lightyizhi", 0x28, 0x02, new int[]{0x00}, false);
    }

    @Override
    public void open_touwu() {
        set_commands("open_touwu", 0x29, 0x02, new int[]{0x03}, false);
    }

    @Override
    public void close_touwu() {
        set_commands("close_touwu", 0x29, 0x02, new int[]{0x00}, false);
    }

    @Override
    public void open_gaoganguang() {
        set_commands("open_gaoganguang", 0x2A, 0x02, new int[]{0x01}, false);

    }

    @Override
    public void close_gaoganguang() {
        set_commands("close_gaoganguang", 0x2A, 0x02, new int[]{0x00}, false);
    }


    private void getReader(byte[] commands) {
        socketUtil.getReader(commands, 5, "接收" + actionName, 1);
    }

    private int boomSize;
    private String rangingSize;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            byte[] bytes = (byte[]) msg.obj;
            if (bytes != null)
                switch (bytes[1]) {
                    case 0x23:  //获取电量
                        if (bytes.length == 5 && bytes[1] == 0x23 && bytes[4] != 0) {
                            int batteryNum = Integer.valueOf(bytes[3]);
                            if (cView != null) {
                                cView.ilog("成功获取电量：" + batteryNum);
                                cView.setBattery(chooseBatteryIcon(batteryNum));
                            }
                        }
                        break;
                    case 0x22:  //获取机芯倍数
                        LogUtil.log("机芯获取成功：", (bytes[3] & 0xff));

                        boomSize = (bytes[3] & 0xff);

                        break;
                    case 0x20:  //激光测距

                        byte[] bytes1 = new byte[]{(byte) (bytes[3] & 0xff), (byte) (bytes[4] & 0xff), (byte) (bytes[5] & 0xff), (byte) (bytes[6] & 0xff), (byte) (bytes[7] & 0xff)};

                        try {
                            String str = new String(bytes1, "ascii");
                            LogUtil.log("激光测距获取成功：" + str);
                            rangingSize = str;
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }


                        break;

                }


        }
    };

    private int chooseBatteryIcon(int batteryNum) {
        if (batteryNum >= 86) {
            return R.mipmap.battery6;
        } else if (batteryNum >= 72) {
            return R.mipmap.battery5;
        } else if (batteryNum >= 58) {
            return R.mipmap.battery4;
        } else if (batteryNum >= 44) {
            return R.mipmap.battery3;
        } else if (batteryNum >= 30) {
            return R.mipmap.battery2;
        } else if (batteryNum > 16) {
            return R.mipmap.battery1;
        } else {
            return R.mipmap.battery0;
        }
    }

    @Override
    public void release() {//释放资源
        scheduledExecutorService.shutdownNow();
        Log.d(TAG, "release: ControlPresentImpl");
    }

}
