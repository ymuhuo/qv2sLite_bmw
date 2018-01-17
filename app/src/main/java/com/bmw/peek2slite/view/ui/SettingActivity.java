package com.bmw.peek2slite.view.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bmw.peek2slite.BaseApplication;
import com.bmw.peek2slite.Constant;
import com.bmw.peek2slite.R;
import com.bmw.peek2slite.presenter.impl.ControlPresentImpl2;
import com.bmw.peek2slite.utils.FileUtil;
import com.bmw.peek2slite.utils.FragmentUtil;
import com.bmw.peek2slite.utils.Manufacturer_FileUtil;
import com.bmw.peek2slite.utils.NetWorkUtil;
import com.bmw.peek2slite.view.dialog.DialogEdtNormalFragment;
import com.bmw.peek2slite.view.dialog.DialogManufacturerFragment;
import com.bmw.peek2slite.view.dialog.DialogNormalFragment;
import com.bmw.peek2slite.view.dialog.OnDialogFragmentClickListener;
import com.bmw.peek2slite.view.dialog.ScreenLightDialog;
import com.bmw.peek2slite.view.dialog.SettingDialog;
import com.bmw.peek2slite.view.dialog.SystemMsgDialog;
import com.bmw.peek2slite.view.viewImpl.UpdateViewImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import cn.bmob.v3.Bmob;

public class SettingActivity extends BaseActivity implements UpdateViewImpl {

    @Bind(R.id.light_set)
    TextView light_set;
    @Bind(R.id.screen)
    LinearLayout main;
    @Bind(R.id.sys_update)
    TextView updateBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        //默认初始化
//        Bmob.initialize(this, "2733df1cda91841bbac921e164dc70d4");

       /* if(Login_info.getInstance().isWifi_auto()) {
            WifiAdmin wifiAdmin = new WifiAdmin(this);
            wifiAdmin.closeWifi();
        }*/

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            Log.d("wait", "onKeyDown: ");
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick({R.id.sys_info, R.id.light_set, R.id.advance_set, R.id.sys_stat, R.id.sys_update})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.light_set:
                setScreenLight();
                break;
            case R.id.advance_set:
//                setAdvance_set();
                startActivity(new Intent(this, AdvanceActivity.class));
                break;
            case R.id.sys_stat:
                new SystemMsgDialog(this, getVersion());
                break;
            case R.id.sys_update:
                DialogNormalFragment dialogNormalFragment = DialogNormalFragment.getInstance(
                        getString(R.string.isUpdateApp),
                        getString(R.string.updateAppAndClosePreview),
                        getString(R.string.sure),
                        getString(R.string.cancel), true);
                dialogNormalFragment.setOnDialogFragmentClickListener(new OnDialogFragmentClickListener() {
                    @Override
                    public void sure() {
                        startActivity(new Intent(context(), UpdateActivity.class));
                    }

                    @Override
                    public void cancel() {

                    }
                });
                FragmentUtil.showDialogFragment(getSupportFragmentManager(), dialogNormalFragment, "DialogNormalFragment");


