package com.bmw.peek2slite.view.dialog;

/**
 * Created by admin on 2016/9/19.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.bmw.peek2slite.R;


public class ClockShow_Dialog {

    private static final String TAG = "YMH";
    private final AlertDialog dialog;

    public ClockShow_Dialog(final Context context) {


//        dialog = new Dialog(context,R.style.fullScreen_dialog);
        dialog = new AlertDialog.Builder(context).create();
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.dialog_anim);
        dialog.setView(new EditText(context));//实现弹出虚拟键盘
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

        window.setBackgroundDrawableResource(android.R.color.transparent);//加上这句实现满屏效果
        window.setGravity(Gravity.CENTER); // 非常重要：设置对话框弹出的位置
        window.setContentView(R.layout.dialog_clock_show);

    }

    /**
     * 关闭对话框
     */
    public void dismiss() {
        dialog.dismiss();
    }

    /*
    String distance, String style, String grade, String name, String clock, String length, String detail
    */

}