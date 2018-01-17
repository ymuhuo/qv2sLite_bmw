package com.bmw.peek2slite.view.viewImpl;


/**
 * Created by admin on 2016/8/29.
 */
public interface PreviewImpl {
    void iToast(String msg);
    void setBattery(int pic);
    void ierror(String msg);
    void ilog(String msg);
    void record(int which,boolean isOk);
    void setRecordBtn(int i);
    void capture(String path);
    void stop();

}
