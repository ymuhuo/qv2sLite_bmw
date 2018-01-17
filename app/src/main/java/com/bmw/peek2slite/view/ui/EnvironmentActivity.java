package com.bmw.peek2slite.view.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;

import com.bmw.peek2slite.R;
import com.bmw.peek2slite.view.adapter.EnvironmentAdapter;
import com.bmw.peek2slite.presenter.EnvironmentPresenter;
import com.bmw.peek2slite.presenter.impl.EnvironmentPresentImpl;
import com.bmw.peek2slite.view.viewImpl.EnvironmentView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EnvironmentActivity extends BaseActivity implements EnvironmentView {

    @Bind(R.id.environment_recyclerview)
    RecyclerView eRecyclerview;
    @Bind(R.id.environment_swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    private EnvironmentAdapter adapter;
    private EnvironmentPresenter ePresenter;
    private boolean isStop;
    private ScheduledExecutorService scheduledThreadPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_environment);
        ButterKnife.bind(this);
        scheduledThreadPool = Executors.newScheduledThreadPool(3);
        init();
        initdata();
        Runnable environRunnable = new Runnable() {
            @Override
            public void run() {
                ePresenter.getData();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        scheduledThreadPool.scheduleAtFixedRate(environRunnable, 0,1, TimeUnit.SECONDS);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (!isStop){
//                    ePresenter.getData();
//                    try {
//                        Thread.sleep(3000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();

    }

    @Override
    protected void onResume() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        ePresenter.getData();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        super.onResume();
    }

    public void init() {
        Log.d(TAG, "init: getdatainit");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        eRecyclerview.setLayoutManager(layoutManager);
        adapter = new EnvironmentAdapter(this);
        eRecyclerview.setAdapter(adapter);

        swipeRefresh.setProgressBackgroundColorSchemeResource(android.R.color.white);
        //设置进度圈颜色
        swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        //设置进度条位置
        swipeRefresh.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));

    }

    public void initdata() {
        ePresenter = new EnvironmentPresentImpl(adapter, this, this);
        ePresenter.getData();
        adapter.setAdapterDateChangeListener(new EnvironmentAdapter.AdapterDateChangeListener() {
            @Override
            public void resetDate() {
                ePresenter.getData();
            }
        });
    }

    @Override
    public void showError(String str) {

    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "environment onDestroy: ");
        isStop = true;
        scheduledThreadPool.shutdownNow();
        ePresenter.realese();
        ButterKnife.unbind(this);
        super.onDestroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            Log.d("wait", "onKeyDown: ");
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
