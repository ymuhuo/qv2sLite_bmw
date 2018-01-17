package com.bmw.peek2slite.view.dialog;

/**
 * Created by admin on 2016/9/19.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.bmw.peek2slite.BaseApplication;
import com.bmw.peek2slite.R;
import com.bmw.peek2slite.model.PictureQueXianInfo;
import com.bmw.peek2slite.model.QueXianInfo;
import com.bmw.peek2slite.utils.DbHelper;
import com.bmw.peek2slite.utils.LogUtil;
import com.bmw.peek2slite.utils.PullXmlParseUtil;
import com.bmw.peek2slite.view.adapter.SpinnerAdapter;
import com.bmw.peek2slite.view.view.MySpinner;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

import java.io.IOException;
import java.util.List;


public class Quexian_Edit_Dialog implements View.OnClickListener {

    private final DbUtils dbUtils;
    private final AlertDialog dialog;
    private EditText mDistanceEdt;
    private MySpinner mStyleSp;
    private MySpinner mNameSp;
    private EditText mClockEdt;
    private MySpinner mGradeSp;
    private EditText mLengthEdt;
    private EditText mDetailEdt;


    private TextView mNameTv;
    private TextView mClockTv;
    private TextView mGradeTv;
    private TextView mLengthTv;
    private TextView mDetailTv;
    private TextView mClockShow;

    private TextView sure_btn, cancel_btn;


    private Handler handler;
    private Context context;
    private boolean isChangeInfo;
    private PictureQueXianInfo mPictureQueXianInfo;

    private String[] mArray_style;
    private int mStyle_id = -1;
    private int mGrade_id = -1;
    private SpinnerAdapter mQuexianNameAdapter, mGradeAdapter, mStyleAdapter;
    private int mName_id = -1;
    private List<QueXianInfo> mQuexianList;

    public Quexian_Edit_Dialog(final Context context, boolean isChange, PictureQueXianInfo pictureQueXianInfo) {


//        dialog = new Dialog(context,R.style.fullScreen_dialog);
        this.mPictureQueXianInfo = pictureQueXianInfo;
        this.isChangeInfo = isChange;
        this.context = context;
        dbUtils = DbHelper.getDbUtils();
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
//        p.width = (int) (dm.widthPixels);    //宽度设置为全屏
        //设置生效
        window.setAttributes(p);

        window.setBackgroundDrawableResource(android.R.color.transparent);//加上这句实现满屏效果
        window.setGravity(Gravity.CENTER); // 非常重要：设置对话框弹出的位置
        window.setContentView(R.layout.dialog_picture_quexian_edit);

        initView(window);
        getXmlData();
        initHandler();
        initAdapter();
        initSpinner();
        if (pictureQueXianInfo != null)
            initData();


    }

    //初始化adapter
    private void initAdapter() {

        mQuexianNameAdapter = new SpinnerAdapter(context);
        mGradeAdapter = new SpinnerAdapter(context);
        mStyleAdapter = new SpinnerAdapter(context);


        mGradeSp.setAdapter(mGradeAdapter);
        mNameSp.setAdapter(mQuexianNameAdapter);
        mStyleSp.setAdapter(mStyleAdapter);

        mStyleAdapter.setStrings(mArray_style);
    }

    //获取基本数据
    private void getXmlData() {
        mArray_style = context.getResources().getStringArray(R.array.que_xian_style);
        try {
            mQuexianList = PullXmlParseUtil.parseQueXianXml(context.getAssets().open("DefectTypes.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //视图初始化
    private void initView(Window window) {

        mDistanceEdt = (EditText) window.findViewById(R.id.capture_quexian_distance_edt);
        mStyleSp = (MySpinner) window.findViewById(R.id.capture_quexian_style_sp);
        mNameSp = (MySpinner) window.findViewById(R.id.capture_quexian_name_sp);
        mClockEdt = (EditText) window.findViewById(R.id.capture_quexian_clock_edt);
        mGradeSp = (MySpinner) window.findViewById(R.id.capture_quexian_grade_sp);
        mLengthEdt = (EditText) window.findViewById(R.id.capture_quexian_length_edt);
        mDetailEdt = (EditText) window.findViewById(R.id.capture_quexian_detail_edt);
        mNameTv = (TextView) window.findViewById(R.id.capture_quexian_name_tv);
        mClockTv = (TextView) window.findViewById(R.id.capture_quexian_clock_tv);
        mGradeTv = (TextView) window.findViewById(R.id.capture_quexian_grade_tv);
        mLengthTv = (TextView) window.findViewById(R.id.capture_quexian_length_tv);
        mDetailTv = (TextView) window.findViewById(R.id.capture_quexian_detail_tv);
        mClockShow = (TextView) window.findViewById(R.id.tv_quexianEdit_clockShow);

        sure_btn = (TextView) window.findViewById(R.id.quexian_sure);
        cancel_btn = (TextView) window.findViewById(R.id.quexian_cancel);


        sure_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
        mClockShow.setOnClickListener(this);
    }

    private void initSpinner() {

        if (TextUtils.isEmpty(mStyleSp.getText().toString())) {
            mStyleSp.setText("请选择");
        }

        mStyleSp.setOnItemSelectedListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mStyleSp.setText(mArray_style[i]);
                mStyle_id = i;
                setNameAdapter(i);
                if (i != 0)
                    mNameSp.setText("请选择");
                else{
                    mName_id = -1;
                    mGrade_id = -1;
                }
                mStyleSp.dismissPop();
            }
        });
        mNameSp.setOnItemSelectedListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mName_id = i;
                mNameSp.setText(mQuexianList.get(mStyle_id).getStyleList().get(i).getName());
                setGradeAdapter(i);
                mGradeSp.setText("请选择");
                mNameSp.dismissPop();
            }
        });

        mGradeSp.setOnItemSelectedListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mGrade_id = i;
                mGradeSp.setText(mQuexianList.get(mStyle_id).getStyleList().get(mName_id).getGradeList().get(mGrade_id).getLevel() + "");
                if (mStyle_id != 0) {
                    String detailText = mQuexianList.get(mStyle_id).getStyleList().get(mName_id).getGradeList().get(mGrade_id).getContent();
                    mDetailEdt.setText(detailText);
                }
                mGradeSp.dismissPop();
            }
        });
    }



    private void setGradeAdapter(int i) {
        if (mStyle_id != 0) {
            int num = mQuexianList.get(mStyle_id).getStyleList().get(i).getGradeList().size();
            String[] gradeList = new String[num];
            for (int x = 0; x < gradeList.length; x++) {
                gradeList[x] = String.valueOf(mQuexianList.get(mStyle_id).getStyleList().get(i).getGradeList().get(x).getLevel());
            }
            mGradeAdapter.setStrings(gradeList);
        }
    }

    private void setNameAdapter(int i) {
        switch (i) {
            case 0:
                setEditText(false);
                break;
            case 1:
                setEditText(true);

                String[] jiegouList = new String[mQuexianList.get(1).getStyleList().size()];
                for (int x = 0; x < jiegouList.length; x++) {
                    jiegouList[x] = mQuexianList.get(1).getStyleList().get(x).getName();
                }

                mQuexianNameAdapter.setStrings(jiegouList);
                break;
            case 2:
                setEditText(true);
                String[] gongnengList = new String[mQuexianList.get(2).getStyleList().size()];
                for (int x = 0; x < gongnengList.length; x++) {
                    gongnengList[x] = mQuexianList.get(2).getStyleList().get(x).getName();
                }
                mQuexianNameAdapter.setStrings(gongnengList);
                break;
        }
    }

    private void setEditText(boolean b) {

        mNameSp.setEnabled(b);
        mGradeSp.setEnabled(b);
        mClockEdt.setEnabled(b);
        mLengthEdt.setEnabled(b);
        mDetailEdt.setEnabled(b);
        mNameSp.setText("");
        mGradeSp.setText("");
        if (!b) {
            mNameTv.setTextColor(context.getResources().getColor(R.color.gray_10));
            mClockTv.setTextColor(context.getResources().getColor(R.color.gray_10));
            mGradeTv.setTextColor(context.getResources().getColor(R.color.gray_10));
            mLengthTv.setTextColor(context.getResources().getColor(R.color.gray_10));
            mDetailTv.setTextColor(context.getResources().getColor(R.color.gray_10));
            mDetailEdt.setText("");
            mClockEdt.setText("");
            mLengthEdt.setText("");

        } else {
            mNameTv.setTextColor(context.getResources().getColor(R.color.colorText));
            mClockTv.setTextColor(context.getResources().getColor(R.color.colorText));
            mGradeTv.setTextColor(context.getResources().getColor(R.color.colorText));
            mLengthTv.setTextColor(context.getResources().getColor(R.color.colorText));
            mDetailTv.setTextColor(context.getResources().getColor(R.color.colorText));
        }
    }

    private void initData() {
        mDistanceEdt.setText(mPictureQueXianInfo.getDistance());

        for (int i = 0; i < mQuexianList.size(); i++) {
            if (mQuexianList.get(i).getName().equals(mPictureQueXianInfo.getStyle())) {
                mStyleSp.setText(mQuexianList.get(i).getName());
                mStyle_id = i;
            }
        }

        if (mStyle_id > 0) {

            for (int i = 0; i < mQuexianList.get(mStyle_id).getStyleList().size(); i++) {
                if (mQuexianList.get(mStyle_id).getStyleList().get(i).getName().equals(mPictureQueXianInfo.getName())) {
                    mNameSp.setText(mQuexianList.get(mStyle_id).getStyleList().get(i).getName());
                    mName_id = i;
                }
            }
            if (mName_id != -1)
                for (int i = 0; i < mQuexianList.get(mStyle_id).getStyleList().get(mName_id).getGradeList().size(); i++) {
                    if (!TextUtils.isEmpty(mPictureQueXianInfo.getGrade()) && mQuexianList.get(mStyle_id).getStyleList().get(mName_id).getGradeList().get(i).getLevel() == Integer.valueOf(mPictureQueXianInfo.getGrade())) {
                        mGradeSp.setText(String.valueOf(mQuexianList.get(mStyle_id).getStyleList().get(mName_id).getGradeList().get(i).getLevel()));
                        mGrade_id = i;
                    }
                }
            mClockEdt.setText(mPictureQueXianInfo.getClock());

            mLengthEdt.setText(mPictureQueXianInfo.getLength());
            mDetailEdt.setText(mPictureQueXianInfo.getDetail());
            if (mStyle_id > -1) {
                mStyleAdapter.setStrings(mArray_style);
                mStyleSp.setText(mArray_style[mStyle_id]);
                setNameAdapter(mStyle_id);
                mNameSp.setText("请选择");
            }
            if (mName_id > -1) {
                setNameAdapter(mName_id);
                mNameSp.setText(mQuexianList.get(mStyle_id).getStyleList().get(mName_id).getName());
                setGradeAdapter(mName_id);
                mGradeSp.setText("请选择");
            }
            if (mGrade_id > -1) {
                setGradeAdapter(mGrade_id);
                mGradeSp.setText(String.valueOf(mQuexianList.get(mStyle_id).getStyleList().get(mName_id).getGradeList().get(mGrade_id).getLevel()));
            }

        }

    }


    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        BaseApplication.toast("请选择缺陷类型！");
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                }
            }
        };
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


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.quexian_cancel:
                dismiss();
                break;
            case R.id.quexian_sure:
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        if(TextUtils.isEmpty(mStyleSp.getText().toString()) || mStyleSp.getText().equals("请选择")){
                            handler.sendEmptyMessage(0);
                            return;
                        }
                        PictureQueXianInfo pictureQueXianInfo = new PictureQueXianInfo(mDistanceEdt.getText().toString(), mStyleSp.getText().toString(),
                                mGradeSp.getText().toString(), mNameSp.getText().toString(),
                                mClockEdt.getText().toString(), mLengthEdt.getText().toString(),
                                mDetailEdt.getText().toString());
                        if (mStyle_id == -1) {
                            pictureQueXianInfo.setStyle("");
                        }
                        if (mName_id == -1) {
                            pictureQueXianInfo.setName("");
                        }
                        if (mGrade_id == -1) {
                            pictureQueXianInfo.setGrade("");
                        }

                        switch (mStyle_id) {
                            case 0:
                                pictureQueXianInfo.setGrade("");
                                pictureQueXianInfo.setName("");
                                break;
                            case 1:
                                if(mName_id< 0 )
                                    break;
                                pictureQueXianInfo.setName(mQuexianList.get(mStyle_id).getStyleList().get(mName_id).getName());
                                if(mGrade_id <0)
                                    break;
                                pictureQueXianInfo.setGrade(String.valueOf(mQuexianList.get(mStyle_id).getStyleList().get(mName_id).getGradeList().get(mGrade_id).getLevel()));

                                break;
                            case 2:
                                if(mName_id < 0)
                                    break;
                                pictureQueXianInfo.setName(mQuexianList.get(mStyle_id).getStyleList().get(mName_id).getName());
                                if(mGrade_id < 0 )
                                    break;
                                pictureQueXianInfo.setGrade(String.valueOf(mQuexianList.get(mStyle_id).getStyleList().get(mName_id).getGradeList().get(mGrade_id).getLevel()));

                                break;
                        }

                        try {
                            if (!isChangeInfo) {
                                dbUtils.save(pictureQueXianInfo);
                            } else {
                                dbUtils.update(pictureQueXianInfo, WhereBuilder.b("id", "=", mPictureQueXianInfo.getId()));
                            }

                            LogUtil.log("数据库：保存完成","");
                            if (listener != null)
                                listener.finish();
                            dismiss();
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.tv_quexianEdit_clockShow:
                new ClockShow_Dialog(context);
                break;
        }

    }


    public interface OnDataChangeListener {
        void finish();
    }

    private OnDataChangeListener listener;

    public void setOnDataChangeListener(OnDataChangeListener listener) {
        this.listener = listener;
    }
}