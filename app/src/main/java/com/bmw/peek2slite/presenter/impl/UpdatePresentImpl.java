package com.bmw.peek2slite.presenter.impl;

import android.content.Context;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.bmw.peek2slite.BaseApplication;
import com.bmw.peek2slite.model.UpdateInfo;
import com.bmw.peek2slite.model.impl.UpdateInfoImpl;
import com.bmw.peek2slite.model.model.UpdateMode;
import com.bmw.peek2slite.presenter.UpdateInfoListener;
import com.bmw.peek2slite.presenter.UpdatePresenter;
import com.bmw.peek2slite.utils.FileUtil;
import com.bmw.peek2slite.utils.LogUtil;
import com.bmw.peek2slite.view.ui.SettingActivity;
import com.bmw.peek2slite.view.viewImpl.UpdateImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by admin on 2016/9/21.
 */
public class UpdatePresentImpl implements UpdatePresenter,UpdateInfoListener {

    private UpdateMode updateMode;
    private String version;
    private UpdateImpl view;
    private Context context;
//    private UpdateProgressDialog proDialog;
    private String url,apkName;

    public UpdatePresentImpl(UpdateImpl view, Context context, String version) {
        this.updateMode = new UpdateInfoImpl();
        this.view = view;
        this.context = context;
        this.version = version;

        LogUtil.log("正在检查更新！");
        update();
    }



    public void update() {
        updateMode.getUpdateInfo(this);
    }

    private void toast(String str){
        BaseApplication.toast(str);
    }

    @Override
    public void realese() {
    }

    @Override
    public void setUpdateInfo(UpdateInfo updateInfo) {
        view.getUpdateInfo(updateInfo);
        if(updateInfo!= null) {
            if (isNeedUpdate(updateInfo)) {
//                showUpdateDialog(updateInfo.getVersion(), updateInfo.getDescription());
                url = updateInfo.getUrl();
                apkName = updateInfo.getApkName();
            } else {
                LogUtil.log("已经是最新版本！");
//                wifiPresenter.startPlayVideo();
//                wifiPresenter.connect();
            }
        }
    }

    @Override
    public void UpdateFalse() {
        view.connectNetFalse();
    }

    @Override
    public void finish() {
        view.noData();
    }

    private void showDialogFragment(DialogFragment dialogFragment, String tag){
        FragmentTransaction fragmentTransaction = ((SettingActivity)context).getSupportFragmentManager().beginTransaction();
        Fragment fragment = ((SettingActivity)context).getSupportFragmentManager().findFragmentByTag(tag);
        if(fragment != null)
            fragmentTransaction.remove(fragment);

        dialogFragment.show(fragmentTransaction,tag);
    }

    public void downloadFile(){
//        DownLoadFile downLoadFile = new DownLoadFile("temp.jpg","","http://bmob-cdn-7943.b0.upaiyun.com/2016/12/06/5c0d6402c113430dbace112f12ea3b2b.jpg");
        BmobFile bomfile = new BmobFile(apkName,"",url);
        downloadFile(bomfile);
    }

    private void downloadFile(BmobFile file){
        //允许设置下载文件的存储路径，默认下载文件的目录为：context.getApplicationContext().getCacheDir()+"/bmob/"
        File saveFile = new File(Environment.getExternalStorageDirectory(), file.getFilename());
        file.download(saveFile, new DownloadFileListener() {

            @Override
            public void onStart() {
//                toast("开始下载...");
            }

            @Override
            public void done(String savePath,BmobException e) {
                if(e==null){
//                    toast("下载成功,保存路径:"+savePath);
                }else{
//                    toast("下载失败："+e.getErrorCode()+","+e.getMessage());
                    LogUtil.log("bmob", "done: 下载失败！");
                }
            }

            @Override
            public void onProgress(Integer value, long newworkSpeed) {
                LogUtil.log("bmob","下载进度："+value+","+newworkSpeed);
            }

        });
    }


    public void downloadDate() {
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {


            @Override
        public void onResponse(Call call, Response response) throws IOException {

            InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                String SDPath = FileUtil.getFileSavePath();
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    LogUtil.log("h_bl", "total=" +" "+total);
                    File file = new File(SDPath,
                            apkName);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        Log.d("h_bl", "progress=" + progress);
                        view.showUpdateProgress(progress);
                    }
                    fos.flush();
                    view.downloadSuccess(apkName);
                    LogUtil.log("h_bl", "文件下载成功");
                } catch (Exception e) {
                    LogUtil.log("h_bl", "文件下载失败");
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {

                LogUtil.log("h_bl", "onFailure");
            }
        });
    }




    private boolean isNeedUpdate(UpdateInfo info) {
        String v = info.getVersion(); // 最新版本的版本号
        if (v.equals(version)) {
            return false;
        } else {
            return true;
        }
    }
}
