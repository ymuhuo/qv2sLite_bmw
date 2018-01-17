package com.bmw.peek2slite.view.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.bmw.peek2slite.BaseApplication;
import com.bmw.peek2slite.R;
import com.bmw.peek2slite.model.AdvanceSetInfo;
import com.bmw.peek2slite.model.Login_info;
import com.bmw.peek2slite.utils.FileUtil;
import com.bmw.peek2slite.view.dialog.AdvancedSetDialog;
import com.bmw.peek2slite.view.dialog.Normal_Dialog;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AdvanceActivity extends AppCompatActivity {

    @Bind(R.id.switch_advance_yingjiema)
    Switch switchYingjiema;
    @Bind(R.id.tv_advance_fileSavePath)
    Switch switchSavePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance);
        ButterKnife.bind(this);

        initYingJieMa();
        initFileSavePath();

    }


    private void initYingJieMa() {
        switchYingjiema.setChecked(Login_info.getInstance().isYingJieMa());
        switchYingjiema.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Login_info.getInstance().setYingJieMa(b);
            }
        });
    }


    private void initFileSavePath() {
        switchSavePath.setChecked(Login_info.getInstance().isSaveToExSdcard());
        switchSavePath.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    sureCheckSDcard();
                else
                    Login_info.getInstance().setSaveToExSdcard(b);
            }
        });
    }


    private void sureCheckSDcard() {
        List<String> sdcardList = FileUtil.getRealExtSDCardPath(this);
        if(sdcardList.size() <=1){
            onleInlaySdcardExist();
            return;
        }
        if(!sdcardList.get(1).contains("Android/data")){
            Login_info.getInstance().setSaveToExSdcard(true);
            FileUtil.pathIsExist();
            return;
        }

        final Normal_Dialog dialog = new Normal_Dialog(this,"警告","受谷歌政策影响，4.4系统在使用二级存储卡时存在一定风险：\n\n  1.文件存储位置固定为：\n"+
                sdcardList.get(1)+"，且不支持移动！\n\n  2.软件卸载后，文件将被自动删除！\n\n确定要将文件默认存储位置定为外置存储卡吗？");

        dialog.setSureOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login_info.getInstance().setSaveToExSdcard(true);
                FileUtil.pathIsExist();
                dialog.dismiss();
            }
        });
        dialog.setCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(!Login_info.getInstance().isSaveToExSdcard())
                    switchSavePath.setChecked(false);
            }
        });
    }


    private void onleInlaySdcardExist() {
        BaseApplication.toast("无可用外置存储设备！");
        switchSavePath.setChecked(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.cameraSet})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cameraSet:
                setAdvance_set();
                break;
        }
    }


    private void setAdvance_set(){

        AdvancedSetDialog dialog = new AdvancedSetDialog(this);
        final SharedPreferences sharendPreferences = getSharedPreferences(AdvanceSetInfo.ANVANCE_SHARE, Context.MODE_PRIVATE);
        dialog.setListening(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                SharedPreferences.Editor editor = sharendPreferences.edit();
                switch (compoundButton.getId()){
                    case R.id.fangdou_switch:
                        editor.putBoolean(AdvanceSetInfo.PIC_FANGDOU,b);
                        break;
                    case R.id.kuandongtai_switch:
                        editor.putBoolean(AdvanceSetInfo.KUANDONGTAI,b);
                        break;
                    case R.id.gaoganguang_switch:
                        editor.putBoolean(AdvanceSetInfo.GAOGANLIGHT,b);
                        break;
                    case R.id.qiangguangyizhi_switch:
                        editor.putBoolean(AdvanceSetInfo.LIGHT_YIZHI,b);
                        break;
                    case R.id.touwu_switch:
                        editor.putBoolean(AdvanceSetInfo.TOUWU,b);
                        break;
                }
                editor.putBoolean(AdvanceSetInfo.ISDONE,true);
                editor.commit();
            }
        });
    }
}
