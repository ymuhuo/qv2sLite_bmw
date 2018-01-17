package com.bmw.peek2slite.presenter;


import com.bmw.peek2slite.model.UpdateInfo;

/**
 * Created by admin on 2016/9/21.
 */
public interface UpdateInfoListener {
    void setUpdateInfo(UpdateInfo updateInfo);
    void UpdateFalse();
    void finish();
}
