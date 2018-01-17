package com.bmw.peek2slite.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bmw.peek2slite.BaseApplication;
import com.bmw.peek2slite.Constant;
import com.bmw.peek2slite.R;
import com.bmw.peek2slite.utils.FileUtil;
import com.bmw.peek2slite.utils.FragmentUtil;
import com.bmw.peek2slite.view.ui.ManufacturerLogoActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.view.annotation.event.OnKey;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by admin on 2017/5/17.
 */

public class DialogManufacturerFragment extends DialogFragment {


    @Bind(R.id.tv_title)
    TextView mTitle;
    @Bind(R.id.edt_message)
    EditText mMessage;
    @Bind(R.id.normal_dialog_cancel)
    TextView mCancel;
    @Bind(R.id.normal_dialog_sure)
    TextView mSure;
    @Bind(R.id.img_manufacturer)
    ImageView img_manufacturer;
    private View mView;
    private boolean mIsNeedDismissCancel;
    private Timer timer;
    private TimerTask timerCancelTask;
    private int allTime = 6;
    private String cancelStr = "取消";
    private String mOldName = null;
    public static final int REQUEST_CODE = 110;
    public static final int RESULT_CODE = 119;
    public static final String IMG_PATH_FLAG = "IMG_PATH_FLAG";

    public static DialogManufacturerFragment getInstance(String title, String message, String imgPath, String sureName, String cancelName, boolean isAutoCancel) {
        DialogManufacturerFragment dialogNormalFragment = new DialogManufacturerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("message", message);
        if (imgPath != null) {
            bundle.putString("imgPath", imgPath);
        }
        if (sureName != null)
            bundle.putString("sureName", sureName);
        if (cancelName != null)
            bundle.putString("cancelName", cancelName);
        bundle.putBoolean("isAutoCancel", isAutoCancel);
        dialogNormalFragment.setArguments(bundle);
        return dialogNormalFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_manufacturer, null);
        ButterKnife.bind(this, mView);
        mTitle.setText(getArguments().getString("title"));
        mMessage.setText(getArguments().getString("message"));
        String sureName = getArguments().getString("sureName");
        mOldName = sureName;
        String cancelName = getArguments().getString("cancelName");
        if (sureName != null)
            mSure.setText(sureName);
        if (cancelName != null) {
            mCancel.setText(cancelName);
            cancelStr = cancelName;
        }

        String imgPath = getArguments().getString("imgPath");
        if (imgPath != null) {
            showImgNoCache(imgPath);
//            Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
//            if (img_manufacturer != null)
//                img_manufacturer.setImageBitmap(bitmap);

        }
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(mView).create();
        dialog.setCanceledOnTouchOutside(false);
        setWindowStyle(dialog);

        return dialog;
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        if (getArguments().getBoolean("isAutoCancel")) {
            timer = new Timer();
            timerCancelTask = new TimerTask() {
                @Override
                public void run() {
                    if (mCancel != null)
                        mCancel.post(new Runnable() {
                            @Override
                            public void run() {
                                if (allTime == 0) {
                                    getDialog().dismiss();
                                    if (listener != null)
                                        listener.cancel();
                                }
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(cancelStr).append("(").append(allTime).append("s)");
                                mCancel.setText(stringBuilder.toString());
                                allTime--;
                            }
                        });
                }
            };
            timer.schedule(timerCancelTask, 0, 1000);
        }
        return super.show(transaction, tag);
    }

    private void setWindowStyle(Dialog dialog) {
        Window window = dialog.getWindow();

        window.setWindowAnimations(R.style.dialog_anim);
        WindowManager manager = (WindowManager) getActivity().
                getSystemService(Context.WINDOW_SERVICE);

        //为获取屏幕宽、高
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams p = window.getAttributes();  //获取对话框当前的参数值

        p.height = (int) (dm.heightPixels);   //高度设置为屏幕的0.3
        p.width = (int) (dm.widthPixels * 0.5);    //宽度设置为全屏
        //设置生效
        window.setAttributes(p);
        window.setBackgroundDrawableResource(android.R.color.transparent);//加上这句实现满屏效果
        window.setGravity(Gravity.CENTER); // 非常重要：设置对话框弹出的位置
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(mView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void isNeedDismissCancel(boolean isNeed) {
        this.mIsNeedDismissCancel = isNeed;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mIsNeedDismissCancel && listener != null) {
            listener.cancel();
        }
        if (timerCancelTask != null)
            timerCancelTask.cancel();
        if (timer != null)
            timer.cancel();
    }

    //    public void setSureOnClickListener(View.OnClickListener listener){
//        mSure.setOnClickListener(listener);
//    }
//
//    public void setCancelClickListener(View.OnClickListener listener){
//        mCancel.setOnClickListener(listener);
//    }
/*
    @OnClick({R.id.normal_dialog_cancel, R.id.normal_dialog_sure})
    public void onClick(View mView) {
        switch (mView.getId()) {
            case R.id.normal_dialog_cancel:
                getDialog().dismiss();
                if (listener != null)
                    listener.cancel();
                break;
            case R.id.normal_dialog_sure:
                getDialog().dismiss();
                if (listener != null)
                    listener.sure();
                break;
        }
    }*/

    @OnClick({R.id.normal_dialog_cancel, R.id.normal_dialog_sure, R.id.img_manufacturer})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.normal_dialog_cancel:
                getDialog().dismiss();
                if (listener != null)
                    listener.cancel();
                break;
            case R.id.normal_dialog_sure:
                getDialog().dismiss();
                if (listener != null) {
                    String msg = mMessage.getText().toString();
                    listener.finish(msg, mImgPath);
                }
                break;
            case R.id.img_manufacturer:
                startActivityForResult(new Intent(this.getActivity(), ManufacturerLogoActivity.class), REQUEST_CODE);
                break;
        }
    }


    @OnLongClick({
            R.id.img_manufacturer
    })
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.img_manufacturer:
                mImgPath = null;
                if (img_manufacturer != null) {
                    img_manufacturer.post(new Runnable() {
                        @Override
                        public void run() {
                            img_manufacturer.setImageResource(R.mipmap.add_pic);
                        }
                    });
                }
                break;
        }
        return true;
    }

    public interface OnManufactureFinishListener {
        void finish(String manufacturerName, String imgPath);

        void cancel();
    }

    private OnManufactureFinishListener listener;

    public void setOnEdtDialogItemClickListener(OnManufactureFinishListener listener) {
        this.listener = listener;
    }

    private String mImgPath;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CODE && data != null) {
            mImgPath = data.getStringExtra(IMG_PATH_FLAG);
            showImg(mImgPath);
        }

    }

    private void showImg(String path) {
        if (img_manufacturer != null)
            Glide
                    .with(getContext())
                    .load(path)
                    .centerCrop()
                    .placeholder(R.mipmap.add_pic)
                    .crossFade()
                    .into(img_manufacturer);
    }

    private void showImgNoCache(String path) {
        Glide
                .with(getContext())
                .load(path)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .centerCrop()
                .placeholder(R.mipmap.add_pic)
                .crossFade()
                .into(img_manufacturer);
    }


}
