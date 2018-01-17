package com.bmw.peek2slite.view.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bmw.peek2slite.R;
import com.bmw.peek2slite.model.FileInfo;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * ApkInfo Adapter
 *
 * Created by mayubao on 2016/11/24.
 * Contact me 345269374@qq.com
 */
public class FileInfoAdapter extends CommonAdapter<FileInfo> {

    /**
     * 文件类型的标识
     */
    private int mType = FileInfo.TYPE_JPG;

    public FileInfoAdapter(Context context, List<FileInfo> dataList) {
        super(context, dataList);
    }

    public FileInfoAdapter(Context context, List<FileInfo> dataList, int type) {
        super(context, dataList);
        this.mType = type;
    }

    @Override
    public View convertView(int position, View convertView) {
        FileInfo fileInfo = getDataList().get(position);

       if(mType == FileInfo.TYPE_JPG){ //JPG convertView
            JpgViewHolder viewHolder = null;
            if(convertView == null){
                convertView = View.inflate(getContext(), R.layout.item_jpg, null);
                viewHolder = new JpgViewHolder();
                viewHolder.iv_ok_tick = (ImageView) convertView.findViewById(R.id.iv_ok_tick);
                viewHolder.iv_shortcut = (ImageView) convertView.findViewById(R.id.iv_shortcut);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (JpgViewHolder) convertView.getTag();
            }

            if(getDataList() != null && getDataList().get(position) != null && fileInfo.getSize()>0){

//                viewHolder.iv_shortcut.setImageBitmap(fileInfo.getBitmap());
                Glide
                    .with(getContext())
                    .load(fileInfo.getFilePath())
                    .centerCrop()
                    .placeholder(R.mipmap.picture1)
                    .crossFade()
                    .into(viewHolder.iv_shortcut);

                /*//全局变量是否存在FileInfo
                if(AppContext.getAppContext().isExist(fileInfo)){
                    viewHolder.iv_ok_tick.setVisibility(View.VISIBLE);
                }else{
                    viewHolder.iv_ok_tick.setVisibility(View.GONE);
                }*/
            }
        }

        return convertView;
    }

    static class ApkViewHolder {
        ImageView iv_shortcut;
        ImageView iv_ok_tick;
        TextView tv_name;
        TextView tv_size;
    }

    static class JpgViewHolder {
        ImageView iv_shortcut;
        ImageView iv_ok_tick;
    }

    static class Mp3ViewHolder {
        ImageView iv_ok_tick;
        TextView tv_name;
        TextView tv_size;
    }

    static class Mp4ViewHolder {
        ImageView iv_shortcut;
        ImageView iv_ok_tick;
        TextView tv_name;
        TextView tv_size;
    }
}
