package com.bmw.peek2slite.presenter.impl;

import com.bmw.peek2slite.jna.HCNetSDKJNAInstance;
import com.bmw.peek2slite.model.All_id_Info;
import com.bmw.peek2slite.model.Login_info;
import com.bmw.peek2slite.presenter.VideoCutPresenter;
import com.bmw.peek2slite.utils.FileUtil;
import com.bmw.peek2slite.utils.LogUtil;
import com.bmw.peek2slite.utils.UrlUtil;
import com.bmw.peek2slite.view.viewImpl.PreviewImpl;
import com.hikvision.netsdk.HCNetSDK;

import org.MediaPlayer.PlayM4.Player;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by admin on 2016/9/30.
 */
public class VideoCutPresentImpl implements VideoCutPresenter {

    private PreviewImpl preview;
    private boolean isRecord;
    private All_id_Info all_id_info;
    private String mRedordPath;

    public VideoCutPresentImpl(PreviewImpl preview) {
        this.preview = preview;
        all_id_info = All_id_Info.getInstance();
        pathIsExist();
    }

    @Override
    public void record() {
        int m_iPlayID = all_id_info.getM_iPlayID();
        if (!isRecord) {
            mRedordPath = FileUtil.getFileSavePath()  + Login_info.local_video_path + UrlUtil.getFileName() + ".mp4";
            LogUtil.log("自定义接口录像！");
            int clibrary = HCNetSDKJNAInstance.getInstance().NET_DVR_SaveRealData_V30(m_iPlayID, 2, mRedordPath);
            if(clibrary <=0) {

                LogUtil.error("海康：抓拍：开始录制失败：", HCNetSDK.getInstance().NET_DVR_GetLastError());
                if (preview != null) {
                    preview.record(0, false);
                }
                return;
            }else{
                LogUtil.error("自定义接口录像！" + clibrary);
                LogUtil.log("海康：抓拍：开始录制成功！", "");
                if (preview != null) {
                    LogUtil.log("NET_DVR_SaveRealData succ!");
                    preview.record(0, true);
                }
            }
            isRecord = true;
        } else {
            if (!HCNetSDK.getInstance().NET_DVR_StopSaveRealData(m_iPlayID)) {
                LogUtil.error("抓拍：结束录制失败！", HCNetSDK.getInstance().NET_DVR_GetLastError());
                if (preview != null) {

                    preview.record(1, false);
                }
            } else {
                LogUtil.log("抓拍：结束录制成功！", "");
                if (preview != null) {
                    preview.ilog("NET_DVR_StopSaveRealData succ!");
                    preview.record(1, true);
                }
            }
            FileUtil.updateSystemLibFile(mRedordPath);
            isRecord = false;
        }
    }

    @Override
    public void capture() {

        int m_iPort = all_id_info.getM_iPort();
        if (m_iPort < 0) {
            LogUtil.error("抓拍：截图失败，未登录！", "");
            preview.iToast("截图失败！");
            return;
        }
        Player.MPInteger stWidth = new Player.MPInteger();
        Player.MPInteger stHeight = new Player.MPInteger();
        if (!Player.getInstance().getPictureSize(m_iPort, stWidth,
                stHeight)) {
            LogUtil.error("抓拍：截图失败，getPictureSize failed with error code:"
                    , Player.getInstance().getLastError(m_iPort));
            return;
        }
        int nSize = 5 * stWidth.value * stHeight.value;
        final byte[] picBuf = new byte[nSize];
        final Player.MPInteger stSize = new Player.MPInteger();
        if (!Player.getInstance()
                .getJPEG(m_iPort, picBuf, nSize, stSize)) {
            LogUtil.error("抓拍：截图失败，未登录！getBMP failed with error code:"
                    , Player.getInstance().getLastError(m_iPort));
            return;
        }

        final String path = FileUtil.getFileSavePath() + Login_info.local_picture_path
                + UrlUtil.getFileName() + ".jpg";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileOutputStream file = new FileOutputStream(path);
                    file.write(picBuf, 0, stSize.value);
                    file.close();
                    FileUtil.updateSystemLibFile(path);
                    preview.capture(path);
//                    preview.iToast("截图已保存");
                } catch (Exception err) {
                    LogUtil.error("海康：抓拍：截图失败:", err.toString());
                }
            }
        }).start();
    }

    @Override
    public void release() {
        if (isRecord) {
            LogUtil.log("抓拍：视图退出，停止录像", "");
            preview = null;
            record();
        }
    }

    /**
     * 路径是否存在，不能存在则创建
     */
    private void pathIsExist() {
        File file = new File(FileUtil.getFileSavePath() + Login_info.local_video_path);
        if (!file.exists())
            file.mkdirs();

        File file1 = new File(FileUtil.getFileSavePath() + Login_info.local_picture_path);
        if (!file1.exists())
            file1.mkdirs();
    }


}
