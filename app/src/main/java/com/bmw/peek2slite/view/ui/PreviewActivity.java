package com.bmw.peek2slite.view.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bmw.peek2slite.R;
import com.bmw.peek2slite.model.AdvanceSetInfo;
import com.bmw.peek2slite.model.All_id_Info;
import com.bmw.peek2slite.model.Login_info;
import com.bmw.peek2slite.presenter.ControlPresenter;
import com.bmw.peek2slite.presenter.HCNetSdkLogin;
import com.bmw.peek2slite.presenter.VideoCutPresenter;
import com.bmw.peek2slite.presenter.impl.ControlPresentImpl2;
import com.bmw.peek2slite.presenter.impl.HCNetSdkLoginImpl;
import com.bmw.peek2slite.presenter.impl.VideoCutPresentImpl;
import com.bmw.peek2slite.service.MyIntentService;
import com.bmw.peek2slite.view.dialog.Capture_tishi_Dialog;
import com.bmw.peek2slite.view.view.Vertical_seekbar;
import com.bmw.peek2slite.view.viewImpl.PreviewImpl;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class PreviewActivity extends BaseActivity implements PreviewImpl {

    private static final String TAG = "main/PreviewActivity";

    @Bind(R.id.main_surface)
    SurfaceView surfaceView;
    @Bind(R.id.battery)
    ImageView battery;//电池电量显示
    @Bind(R.id.lights_adjust)
    Vertical_seekbar lightAdjust;//灯光调节
    @Bind(R.id.lights_show)
    TextView light_text;
    @Bind(R.id.Records)
    ImageView recordBtn;

    private ControlPresenter cPresenter;//socket控制执行者
    private boolean isHighBeam;
    private int high_num; //记录远光灯
    private int low_num;  //记录近光灯
    private SharedPreferences sharedPreferences;
    private HCNetSdkLogin hcNetSdkLogin;
    private VideoCutPresenter videoCutPresenter;
    private Handler handler = new Handler();
    private long key_back_time;
    private int key_back;
    private boolean isRecordOpen;
    private Executor cacheThreadPool;
    private boolean isRanging;  //测距
    private String mBiaojiText;

//    private WifiConnect wifiConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.bind(this);

        log("数据：主线程号：" + Thread.currentThread().getId());

        cacheThreadPool = Executors.newCachedThreadPool();
        cPresenter = new ControlPresentImpl2(this, this);
        sharedPreferences = getSharedPreferences(AdvanceSetInfo.ANVANCE_SHARE, Context.MODE_PRIVATE);
        high_num = sharedPreferences.getInt(AdvanceSetInfo.HIGH_LIGHT, 0);
        low_num = sharedPreferences.getInt(AdvanceSetInfo.LOW_LIGHT, 0);
        hcNetSdkLogin = new HCNetSdkLoginImpl(this, this, surfaceView);
        videoCutPresenter = new VideoCutPresentImpl(this);
        cPresenter.startThread();
        mToast = Toast.makeText(this, "再按一次返回键退出预览！", Toast.LENGTH_SHORT);

        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕信息
        getWindowManager().getDefaultDisplay().getMetrics(dm);

//        initWifiConnect();
        lightAjust();
        initHaiKangConn();

        initWifiConnect();
    }

    private void initWifiConnect() {
        if (!Login_info.getInstance().isWifi_auto())
            return;
//        if (!WifiConnectNew.getInstance().isAlreadyConnectMain() && !WifiConnectNew.getInstance().isAlreadyConnectRepeater()) {
//            if (!WifiConnectNew.getInstance().getIsOpenThread())
//                WifiConnectNew.getInstance().starConnect();
//        }
        MyIntentService.stopIntentService(this);
        String ssid = Login_info.getInstance().isWifiIsRepeater() ? Login_info.baseRepeaterWifiSSID : Login_info.baseMainFrameWifiSSID;
        MyIntentService.startActionWifiConnect(this, ssid, Login_info.baseRepeaterWifiPassword, Login_info.getInstance().getSocket_ip());

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initHaiKangConn() {
        cacheThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                while (All_id_Info.getInstance().getM_iLogID() >= 0) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                hcNetSdkLogin.connectDevice();
                sleep(1000);
            }
        });
    }

    private void lightAjust() {
        lightAdjust.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                log("seekbar: onProgressChanged: running");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStartTrackingTouch: ");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                log("seekbar: onStopTrackingTouch: ");
                if (isHighBeam) {
                    high_num = progress;
                    cPresenter.high_beam_open(high_num);
                } else {
                    low_num = progress;
                    cPresenter.low_beam_open(low_num);
                }
                if (progress == 0) {
                    if (isHighBeam)
                        cPresenter.high_beam_close();
                    else
                        cPresenter.low_beam_close();
                }
            }
        });
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @OnClick({R.id.lights_switch, R.id.Records, R.id.screenShot, R.id.autoHorizontal})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lights_switch:
                if (isHighBeam) {
                    light_text.setText("近光灯");
                    isHighBeam = false;
                    lightAdjust.setProgress(low_num);
                } else {
                    light_text.setText("远光灯");
                    isHighBeam = true;
                    lightAdjust.setProgress(high_num);
                }
                break;
            case R.id.Records: //录像
                videoCutPresenter.record();

