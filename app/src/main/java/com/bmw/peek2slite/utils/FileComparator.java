package com.bmw.peek2slite.utils;

import java.io.File;
import java.util.Comparator;

/**
 * Created by admin on 2017/4/14.
 */

public class FileComparator implements Comparator<File> {

    @Override
    public int compare(File file, File t1) {

        if(file.lastModified() > t1.lastModified())
        {
            return -1;
        }else
        {
            return 1;
        }

       /* String fileName1 = file.getName();
        String fileName2 = t1.getName();
        fileName1 = fileName1.substring(0,fileName1.lastIndexOf("."));
        fileName2 = fileName2.substring(0,fileName2.lastIndexOf("."));

        try {
            int flag = Long.valueOf(fileName2).compareTo(Long.valueOf(fileName1));
            return flag;
        }catch (NumberFormatException e){
            LogUtil.error("文件排序：error: "+ e);
        }


        return fileName2.compareTo(fileName1);*/
    }
}
