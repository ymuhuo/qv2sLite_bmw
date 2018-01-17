package com.bmw.peek2slite.view.dialog;

/**
 * Created by admin on 2016/9/19.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.bmw.peek2slite.R;
import com.bmw.peek2slite.model.AdvanceSetInfo;


public class AdvancedSetDialog {

    private static final String TAG = "YMH";
    private Switch fangdou;
    private Switch kuandongtai;
    private Switch qiangguangyizhi;
    private Switch touwu;
    private Switch gaoganguang;
    private AlertDialog dialog;

    public AdvancedSetDialog(Context context) {


        dialog = new AlertDialog.Builder(context).create();
//        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.dialog_anim);
        dialog.show();
        WindowManager manager = (WindowManager) context.
                getSystemService(Context.WINDOW_SERVICE);

        //为获取屏幕宽、高
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
//        p.height = (int) (dm.heightPixels() * 0.3);   //高度设置为屏幕的0.3
//        p.width = (int) (dm.widthPixels);    //宽度设置为全屏
        //设置生效
        window.setAttributes(p);

//        window.setBackgroundDrawableResource(android.R.color.transparent);//加上这句实现满屏效果
        window.setGravity(Gravity.CENTER); // 非常重要：设置对话框弹出的位置
        window.setContentView(R.layout.advance_setting);
        fangdou = (Switch) window.findViewById(R.id.fangdou_switch);
        kuandongtai = (Switch) window.findViewById(R.id.kuandongtai_switch);
        qiangguangyizhi = (Switch) window.findViewById(R.id.qiangguangyizhi_switch);
        touwu = (Switch) window.findViewById(R.id.touwu_switch);
        gaoganguang = (Switch) window.findViewById(R.id.gaoganguang_switch);
        initSwitch(context);
        window.findViewById(R.id.advance_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private void initSwitch(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AdvanceSetInfo.ANVANCE_SHARE,Context.MODE_PRIVATE);
        fangdou.setChecked(sharedPreferences.getBoolean(AdvanceSetInfo.PIC_FANGDOU,false));
        kuandongtai.setChecked(sharedPreferences.getBoolean(AdvanceSetInfo.KUANDONGTAI,false));
        qiangguangyizhi.setChecked(sharedPreferences.getBoolean(AdvanceSetInfo.LIGHT_YIZHI,false));
        touwu.setChecked(sharedPreferences.getBoolean(AdvanceSetInfo.TOUWU,false));
        gaoganguang.setChecked(sharedPreferences.getBoolean(AdvanceSetInfo.GAOGANLIGHT,false));
    }


    public void setListening(CompoundButton.OnCheckedChangeListener listener){
        fangdou.setOnCheckedChangeListener(listener);
        kuandongtai.setOnCheckedChangeListener(listener);
        qiangguangyizhi.setOnCheckedChangeListener(listener);
        touwu.setOnCheckedChangeListener(listener);
        gaoganguang.setOnCheckedChangeListener(listener);
    }

    /**
     * 关闭对话框
     */
    public void dismiss() {
        dialog.dismiss();
    }


}