//                videoCutPresenter.record();
//                addRecordHead();
                break;
            case R.id.screenShot://截图
                videoCutPresenter.capture();
                break;
            case R.id.autoHorizontal://自动水平
                cPresenter.autoHorizontal();
                break;

        }
    }


    @OnTouch({R.id.zoom_add, R.id.zoom_sub, R.id.size_add, R.id.size_sub, R.id.lights_switch, R.id.up, R.id.down})
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.zoom_add: //聚焦近

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    cPresenter.zoom_add();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    cPresenter.stop();
                }
                break;
            case R.id.zoom_sub://聚焦远

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    cPresenter.zoom_sub();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    cPresenter.stop();
                }
                break;
            case R.id.size_add://变倍变长
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    cPresenter.size_add();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    cPresenter.stop();
                }
                break;
            case R.id.size_sub://变倍变短
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    cPresenter.size_sub();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    cPresenter.stop();
                }
                break;
            case R.id.up://控制摄像头向上

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    cPresenter.up();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    cPresenter.stop();
                }
                break;
            case R.id.down://控制摄像头向下

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    cPresenter.down();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    cPresenter.stop();
                }
                break;
        }
        return false;
    }


    @Override//回调改变提示信息
    public void iToast(final String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                toast(msg);
            }
        });
    }

    @Override
    protected void onStop() {


//        lightAdjust.setProgress(0);
//        cPresenter.low_beam_close();
//        cPresenter.high_beam_close();
//        cPresenter.stop();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(AdvanceSetInfo.LOW_LIGHT, low_num);
        editor.putInt(AdvanceSetInfo.HIGH_LIGHT, high_num);
        editor.commit();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    //释放资源
    @Override
    protected void onDestroy() {

//        if (wifiConnect != null)
//            wifiConnect.setFinish(true);

        log("previewActivity destroy!");
//        WifiConnectNew.getInstance().setFinish(true);
        videoCutPresenter.release();
        mToast.cancel();
        Log.d(TAG, "onDestroy:start ");

        cPresenter.release();
        Log.d(TAG, "onDestroy:finish ");
        cPresenter = null;
//        hcNetSdkLogin.release();
        new Thread(new Runnable() {
            @Override
            public void run() {
                hcNetSdkLogin.release();
            }
        }).start();
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    ;


    //设置返回控制
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            if ((System.currentTimeMillis() - key_back_time) >= 5000) {
                key_back = 0;
            }
            if (key_back != 1) {
//               toast("再按一次返回键退出预览！");
                mToast.show();
                mToast.show();

                key_back_time = System.currentTimeMillis();
            }
            if (key_back == 1) {
                mToast.cancel();
                finish();
            }
            key_back++;

            return true;
        }
        if (keyCode == event.KEYCODE_HOME) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    //界面回归自动播放
    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart: ");
        super.onRestart();
    }

    @Override
    public void setBattery(int pic) {
        if (battery != null)
            battery.setImageResource(pic);
    }

    @Override
    public void ierror(String msg) {
        error(msg);
    }

    @Override
    public void ilog(String msg) {
        log(msg);
    }

    @Override
    public void record(int i, boolean isRecord) {
        switch (i) {
            case 0:
                if (isRecord) {
                    isRecordOpen = true;
                    toast("开始录像");
                    if (recordBtn != null)
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                recordBtn.setImageResource(R.mipmap.recordpress);
                            }
                        });
                } else {
                    toast("开始录像失败");
                }
                break;
            case 1:
                if (isRecord) {
                    isRecordOpen = false;
                    toast("停止录像");
                    if (recordBtn != null)
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                recordBtn.setImageResource(R.mipmap.record);
                            }
                        });
                } else {
                    toast("停止录像失败");
                }
                break;
        }
    }


    @Override
    public void setRecordBtn(int i) {
        recordBtn.setImageResource(i);
    }


    @Override
    public void stop() {
        this.finish();
    }


    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void capture(final String path) {

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Capture_tishi_Dialog dialog = new Capture_tishi_Dialog(PreviewActivity.this, "是否编辑图片？", path);

                dialog.setSureOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(PreviewActivity.this, PictureEditActivity.class);
                        intent.putExtra("path", path);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                dialog.setCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });


    }

}
