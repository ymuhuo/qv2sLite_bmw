package com.bmw.peek2slite.presenter.impl;

import com.bmw.peek2slite.utils.FileComparator;
import com.bmw.peek2slite.utils.FileUtil;
import com.bmw.peek2slite.utils.UrlUtil;
import com.bmw.peek2slite.view.adapter.FileListAdapter;
import com.bmw.peek2slite.presenter.FilePresenter;
import com.bmw.peek2slite.model.Login_info;
import com.bmw.peek2slite.view.viewImpl.FileViewImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by admin on 2016/9/8.
 */
public class FilePresenterImpl implements FilePresenter {

    private FileListAdapter adapter;
    private boolean isPicture;
    private FileViewImpl view;
    private boolean isFileWork;

    public FilePresenterImpl(FileListAdapter adapter, boolean isPicture, FileViewImpl view) {
        this.isPicture = isPicture;
        this.adapter = adapter;
        this.view = view;
//        initAdapter();
    }

    @Override
    public void initAdapter() {
        adapter.setFiles(initData());
        adapter.setOnItemLongClickListener(new FileListAdapter.OnItemLongClickListener() { //长按进入文件管理模式
            @Override
            public void longClick(int position) {
                delete();
                adapter.setList(position);
            }
        });
        initDiskSize();
    }

    private void initDiskSize() {
        StringBuilder stringBuilder = new StringBuilder();
        List<Float> diskSizeList = FileUtil.getDiskCapacity();
        stringBuilder.append(Login_info.getInstance().isSaveToExSdcard() ? "SD卡存储" : "本机存储").
                append("\n").append(diskSizeList.get(2)).append("G").append("/").append(diskSizeList.get(0)).append("G");
        view.setDiskSize(stringBuilder.toString());
    }

    private List<File> initData() { //从指定文件夹获取文件列表

        List<File> files = null;
        if (isPicture) {
            files = UrlUtil.getFileUtils(FileUtil.getFileSavePath() + Login_info.local_picture_path);
            List<File> picFiles = new ArrayList<>();
            if (files != null)
                for (int i = 0; i < files.size(); i++) {
                    String name = files.get(i).getName();
                    name = name.substring(name.lastIndexOf("."), name.length());
                    if (name.equals(".jpg")) {
                        picFiles.add(files.get(i));
                    } else {
                        boolean isDelete = true;
                        for (File f : files) {
                            String fName = files.get(i).getName();
                            fName = fName.substring(fName.lastIndexOf("/") + 1, fName.lastIndexOf("."));
                            String tName = f.getName();
                            String tNameF = tName.substring(tName.lastIndexOf("/") + 1, tName.lastIndexOf("."));
                            String tNameE = tName.substring(tName.lastIndexOf("."), tName.length());
                            if (tNameF.equals(fName) && tNameE.equals(".jpg")) {
                                isDelete = false;
                            }
                        }
                        if (isDelete) {
                            files.get(i).delete();
                            FileUtil.updateSystemLibFile(files.get(i).getAbsolutePath());
                        }
                    }
                }
            files = picFiles;
        } else {
            files = UrlUtil.getFileUtils(FileUtil.getFileSavePath() + Login_info.local_video_path);
            for (int i = 0; i < files.size(); i++) {
                String vName = files.get(i).getName();
                if (!vName.contains(".mp4") && !vName.contains(".MP4") ) {
                    files.remove(i);
                    i--;
                }
            }
        }
        if (files != null) {
            FileComparator fileComparator = new FileComparator();
            Collections.sort(files, fileComparator);
        }
        return files;

    }

    @Override
    public void searching(String msg) {
        if (msg.equals("")) {
//            view.mToast("请先输入搜索内容！");
            adapter.setFiles(initData());
            return;
        }
        List<File> searFiles = new ArrayList<>();
        if (initData() != null)
            for (File f : initData()) {
                String name = f.getName();
                if (name.contains(msg)) {
                    searFiles.add(f);
                }
            }
//        if (searFiles.size() != 0) {
        adapter.setFiles(searFiles);
        view.showToast("搜索到" + searFiles.size() + "个文件");
//        } else {
//            adapter.setFiles(searFiles);
//        }
//            view.mToast("未搜索到相关文件！");
    }

    @Override
    public void delete() {
        if (isFileWork) {
            int n = 0;
            if (adapter.getList() != 0) {
                n = adapter.deleteFile();
                adapter.setFiles(initData());
                view.showToast("成功删除" + n + "个文件");
            }
            isFileWork = false;
            view.isMenuShow(false);
            adapter.setChoose(false);
            view.btnChange("文件管理");
        } else {
            isFileWork = true;
            adapter.setChoose(true);
            view.btnChange("删除");
            view.isMenuShow(true);
        }
    }

    @Override
    public void chooseAll() {
        adapter.chooseAll();
    }

    @Override
    public void cancelAll() {
        if (adapter.getList() != 0)
            adapter.cancelAll();
        else {
            isFileWork = false;
            view.isMenuShow(false);
            adapter.setChoose(false);
            view.btnChange("文件管理");
        }
    }

}
