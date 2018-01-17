package com.bmw.peek2slite.view.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.bmw.peek2slite.R;
import com.bmw.peek2slite.model.UpdateInfo;
import com.bmw.peek2slite.presenter.UpdatePresenter;
import com.bmw.peek2slite.presenter.impl.UpdatePresentImpl;
import com.bmw.peek2slite.service.MyIntentService;
import com.bmw.peek2slite.utils.LogUtil;
import com.bmw.peek2slite.utils.NetWorkUtil;
import com.bmw.peek2slite.utils.WifiAdmin;
import com.bmw.peek2slite.view.viewImpl.UpdateImpl;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;

public class UpdateActivity extends BaseActivity {

    @Bind(R.id.updateActivity_content)
    TextView mContent;
    @Bind(R.id.updateActivity_reCheck)
    Button mReCheckBtn;
    @Bind(R.id.update_progressBar)
    ProgressBar mProgressBar;

    private WifiAdmin wifiAdmin;
    private boolean isCheckNet;
    private boolean hasNetToUpdate;
    private UpdatePresenter updatePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        ButterKnife.bind(this);
        removeOtherActivity();
        MyIntentService.stopIntentService(this);
        Bmob.initialize(this, "7d05288ed91423eac540b1a6bbfb9cd8");
        disConnectWifi();
        isCheckNet = true;
        setCheckNetState();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCheckNet = false;
    }

    private void disConnectWifi() {
        wifiAdmin = new WifiAdmin(this);
        wifiAdmin.disconnectWifi(wifiAdmin.getNetworkId());
    }


    private void justNetState() {
        isCheckNet = true;
        int netState = NetWorkUtil.isNetworkAvailable(this);
        isCheckNet = false;
        switch (netState) {
            case 0: //可联网
                hasNetToUpdate = true;
                startUpdaxte();
                break;
            case 1: //不可联网，无wifi
                connectNetFalse();
                break;
            case 2: //不可联网，有wifi
                disConnectWifi();
                connectNetFalse();
                break;
        }
    }

    private void startUpdaxte() {
        if(mContent!=null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mContent.setText(getResources().getString(R.string.checkUpdateInfo));
                    updatePresenter = new UpdatePresentImpl(new UpdateImpl() {
                        @Override
                        public void update(String apkName) {
                            updateApk(apkName);
                        }

                        @Override
                        public void getUpdateInfo(UpdateInfo updateInfo) {
                            if(updateInfo!=null) {
                                if (isNeedUpdate(updateInfo)) {
                                    runOnUi(getResources().getString(R.string.updateToVersion)+updateInfo.getVersion()+"?\n\n更新信息：\n"+updateInfo.getDescription()+"");
                                    mReCheckBtn.setText(getString(R.string.updateSure));
                                    mReCheckBtn.setVisibility(View.VISIBLE);
                                } else {
                                    runOnUi(getString(R.string.noNeedUpdate));
                                }
                            }else {
                                hasNetToUpdate = false;
                                connectNetFalse();
//                wifiPresenter.startPlayVideo();
//                wifiPresenter.connect();
                            }
                        }

                        @Override
                        public void showUpdateProgress(final int progress) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(mProgressBar != null){
                                        mProgressBar.setProgress(progress);
                                    }
                                }
                            });
                        }

                        @Override
                        public void downloadSuccess(String apkName) {
                            updateApk(apkName);
                            UpdateActivity.this.removeALLActivity();
                        }

                        @Override
                        public void connectNetFalse() {
                            hasNetToUpdate = false;
                            if (mContent != null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mContent.setText(getString(R.string.noInternet));
                                        mReCheckBtn.setText(getString(R.string.reTry));
                                        mReCheckBtn.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        }

                        @Override
                        public void noData() {
                            runOnUi(getString(R.string.noNeedUpdate));
                        }
                    }, UpdateActivity.this, getVersion());
                }
            });


        }
    }

    private void runOnUi(final String str){
        if(mContent!= null)
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mContent.setText(str);
                }
            });
    }

    private boolean isNeedUpdate(UpdateInfo info) {
        String v = info.getVersion(); // 最新版本的版本号
        if(v.contains("V") && v.contains("F")) {
            String net_firstStr = v.substring(v.indexOf("V") + 1, v.indexOf("F"));
            String net_secondStr = v.substring(v.indexOf("F") + 1, v.indexOf("D"));

            int net_firstNum = Integer.valueOf(net_firstStr);
            int net_secondNum = Integer.valueOf(net_secondStr);

            String l_version = getVersion();

            String l_firstStr = l_version.substring(v.indexOf("V") + 1, v.indexOf("F"));
            String l_secondStr = l_version.substring(v.indexOf("F") + 1, v.indexOf("D"));

            int l_firstNum = Integer.valueOf(l_firstStr);
            int l_secondNum = Integer.valueOf(l_secondStr);

            LogUtil.log(" firstNum = " + net_firstNum + " secondNum = " + net_secondNum+" lfirst = "+ l_firstNum+" lsecond = "+ l_secondNum);
            if(l_firstNum != net_firstNum){
                if(l_firstNum>=net_firstNum)
                    return false;
                else
                    return true;
            }else {
                if(l_secondNum>=net_secondNum)
                    return false;
                else
                    return true;
            }
        }else {
            return false;
        }
    }


    // 获取当前版本的版本号
    private String getVersion() {
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                   getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return getString(R.string.noVersion);
        }
    }


    public void updateApk(String name) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), name)),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }

    public void connectNetFalse() {
        if (mContent != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mContent.setText(getString(R.string.noInternet));
                    mReCheckBtn.setText(getString(R.string.reTry));
                    mReCheckBtn.setVisibility(View.VISIBLE);
                }
            });
        }
    }


    private void setCheckNetState() {
        final int[] checkNum = {0};
        String checkInternet = getString(R.string.isCheckingInternet);
        final String[] checkStrings = new String[]
                {
                        checkInternet+" . . .",
                        checkInternet+" . .",
                        checkInternet+" .",
                        checkInternet+" . .",
                };
        isCheckNet = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                justNetState();
            }
        }).start();

        if (isCheckNet) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isCheckNet) {
                        if (mContent != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    checkNum[0]++;
                                    if (checkNum[0] > 3) {
                                        checkNum[0] = 0;
                                    }
                                    mContent.setText(checkStrings[checkNum[0]]);
                                }
                            });
                        }
                        sleep(1000);
                    }
                }
            }).start();
        }
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @OnClick({R.id.updateActivity_content, R.id.updateActivity_reCheck,R.id.updateActivity_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.updateActivity_reCheck:
                if(!hasNetToUpdate) {
                    setCheckNetState();
                    mReCheckBtn.setVisibility(View.INVISIBLE);
                }else{
                    if(updatePresenter != null) {
                        updatePresenter.downloadDate();
                        mReCheckBtn.setVisibility(View.INVISIBLE);
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.updateActivity_cancel:
                removeALLActivity();
                break;
        }
    }
}
