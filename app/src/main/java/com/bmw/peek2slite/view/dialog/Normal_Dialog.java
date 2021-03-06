package com.bmw.peek2slite.view.dialog;

/**
 * Created by admin on 2016/9/19.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.bmw.peek2slite.R;


public class Normal_Dialog {

    private static final String TAG = "YMH";
    private AlertDialog dialog;
    private TextView sureBtn, cancelBtn;
    private TextView titleTv,messageTv;

    public Normal_Dialog(Context context,String title,String message) {


//        dialog = new Dialog(context,R.style.fullScreen_dialog);
        dialog = new AlertDialog.Builder(context).create();
        dialog.setCanceledOnTouchOutside(false);
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
        p.width = (int) (dm.widthPixels*0.5);    //宽度设置为全屏
        //设置生效
        window.setAttributes(p);

        window.setBackgroundDrawableResource(android.R.color.transparent);//加上这句实现满屏效果
        window.setGravity(Gravity.CENTER); // 非常重要：设置对话框弹出的位置
        window.setContentView(R.layout.dialog_nomal);
        sureBtn = (TextView) window.findViewById(R.id.normal_dialog_sure);
        cancelBtn = (TextView) window.findViewById(R.id.normal_dialog_cancel);

        titleTv = (TextView) window.findViewById(R.id.normal_dialog_title);
        messageTv = (TextView) window.findViewById(R.id.normal_dialog_message);

        titleTv.setText(title);
        messageTv.setText(message);
    }

    public void setSureOnClickListener(View.OnClickListener listener){
        sureBtn.setOnClickListener(listener);
    }

    public void setCancelClickListener(View.OnClickListener listener){
        cancelBtn.setOnClickListener(listener);
    }



    public void setOnDismissListener(DialogInterface.OnDismissListener listener){
        dialog.setOnDismissListener(listener);
    }
    /**
     * 关闭对话框
     */
    public void dismiss() {
        dialog.dismiss();
    }


}