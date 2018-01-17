package com.bmw.peek2slite.utils;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.util.Log;

import com.bmw.peek2slite.BaseApplication;
import com.bmw.peek2slite.model.FileInfo;
import com.bmw.peek2slite.model.Login_info;
import com.bumptech.glide.load.engine.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/4/27.
 */

public class FileUtil {


    public static void updateSystemLibFile(String path) {
        LogUtil.log("updateSystemLibFile path = " + path);
        MediaScannerConnection.scanFile(BaseApplication.context(), new String[]{path}, null, null);
    }

    public static String getFileSavePath() {
        List<String> list = getRealExtSDCardPath(BaseApplication.context());
        if (!Login_info.getInstance().isSaveToExSdcard()) {
            return list.get(0);
        } else {
            if (list.size() > 1)
                return list.get(1);
            Login_info.getInstance().setSaveToExSdcard(false);
            return list.get(0);
        }
    }


    /**
     * 路径是否存在，不能存在则创建
     */
    public static void pathIsExist() {
        File file = new File(getFileSavePath() + Login_info.local_video_path);
        if (!file.exists())
            file.mkdirs();

        File file1 = new File(getFileSavePath() + Login_info.local_picture_path);
        if (!file1.exists())
            file1.mkdirs();
    }


    public static List<String> getRealExtSDCardPath(Context context) {
        List<String> sdcardList = new ArrayList<>();
        String[] allSdcard = getExtSDCardPath(context);

        String inlaySDcard = getSDPath();
        sdcardList.add(inlaySDcard);

        if (allSdcard.length == 0 || allSdcard.length == 1)
            return sdcardList;

        for (String sdPath : allSdcard) {
            if (sdPath.contains(inlaySDcard))
                continue;
            if (canSdcardWrite(sdPath)) {
                sdcardList.add(sdPath);
            } else {
                File[] sdcardDataLogFiles = getSdcardDataLog(context);
                if (sdcardDataLogFiles.length <= 1)
                    continue;
                String sdcardDataLog = getSDcardDataLog(sdPath, sdcardDataLogFiles);
                if (sdcardDataLog != null)
                    sdcardList.add(sdcardDataLog);
            }

        }

        return sdcardList;
    }

    private static String getSDcardDataLog(String sdPath, File[] sdcardDataLogFiles) {
        for (File file : sdcardDataLogFiles) {
            if (file != null)
                if (file.toString().contains(sdPath) && canSdcardWrite(file.toString()))
                    return file.toString();
        }
        return null;
    }


