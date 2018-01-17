package com.bmw.peek2slite.model.impl;


import com.bmw.peek2slite.BaseApplication;
import com.bmw.peek2slite.Constant;
import com.bmw.peek2slite.model.UpdateInfo;
import com.bmw.peek2slite.model.model.UpdateMode;
import com.bmw.peek2slite.presenter.UpdateInfoListener;
import com.bmw.peek2slite.utils.LogUtil;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * Created by admin on 2016/9/21.
 */
public class UpdateInfoImpl implements UpdateMode {

    @Override
    public void getUpdateInfo(UpdateInfoListener listener) {
        getData(listener);
    }

    public void getData(final UpdateInfoListener listener) {


        BmobQuery query = new BmobQuery("UpdateInfo");
        query.order("-createdAt");
        query.findObjectsByTable(new QueryListener<JSONArray>() {
            @Override
            public void done(JSONArray ary, BmobException e) {
                if (e == null) {
                    if (ary != null && ary.length()>0) {
                        boolean isGetData = false;
                        for (int i = 0; i < ary.length(); i++) {
                            try {
                                JSONObject json = ary.getJSONObject(i);
                                if (json != null) {
                                    Gson g = new Gson();
                                    UpdateInfo u = g.fromJson(json.toString(), UpdateInfo.class);
                                    LogUtil.log("Version = "+ u.toString());
                                    if(Constant.IS_NEUTRAL_VERSION){
                                        if (u.getApkName().equals("Peek2SLite_neutral")) {
                                            isGetData = true;
                                            listener.setUpdateInfo(u);
                                            break;
                                        }
                                    }else {
                                        if (u.getApkName().equals("Peek2SLite")) {
                                            isGetData = true;
                                            listener.setUpdateInfo(u);
                                            break;
                                        }
                                    }
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                                LogUtil.error("update 1", e1.toString());
                            }

                        }
                        if(!isGetData){
                            listener.finish();
                        }
                    }else {
                        listener.finish();
                    }

                } else {
                    BaseApplication.toast("更新出错，请稍后重试！");
                    LogUtil.error("update 2", e.toString());
                    if (listener != null)
                        listener.UpdateFalse();
                }
            }
        });
    }
}
