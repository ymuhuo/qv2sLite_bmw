package com.bmw.peek2slite.presenter.impl;

import android.content.Context;
import android.content.SharedPreferences;

import com.bmw.peek2slite.model.impl.EnvironmentImpl2;
import com.bmw.peek2slite.view.adapter.EnvironmentAdapter;
import com.bmw.peek2slite.model.Environment;
import com.bmw.peek2slite.model.model.EnvironmentMode;
import com.bmw.peek2slite.presenter.EnvironmentListener;
import com.bmw.peek2slite.presenter.EnvironmentPresenter;
import com.bmw.peek2slite.view.viewImpl.EnvironmentView;

import java.util.List;

/**
 * Created by admin on 2016/9/19.
 */
public class EnvironmentPresentImpl implements EnvironmentPresenter,EnvironmentListener {

    private EnvironmentMode environmentMode;
    private EnvironmentAdapter adapter;
    private EnvironmentView view;
    private Context context;

    public EnvironmentPresentImpl(EnvironmentAdapter adapter, EnvironmentView view, Context context) {
        environmentMode = new EnvironmentImpl2(context);
        this.adapter = adapter;
        this.view = view;
        this.context = context;
        initSharePrefrences();
    }

    private void initSharePrefrences() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(EnvironmentImpl2.SHAREPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.remove(EnvironmentImpl.QIYA_MIN);
//        editor.remove(EnvironmentImpl.QIYA_MAX);
//        editor.commit();
        float qiya_min = sharedPreferences.getFloat(EnvironmentImpl2.QIYA_MIN, -1.0f);
        float qiya_max = sharedPreferences.getFloat(EnvironmentImpl2.QIYA_MAX, -1.0f);
        if(qiya_min == -1.0f){
            editor.putFloat(EnvironmentImpl2.QIYA_MIN,0f);
        }
        if(qiya_max == -1.0f){
            editor.putFloat(EnvironmentImpl2.QIYA_MAX, 16.48f);
        }
        editor.commit();
    }

    //获取数据成功回调此方法
    @Override
    public void success(List<Environment> list) {
        adapter.setList(list);
    }
    //获取数据失败回调此方法
    @Override
    public void failure() {
        view.showError("获取数据失败");
    }

    //获取数据
    @Override
    public void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                environmentMode.getDatas(EnvironmentPresentImpl.this);
            }
        }).start();
    }

    @Override
    public void realese() {
        environmentMode.realese();
    }
}
