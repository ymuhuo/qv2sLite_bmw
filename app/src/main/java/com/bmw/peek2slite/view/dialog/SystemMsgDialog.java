package com.bmw.peek2slite.view.dialog;

/**
 * Created by admin on 2016/9/19.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.bmw.peek2slite.Constant;
import com.bmw.peek2slite.R;
import com.bmw.peek2slite.utils.Manufacturer_FileUtil;


public class SystemMsgDialog {

    private static final String TAG = "YMH";
    private AlertDialog dialog;
    private TextView tv_manufacturer_name;
    private TextView tv_manufacturer_title;
    private TextView tv_xinghao_title;
    private TextView tv_xinghao_name;

    public SystemMsgDialog(Context context, String version) {


        dialog = new AlertDialog.Builder(context).create();
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
        window.setContentView(R.layout.system_msg);
        TextView version_text = (TextView) window.findViewById(R.id.sys_version);
        tv_manufacturer_name = (TextView) window.findViewById(R.id.manufacturer_name);
        tv_manufacturer_title = (TextView) window.findViewById(R.id.shengchanshang);
        tv_xinghao_title = (TextView) window.findViewById(R.id.xinghao_text);
        tv_xinghao_name = (TextView) window.findViewById(R.id.xinghao_text_name);
        version_text.setText(version);
        if (Constant.IS_NEUTRAL_VERSION) {
            tv_manufacturer_title.setVisibility(View.GONE);
            tv_manufacturer_name.setVisibility(View.GONE);
            tv_xinghao_name.setVisibility(View.GONE);
            tv_xinghao_title.setVisibility(View.GONE);
            String manufacturerName = Manufacturer_FileUtil.readFileMessage("mnt/sdcard/Android/obj/com.bmw.peek2s", "manufacturer.txt");
            if (manufacturerName != null && !manufacturerName.isEmpty()) {
                tv_manufacturer_name.setText(manufacturerName);
                tv_manufacturer_title.setVisibility(View.VISIBLE);
                tv_manufacturer_name.setVisibility(View.VISIBLE);
            }
        }

    }


    /**
     * 关闭对话框
     */
    public void dismiss() {
        dialog.dismiss();
    }


}