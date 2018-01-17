package com.bmw.peek2slite.presenter;

/**
 * Created by admin on 2016/9/28.
 */
public interface PreviewPresenter {
    void startSingle();
    void stopSingle();
    void startMulti();
    void stopMulti();
    void surfaceAddCallback();
    void release();
}
