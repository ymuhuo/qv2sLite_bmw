package com.bmw.peek2slite.utils;

import android.content.Context;

import com.lidroid.xutils.DbUtils;

/**
 * Created by yMuhuo on 2016/11/30.
 */
public class DbHelper {
    private static DbUtils dbUtils;


    public static void init(Context context){

        DbUtils.DaoConfig config = new DbUtils.DaoConfig(context);
        config.setDbName("BMW_Peek_Task_Data");
        config.setDbVersion(1);
        dbUtils = DbUtils.create(config);
    }

    public static DbUtils getDbUtils() {
        return dbUtils;
    }
}
