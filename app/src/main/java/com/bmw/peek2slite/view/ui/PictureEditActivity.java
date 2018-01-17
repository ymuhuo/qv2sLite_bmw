package com.bmw.peek2slite.view.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bmw.peek2slite.BaseApplication;
import com.bmw.peek2slite.R;
import com.bmw.peek2slite.model.Login_info;
import com.bmw.peek2slite.model.PictureQueXianInfo;
import com.bmw.peek2slite.utils.DbHelper;
import com.bmw.peek2slite.utils.FileUtil;
import com.bmw.peek2slite.utils.LogUtil;
import com.bmw.peek2slite.utils.PullXmlParseUtil;
import com.bmw.peek2slite.view.adapter.PictureEditAdapter;
import com.bmw.peek2slite.view.dialog.Quexian_Edit_Dialog;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2017/3/2.
 */

public class PictureEditActivity extends BaseActivity {

    @Bind(R.id.capture_image)
    ImageView captureImage;
    @Bind(R.id.capture_recyclerview)
    RecyclerView recyListView;
    @Bind(R.id.pipe_id)
    EditText mPipeIdEdt;
    private Bitmap bitmap;
    private PictureEditAdapter adapter;
    private int position = -1;
    private Handler handler;
    private DbUtils dbUtils;
    private List<PictureQueXianInfo> list;

    private static final String GuanDaoHao = "GuanDaoHao";
    private static final String Distance = "Distance";
    private static final String DefectType = "DefectType";
    private static final String DefectCode = "DefectCode";
    private static final String DefectLevel = "DefectLevel";
    private static final String ClockExpression = "ClockExpression";
    private static final String DefectLength = "DefectLength";
    private static final String DefectDescription = "DefectDescription";

    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_edit);
        ButterKnife.bind(this);

        initHandler();
        dbUtils = DbHelper.getDbUtils();

        String path = getIntent().getStringExtra("path");
        fileName = path.substring(path.lastIndexOf("/")+1,path.lastIndexOf("."));
        LogUtil.log("文件名：",fileName);

        if (!TextUtils.isEmpty(path)) {
            bitmap = BitmapFactory.decodeFile(path);
            captureImage.setImageBitmap(bitmap);
        }

        recyListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PictureEditAdapter(this);
        recyListView.setAdapter(adapter);
        adapter.setOnDataChooseListener(new PictureEditAdapter.OnDataChooseListener() {
            @Override
            public void setData(int id) {
                position = id;
                handler.sendEmptyMessage(0);
                LogUtil.log("数据回调：",position);
            }
        });

        initData(1);


    }

    private void initData(final int i) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    list = dbUtils.findAll(PictureQueXianInfo.class);
                    if (list != null)
                        Collections.sort(list);
                    handler.sendEmptyMessage(i);
                    if (i != 4)
                        position = -1;
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        if (position != -1)
                            recyListView.smoothScrollToPosition(position);
                        break;
                    case 1:
                        adapter.setList(list);
                        break;
                    case 2:
                        adapter.setList(list);
                        adapter.setChooseByPosition(0);
                        break;
                    case 3:
                        adapter.setList(list);
                        adapter.setChooseByPosition(list.size() - 1);
                        break;
                    case 4:
                        adapter.setList(list);
                        adapter.setChooseByPosition(position);
                        break;
                    case 5:
                        BaseApplication.toast("请先选中一个列表选项！");
                        break;
                }
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        bitmap.recycle();
    }

    @OnClick({R.id.capture_cancel, R.id.capture_sure, R.id.capture_image, R.id.capture_add, R.id.capture_change, R.id.capture_delete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.capture_cancel:
                finish();
                break;
            case R.id.capture_sure:
                Map<String,String> map = null;
                if(list != null && position>=0) {
                    map = new HashMap<>();
                    map.put(Distance, list.get(position).getDistance());
                    map.put(DefectType, list.get(position).getStyle());
                    map.put(DefectCode, list.get(position).getName());
                    map.put(DefectLevel, list.get(position).getGrade());
                    map.put(ClockExpression, list.get(position).getClock());
                    map.put(DefectLength, list.get(position).getLength());
                    map.put(DefectDescription, list.get(position).getDetail());
                }

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(FileUtil.getFileSavePath()).append(Login_info.local_picture_path).append(fileName).append(".xml");
                File file = new File( stringBuilder.toString());
                PullXmlParseUtil.writeXml(file,fileName,mPipeIdEdt.getText().toString(),new String[]{Distance,DefectType,DefectCode,DefectLevel,ClockExpression,DefectLength,DefectDescription,},map);
                FileUtil.updateSystemLibFile(stringBuilder.toString());
                finish();
                break;
            case R.id.capture_image:
                break;
            case R.id.capture_add:

                if (position != -1)
                    new Quexian_Edit_Dialog(this, false, list.get(position)).setOnDataChangeListener(new Quexian_Edit_Dialog.OnDataChangeListener() {
                        @Override
                        public void finish() {
                            initData(3);
                        }
                    });
                else {
                    new Quexian_Edit_Dialog(this, false, null).setOnDataChangeListener(new Quexian_Edit_Dialog.OnDataChangeListener() {
                        @Override
                        public void finish() {
                            initData(3);
                        }
                    });
                }

                break;
            case R.id.capture_change:
                if (position == -1)
                    handler.sendEmptyMessage(5);
                else {
                    new Quexian_Edit_Dialog(this, true, list.get(position)).setOnDataChangeListener(new Quexian_Edit_Dialog.OnDataChangeListener() {
                        @Override
                        public void finish() {
                            initData(4);
                        }
                    });
                }

                break;
            case R.id.capture_delete:
                if (position != -1)
                    try {
                        dbUtils.delete(list.get(position));
                        LogUtil.log("数据库：删除成功",position);
                        initData(2);
                    } catch (DbException e) {
                        LogUtil.error("数据库：删除失败：",e);
                        e.printStackTrace();
                    }
                break;
        }
    }
}