                break;
            case R.id.sys_info:
                SettingDialog settingDialog = new SettingDialog(this);
                settingDialog.setOnSettingChangeListener(new SettingDialog.OnSettingChangeListener() {
                    @Override
                    public void changeReporter(boolean isChange) {
                        if (isChange)
                            new ControlPresentImpl2(null, SettingActivity.this).resetSocket();
                    }
                });
                break;
        }
    }


    @OnLongClick({
            R.id.sys_stat
    })
    public boolean onLongClick(View v) {
        if (Constant.IS_NEUTRAL_VERSION) {
            final DialogEdtNormalFragment dialogEdtNormalFragment = DialogEdtNormalFragment.getInstance("正在修改系统参数，请输入修改密码！"
                    , "", null, null, false);
            dialogEdtNormalFragment.setOnEdtDialogItemClickListener(new DialogEdtNormalFragment.OnEdtDialogItemClickListener() {
                @Override
                public void next_step(String msg) {
                    if (msg != null && msg.equals(Constant.VERSION_PASS)) {
                        setManufacturerName();
                    } else {
                        BaseApplication.toast(getString(R.string.error_pass));
                        dialogEdtNormalFragment.dismiss();
                    }
                }

                @Override
                public void cancel() {
                    dialogEdtNormalFragment.dismiss();
                }
            });
            FragmentUtil.showDialogFragment(getSupportFragmentManager(), dialogEdtNormalFragment, "DialogEdtNormalFragment");
        }
        return true;
    }


    private void setManufacturerName() {
        String manufacturerName = Manufacturer_FileUtil.readFileMessage("mnt/sdcard/Android/obj/com.bmw.peek2s", "manufacturer.txt");
        if (manufacturerName == null)
            manufacturerName = "";
        DialogManufacturerFragment dialogEdtNormalFragment = DialogManufacturerFragment.getInstance("正在修改生产商名称！"
                , manufacturerName, Constant.LOGO_PATH, null, null, false);
        dialogEdtNormalFragment.setOnEdtDialogItemClickListener(new DialogManufacturerFragment.OnManufactureFinishListener() {
            @Override
            public void finish(final String manufacturerName, final String imgPath) {
                BaseApplication.MAIN_EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        saveData(manufacturerName, imgPath);
                    }
                });
            }

            @Override
            public void cancel() {

            }
        });
        FragmentUtil.showDialogFragment(getSupportFragmentManager(), dialogEdtNormalFragment, "DialogManufacturerFragment");
    }

    private void saveData(String manufacturerName, String imgPath) {
        boolean isSet = false;
        if (manufacturerName == null || manufacturerName.isEmpty()) {
            File file = new File("mnt/sdcard/Android/obj/com.bmw.peek2s", "manufacturer.txt");
            if (file.exists())
                isSet = file.delete();
            else
                isSet = true;
        } else {
            isSet = Manufacturer_FileUtil.writeFile("mnt/sdcard/Android/obj/com.bmw.peek2s", "manufacturer.txt", manufacturerName);
        }

        if (imgPath != null) {
            isSet = FileUtil.replaceImage(imgPath, Constant.LOGO_PATH);
        } else {
            File file = new File(Constant.LOGO_PATH);
            if (file.exists()) {
                file.delete();
                FileUtil.updateSystemLibFile(Constant.LOGO_PATH);
            }
        }


        final boolean isSetFinal = isSet;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isSetFinal) {
                    BaseApplication.toast("设置成功");
                } else {
                    BaseApplication.toast("设置失败，请稍后重试！");
                }
            }
        });

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
            return "版本号未知";
        }
    }


    private void setScreenLight() {
        //取得当前亮度
        int normal = Settings.System.getInt(getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, 255);
//        AdvancedSetDialog dialog = new AdvancedSetDialog(this,normal);
//        ScreenPopupWindow dialog = new ScreenPopupWindow(this,normal);
        ScreenLightDialog dialog = new ScreenLightDialog(this, normal);
//        dialog.showPopupWindow(main);
        dialog.setDialogSeeekbarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //取得当前进度
                int tmpInt = seekBar.getProgress();

                //当进度小于80时，设置成40，防止太黑看不见的后果。
                if (tmpInt < 40) {
                    tmpInt = 40;
                }

                //根据当前进度改变亮度
                Settings.System.putInt(getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, tmpInt);
                tmpInt = Settings.System.getInt(getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, -1);
                WindowManager.LayoutParams wl = getWindow()
                        .getAttributes();

                float tmpFloat = (float) tmpInt / 255;
                if (tmpFloat > 0 && tmpFloat <= 1) {
                    wl.screenBrightness = tmpFloat;
                }
                getWindow().setAttributes(wl);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
            }
        });
    }

    @Override
    public void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void update(String name) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), name)),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