    private static boolean canSdcardWrite(String sdPath) {
        File file = new File(sdPath + "/a.txt");
        try {
            file.createNewFile();
            file.delete();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String[] getExtSDCardPath(Context context) {
        StorageManager storageManager = (StorageManager) context.getSystemService(Context
                .STORAGE_SERVICE);
        try {
            Class<?>[] paramClasses = {};
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", paramClasses);
            getVolumePathsMethod.setAccessible(true);
            Object[] params = {};
            Object invoke = getVolumePathsMethod.invoke(storageManager, params);
            return (String[]) invoke;
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getSDPath() {

        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            return Environment.getExternalStorageDirectory().toString();
        } else
            return Environment.getDownloadCacheDirectory().toString();
    }

    private static File[] getSdcardDataLog(Context context) {
        File[] filearray = new File[0];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            filearray = context.getExternalFilesDirs(null);
        }
        return filearray;
    }

    public static List<Float> getDiskCapacity() {
        String path = getFileSavePath();
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        List<Float> diskSizeList = new ArrayList<>();
        StatFs stat = new StatFs(path);
        long blockSize = stat.getBlockSize();
        long totalBlockCount = stat.getBlockCount();
        long feeBlockCount = stat.getAvailableBlocks();
        float totleSize = (float) (blockSize * totalBlockCount) / (1024 * 1024 * 1024);
        float freeSize = (float) (blockSize * feeBlockCount) / (1024 * 1024 * 1024);
        float usedSize = totleSize - freeSize;
        totleSize = (float) (Math.round(totleSize * 100)) / 100;
        freeSize = (float) (Math.round(freeSize * 100)) / 100;
        usedSize = (float) (Math.round(usedSize * 100)) / 100;

        diskSizeList.add(totleSize);
        diskSizeList.add(freeSize);
        diskSizeList.add(usedSize);
        return diskSizeList;
    }


    /**
     * 存储卡获取 指定文件
     *
     * @param context
     * @param extension
     * @return
     */
    public static List<FileInfo> getSpecificTypeFiles(Context context, String[] extension) {
        List<FileInfo> fileInfoList = new ArrayList<FileInfo>();

        //内存卡文件的Uri
        Uri fileUri = MediaStore.Files.getContentUri("external");
        //筛选列，这里只筛选了：文件路径和含后缀的文件名
        String[] projection = new String[]{
                MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE
        };

        //构造筛选条件语句
        String selection = "";
        for (int i = 0; i < extension.length; i++) {
            if (i != 0) {
                selection = selection + " OR ";
            }
            selection = selection + MediaStore.Files.FileColumns.DATA + " LIKE '%" + extension[i] + "'";
        }
        //按时间降序条件（升序" ASC"）
        String sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC";

        Cursor cursor = context.getContentResolver().query(fileUri, projection, selection, null, sortOrder);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    String data = cursor.getString(0);
                    int logLayer = StringUtils.countMatches(data, '/');
                    if (logLayer <= 7) {
                        FileInfo fileInfo = new FileInfo();
                        fileInfo.setFilePath(data);

                        long size = 0;
                        try {
                            File file = new File(data);
                            size = file.length();
                            fileInfo.setSize(size);
                        } catch (Exception e) {

                        }
                        fileInfoList.add(fileInfo);
                    }
                } catch (Exception e) {
                    Log.i("FileUtils", "------>>>" + e.getMessage());
                }

            }
        }
        return fileInfoList;
    }


    /**
     * 转化完整信息的FileInfo
     *
     * @param context
     * @param fileInfoList
     * @param type
     * @return
     */
    public static List<FileInfo> getDetailFileInfos(Context context, List<FileInfo> fileInfoList, int type) {

        if (fileInfoList == null || fileInfoList.size() <= 0) {
            return fileInfoList;
        }

        for (FileInfo fileInfo : fileInfoList) {
            if (fileInfo != null) {
                fileInfo.setName(getFileName(fileInfo.getFilePath()));
                fileInfo.setSizeDesc(getFileSize(fileInfo.getSize()));

                fileInfo.setFileType(type);
            }
        }
        return fileInfoList;
    }

    public static String getFileName(String filePath) {
        if (filePath == null || filePath.equals("")) return "";
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }


    /**
     * 小数的格式化
     */
    public static final DecimalFormat FORMAT = new DecimalFormat("####.##");
    public static final DecimalFormat FORMAT_ONE = new DecimalFormat("####.#");

    public static String getFileSize(long size) {
        if (size < 0) { //小于0字节则返回0
            return "0B";
        }

        double value = 0f;
        if ((size / 1024) < 1) { //0 ` 1024 byte
            return size + "B";
        } else if ((size / (1024 * 1024)) < 1) {//0 ` 1024 kbyte

            value = size / 1024f;
            return FORMAT.format(value) + "KB";
        } else if (size / (1024 * 1024 * 1024) < 1) {                  //0 ` 1024 mbyte
            value = (size * 100 / (1024 * 1024)) / 100f;
            return FORMAT.format(value) + "MB";
        } else {                  //0 ` 1024 mbyte
            value = (size * 100l / (1024l * 1024l * 1024l)) / 100f;
            return FORMAT.format(value) + "GB";
        }
    }

    public static boolean copyFile(String sourPath, String desPath) {
        if (sourPath == null || desPath == null)
            return false;
        File sourFile = new File(sourPath);
        if (!sourFile.exists())
            return false;
        File desFile = new File(desPath);
        try {

            if (!desFile.exists())
                desFile.createNewFile();
            FileOutputStream out = new FileOutputStream(desFile);
            FileInputStream in = new FileInputStream(sourFile);
            byte[] buf = new byte[1024];
            int length = -1;
            while ((length = in.read(buf)) != -1) {
                out.write(buf, 0, length);
            }
            in.close();
            out.close();
            updateSystemLibFile(desPath);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean replaceImage(String sourPath, String desPath) {
        Bitmap bitmap = BitmapFactory.decodeFile(sourPath);
        try {
            BitmapUtils.save(desPath, bitmap);
            updateSystemLibFile(desPath);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            bitmap.recycle();
            bitmap = null;
        }
    }

}
