package com.bmw.peek2slite.view.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.bmw.peek2slite.BaseApplication;
import com.bmw.peek2slite.R;
import com.bmw.peek2slite.model.FileInfo;
import com.bmw.peek2slite.utils.FileUtil;
import com.bmw.peek2slite.view.adapter.FileInfoAdapter;
import com.bmw.peek2slite.view.dialog.DialogManufacturerFragment;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ManufacturerLogoActivity extends BaseActivity {

    @Bind(R.id.gv)
    GridView gv;
    @Bind(R.id.pb_img)
    ProgressBar pbImg;
    private FileInfoAdapter mFileInfoAdapter;
    private List<FileInfo> mFileInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manufacturer_logo);
        ButterKnife.bind(this);
        gv.setColumnWidth(6);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                toast("pic path = " + mFileInfoList.get(position).getFilePath());
                Intent intent = new Intent();
                intent.putExtra(DialogManufacturerFragment.IMG_PATH_FLAG,mFileInfoList.get(position).getFilePath());
                setResult(DialogManufacturerFragment.RESULT_CODE,intent);
                finish();
            }
        });

        new GetFileInfoListTask(getApplicationContext()).executeOnExecutor(BaseApplication.MAIN_EXECUTOR);
    }


    /**
     * 获取ApkInfo列表任务
     */
    class GetFileInfoListTask extends AsyncTask<String, Integer, List<FileInfo>> {
        Context sContext = null;
        List<FileInfo> sFileInfoList = null;

        public GetFileInfoListTask(Context sContext) {
            this.sContext = sContext;
//            this.sType = FileInfo.TYPE_JPG;
        }

        @Override
        protected void onPreExecute() {
            showProgressBar();
            super.onPreExecute();
        }

        @Override
        protected List doInBackground(String... params) {
            //FileUtils.getSpecificTypeFiles 只获取FileInfo的属性 filePath与size
//            if(sType == FileInfo.TYPE_JPG){
            sFileInfoList = FileUtil.getSpecificTypeFiles(sContext, new String[]{FileInfo.EXTEND_JPG, FileInfo.EXTEND_JPEG, FileInfo.EXTEND_PNG});
            sFileInfoList = FileUtil.getDetailFileInfos(sContext, sFileInfoList, FileInfo.TYPE_JPG);
//            }

            mFileInfoList = sFileInfoList;

            return sFileInfoList;
        }


        @Override
        protected void onPostExecute(List<FileInfo> list) {
            hideProgressBar();
            if (sFileInfoList != null && sFileInfoList.size() > 0) {
//                if(mType == FileInfo.TYPE_JPG){ //图片
                mFileInfoAdapter = new FileInfoAdapter(sContext, sFileInfoList, FileInfo.TYPE_JPG);
                gv.setAdapter(mFileInfoAdapter);
//                }
            } else {
//                ToastUtils.show(sContext, sContext.getResources().getString(R.string.tip_has_no_apk_info));
            }
        }
    }

    private void hideProgressBar() {
        if (pbImg != null)
            pbImg.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        if (pbImg != null)
            pbImg.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复

            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
