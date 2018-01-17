package com.bmw.peek2slite.presenter.impl;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.bmw.peek2slite.model.All_id_Info;
import com.bmw.peek2slite.model.Login_info;
import com.bmw.peek2slite.utils.LogUtil;
import com.bmw.peek2slite.view.view.PlaySurfaceView;
import com.bmw.peek2slite.presenter.PreviewPresenter;
import com.bmw.peek2slite.view.ui.PreviewActivity;
import com.bmw.peek2slite.view.viewImpl.PreviewImpl;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.RealPlayCallBack;

import org.MediaPlayer.PlayM4.Player;

/**
 * Created by admin on 2016/9/29.
 */
public class PreviewPresentImpl implements PreviewPresenter {

    private SurfaceView surfaceView;
    private Context context;
    private PreviewImpl preview;
    private static PlaySurfaceView[] playView = new PlaySurfaceView[16];
    private All_id_Info all_id_info;
    private Login_info loginInfo;
    private long currentTime;
    private int errorTime;
    private boolean isAddSufaceCallBack;


    public PreviewPresentImpl(Context context, PreviewImpl preview, SurfaceView surfaceView, OnPlayFailedListener listener) {
        this.context = context;
        loginInfo = Login_info.getInstance();
        this.surfaceView = surfaceView;
        this.preview = preview;
        all_id_info = All_id_Info.getInstance();
        this.listener = listener;

    }


    private OnPlayFailedListener listener;

    public interface OnPlayFailedListener {
        void playFailed();
    }

    @Override
    public void surfaceAddCallback() {
        surfaceView.getHolder().addCallback(new surfaceCallBackHolder());
    }

    @Override
    public void startSingle() {
        int m_iLogID = all_id_info.getM_iLogID();
        if (m_iLogID < 0) {
            return;
        }
        RealPlayCallBack fRealDataCallBack = getRealPlayerCbf();
        if (fRealDataCallBack == null) {
            LogUtil.error("海康：PreviewPresentImpl: fRealDataCallBack object is failed!");
            return;
        }
        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = all_id_info.getM_iStartChan();
        if (loginInfo.isVideo_zimaliu()) {
            previewInfo.dwStreamType = 1; // substream选择子码流；1
        } else {
            previewInfo.dwStreamType = 0;
        }
        previewInfo.bBlocked = 0;
//        previewInfo.byProtoType = 1;
//        previewInfo.dwLinkMode = 5;

        /*
        * lChannel
            通道号，目前设备模拟通道号从 1 开始，数字通道的起始通道号一般从 33 开始，具体取值在登录接口
            返回
        dwStreamType
            码流类型： 0-主码流， 1-子码流， 2-码流 3， 3-虚拟码流，以此类推
        dwLinkMode
            连接方式： 0- TCP 方式， 1- UDP 方式， 2- 多播方式， 3- RTP 方式， 4-RTP/RTSP， 5-RSTP/HTTP
        bBlocked
            0- 非阻塞取流， 1- 阻塞取流
        bPassbackRecord
            0-不启用录像回传， 1-启用录像回传。 ANR 断网补录功能，客户端和设备之间网络异常恢复之后自动将
            前端数据同步过来，需要设备支持。
        byPreviewMode
            预览模式： 0- 正常预览， 1- 延迟预览
        byProtoType
            应用层取流协议： 0- 私有协议， 1- RTSP 协议
        nRTSPPort
            RTSP 端口
        * */
//         NET_DVR_CLIENTINFO struClienInfo = new NET_DVR_CLIENTINFO();
//         struClienInfo.lChannel = m_iStartChan;
//         struClienInfo.lLinkMode = 0;
        // HCNetSDK start preview
        int m_iPlayID = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(m_iLogID,
                previewInfo, fRealDataCallBack);
//         m_iPlayID = HCNetSDK.getInstance().NET_DVR_RealPlay_V30(m_iLogID,
//         struClienInfo, fRealDataCallBack, false);
        if (m_iPlayID < 0) {
            LogUtil.error("海康：PreviewPresentImpl: NET_DVR_RealPlay is failed!Err:"
                    + HCNetSDK.getInstance().NET_DVR_GetLastError());
            listener.playFailed();
            return;
        }
        all_id_info.setM_iPlayID(m_iPlayID);
        LogUtil.log("海康：PreviewPresentImpl: NetSdk Play sucess"+m_iPlayID);

        if(!isAddSufaceCallBack) {
            isAddSufaceCallBack = true;
            surfaceAddCallback();
        }


    }

