package com.bmw.peek2slite.view.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bmw.peek2slite.R;
import com.bmw.peek2slite.utils.LogUtil;
import com.bmw.peek2slite.view.adapter.SpinnerAdapter;

/**
 * Created by admin on 2017/3/6.
 */

public class MySpinner extends TextView {
    private Context mContext;
    private SpinnerAdapter adapter;
    private ListView popContentView;
    private AdapterView.OnItemClickListener onItemClickListener;
    private PopupWindow mDropView;
    private int mWidth;

    public MySpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MySpinner);
        mWidth = typedArray.getDimensionPixelSize(R.styleable.MySpinner_width_view, 0);
        typedArray.recycle();

    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        LinearLayout container = (LinearLayout) inflater.inflate(R.layout.spinner_content, null);
        popContentView = (ListView) container.findViewById(R.id.spinner_content);
        if (mWidth != 0) {
            mDropView = new PopupWindow(container, mWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
            LogUtil.log("自定义view: " , mWidth);
        } else
            mDropView = new PopupWindow(container, 500, LinearLayout.LayoutParams.WRAP_CONTENT);
        mDropView.setBackgroundDrawable(new BitmapDrawable());
        mDropView.setFocusable(true);
        mDropView.setOutsideTouchable(true);
        mDropView.setOutsideTouchable(true);
        mDropView.setTouchable(true);
        container.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismissPop();
            }
        });
        this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mDropView.isShowing()) {
                    dismissPop();
                } else {
                    showPop();
                }
            }
        });
        mDropView.update();
    }

    public void setHint(String hint) {
        this.setText(hint);
    }

    public void setAdapter(SpinnerAdapter adapter) {
        if (adapter != null) {
            this.adapter = adapter;
            popContentView.setAdapter(this.adapter);
        }

    }

    public void setOnItemSelectedListener(AdapterView.OnItemClickListener listener) {
        if (listener != null) {
            this.onItemClickListener = listener;
            popContentView.setOnItemClickListener(listener);
        }

    }

    public void dismissPop() {
        if (mDropView.isShowing()) {
            mDropView.dismiss();
        }
    }

    public void showPop() {
        mDropView.showAsDropDown(this);
    }
}
