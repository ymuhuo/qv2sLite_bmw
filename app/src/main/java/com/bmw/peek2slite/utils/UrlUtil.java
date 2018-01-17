package com.bmw.peek2slite.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/9/30.
 */
public class UrlUtil {


    public static String getFileName() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyyMMddHHmmss");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }
/*
    public static String getSDPath() {
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            return Environment.getExternalStorageDirectory().toString();
        } else
            return Environment.getDownloadCacheDirectory().toString();
    }*/

    //得到文件夹下所有文件列表
    public static List<File> getFileUtils(String path) {
        File file = new File(path);
        if (!file.exists() || file.isFile())
            return null;
        File[] files = file.listFiles();
        if (files.length > 0) {
            List<File> list = new ArrayList<File>();
            for (File f : files) {
                if (f.isFile())
                    list.add(f);
            }
            file = null;
            files = null;
            return list;
        } else {
            return null;
        }
    }

    public static String getTimeYMd() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy年MM月dd日");
        String date = sDateFormat.format(new java.util.Date());
        return date;

    }
}