    @Override
    public void stopSingle() {
        int m_iPlayID = all_id_info.getM_iPlayID();
        if (m_iPlayID < 0) {
            LogUtil.error("海康：PreviewPresentImpl: stopSingle:m_iPlayID < 0");
            return;
        }

        // net sdk stop preview
        if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay(m_iPlayID)) {
            LogUtil.error("海康：PreviewPresentImpl: StopRealPlay is failed!Err:"
                    + HCNetSDK.getInstance().NET_DVR_GetLastError());
//            return;
        }

        all_id_info.setM_iPlayID(-1);
        stopSinglePlayer();

        LogUtil.log("海康：退出预览成功！");
    }

    @Override
    public void startMulti() {
        int m_iLogID = all_id_info.getM_iLogID();
        if (m_iLogID < 0) {
            LogUtil.error("海康：PreviewPresentImpl: please login on device first");
            return;
        }
        int multi_chan_num = all_id_info.getMulti_chan_num();
        DisplayMetrics metric = new DisplayMetrics();
        ((PreviewActivity) context).getWindowManager().getDefaultDisplay().getMetrics(metric);
        int i = 0;
        for (i = 0; i < multi_chan_num; i++) {
            if (playView[i] == null) {
                playView[i] = new PlaySurfaceView(context);
                playView[i].setParam(metric.widthPixels);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
                params.bottomMargin = playView[i].getCurHeight() - (i / 2)
                        * playView[i].getCurHeight();
                params.leftMargin = (i % 2) * playView[i].getCurWidth();
                params.gravity = Gravity.BOTTOM | Gravity.LEFT;
                ((Activity) context).addContentView(playView[i], params);
            }
            playView[i].startPreview(m_iLogID, all_id_info.getM_iStartChan() + i);
        }
        all_id_info.setM_iPlayID(playView[0].m_iPreviewHandle);
    }

    @Override
    public void stopMulti() {
        int i = 0;
        for (i = 0; i < all_id_info.getMulti_chan_num(); i++) {
            playView[i].stopPreview();
        }
        all_id_info.setM_iPlayID(-1);
    }


    @Override
    public void release() {

    }


    private RealPlayCallBack getRealPlayerCbf() {
        RealPlayCallBack cbf = new RealPlayCallBack() {
            public void fRealDataCallBack(int iRealHandle, int iDataType,
                                          byte[] pDataBuffer, int iDataSize) {
                long time = System.currentTimeMillis() - currentTime;
                if (time > 500)
                    LogUtil.log("海康时间：" + time);
                currentTime = System.currentTimeMillis();

                // player channel 1
//                if(time<500)
                processRealData(1, iDataType, pDataBuffer,
                        iDataSize, Player.STREAM_REALTIME);


//                StringBuilder builder = new StringBuilder();
               /*for(int i=0;i<200;i++){
                   builder.append(" ").append(Integer.toHexString(pDataBuffer[i]&0xff)).append(" ");
                }
                LogUtil.log("海康数据：",builder.toString());*/
            }
        };
        return cbf;
    }


    public void processRealData(int iPlayViewNo, int iDataType,
                                byte[] pDataBuffer, int iDataSize, int iStreamMode) {

        int m_iPort = all_id_info.getM_iPort();

        if (HCNetSDK.NET_DVR_SYSHEAD == iDataType) {
            if (m_iPort >= 0) {
                return;
            }
            m_iPort = Player.getInstance().getPort();
            if (m_iPort == -1) {
                LogUtil.error("海康：PreviewPresentImpl: getPort is failed with: "
                        + Player.getInstance().getLastError(m_iPort));
                return;
            }
            LogUtil.log("海康：PreviewPresentImpl: getPort succ with: " + m_iPort);
            all_id_info.setM_iPort(m_iPort);
            if (iDataSize > 0) {
                if (!Player.getInstance().setStreamOpenMode(m_iPort,
                        iStreamMode)) // set stream mode
                {
                    LogUtil.log("海康：PreviewPresentImpl: setStreamOpenMode failed");
                    return;
                }
                /*if (!Player.getInstance().openStream(m_iPort, pDataBuffer,
                        iDataSize, 600 * 1024)) // startPlayVideo stream
                {
                    preview.ierror("PreviewPresentImpl: openStream failed");
                    return;
                }*/

                if (!Player.getInstance().openStream(m_iPort, pDataBuffer,
                        iDataSize, 720 * 1280)) // startPlayVideo stream
                {
                    LogUtil.error("海康：PreviewPresentImpl: openStream failed");
                    return;
                }


                if(!Player.getInstance().setDisplayBuf(m_iPort,1)){
                    LogUtil.error("海康：设置播放缓冲区最大缓冲帧数！" + Player.getInstance().getLastError(m_iPort));
                }

                LogUtil.log("海康：缓冲区剩余数据：" + Player.getInstance().getSourceBufferRemain(m_iPort));
                if (Player.getInstance().resetSourceBuffer(m_iPort)) {
                    LogUtil.log("海康：清空缓冲区所有剩余数据！");
                }
                if (Login_info.getInstance().isYingJieMa()) {
                    if (Player.getInstance().setMaxHardDecodePort(1)) {
                        LogUtil.log("设置最大硬解码路数为16！");

                    }
                    if (Player.getInstance().setHardDecode(m_iPort, 1)) {
                        LogUtil.log("启用硬解码优先！");
                    }
                }


//                if (Player.getInstance().setDisplayBuf(m_iPort, 30)) {
//
//                    preview.ilog("设置播放缓冲区最大缓冲帧数20帧！");
//                }
                if (!Player.getInstance().play(m_iPort,
                        surfaceView.getHolder())) {
                    LogUtil.error("海康：PreviewPresentImpl: play failed");
                    if (listener != null) {
                        listener.playFailed();
                    }
                    return;
                }
                if (!Player.getInstance().playSound(m_iPort)) {
                    LogUtil.error("海康：PreviewPresentImpl: playSound failed with ierror code:"
                            + Player.getInstance().getLastError(m_iPort));
                    return;
                }
            }
        } else {
            if(m_iPort == -1)
                return;
            if (!Player.getInstance().inputData(m_iPort, pDataBuffer,
                    iDataSize)) {
                // Log.e(TAG, "inputData failed with: " +
                // Player.getInstance().getLastError(m_iPort));


                for (int i = 0; i < 4000; i++) {

                    if (Player.getInstance().resetSourceBuffer(m_iPort)) {
                        LogUtil.log("海康：清空缓冲区所有剩余数据！");
                    }

                    if (Player.getInstance().inputData(m_iPort,
                            pDataBuffer, iDataSize)) {
                        break;
                    }

                    if (i % 100 == 0) {
                        LogUtil.error("海康：PreviewPresentImpl: inputData failed with: "
                                + Player.getInstance()
                                .getLastError(m_iPort) + ", i:" + i);
                      /*  errorTime++;
                        if(errorTime>10 && listener != null) {
                            listener.playFailed();
                            errorTime = 0;
                            break;
                        }*/
                    }

                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();

                    }
                }
            }


        }

    }

    private void stopSinglePlayer() {
        Player.getInstance().stopSound();
        int m_iPort = all_id_info.getM_iPort();
        // player stop play
        if (!Player.getInstance().stop(m_iPort)) {
            LogUtil.error("海康：PreviewPresentImpl: stopSinglePlayer is failed!");
//            return;
        }

        if (!Player.getInstance().setHardDecode(m_iPort, 0)) {
            LogUtil.error("海康：PreviewPresentImpl: stopHardDecode is failed!");
//            return;
        }

        if (!Player.getInstance().closeStream(m_iPort)) {
            LogUtil.error("海康：PreviewPresentImpl: stopSinglePlayer closeStream is failed!");
//            return;
        }
        if (!Player.getInstance().freePort(m_iPort)) {
            LogUtil.error("海康：PreviewPresentImpl: stopSinglePlayer freePort is failed!" + m_iPort);
//            return;
        }

        all_id_info.setM_iPort(-1);
    }

    private class surfaceCallBackHolder implements SurfaceHolder.Callback{

        // @Override
        public void surfaceCreated(SurfaceHolder holder) {
            surfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
            int m_iPort = all_id_info.getM_iPort();
            LogUtil.log("海康：PreviewPresentImpl: surface is created" + m_iPort);
            if (-1 == m_iPort) {
                return;
            }
            Surface surface = holder.getSurface();
            if (true == surface.isValid()) {
                if (false == Player.getInstance()
                        .setVideoWindow(m_iPort, 0, holder)) {
                    LogUtil.error("海康：PreviewPresentImpl: Player setVideoWindow failed!"+Player.getInstance().getLastError(m_iPort));
                }
            }
        }

        // @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
        }

        // @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

            int m_iPort = all_id_info.getM_iPort();
            LogUtil.log("海康：PreviewPresentImpl: Player setVideoWindow release!" + m_iPort);
            if (-1 == m_iPort) {
                return;
            }
            if (true == holder.getSurface().isValid()) {
                if (false == Player.getInstance().setVideoWindow(m_iPort, 0, null)) {
                    LogUtil.error("海康：PreviewPresentImpl: Player setVideoWindow failed!");
                }
            }
        }
    }
}

