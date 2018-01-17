package com.bmw.peek2slite.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bmw.peek2slite.R;

/**
 * Created by admin on 2017/3/3.
 */

public class SpinnerAdapter extends BaseAdapter  {

    public Context mContext;
    private String[] strings;

    public SpinnerAdapter(Context mContext) {
        this.mContext = mContext;
        strings = new String[0];
    }

    public void setStrings(String[] strings){
        this.strings = strings;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return strings.length;
    }

    @Override
    public Object getItem(int i) {
        return strings[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView textView = null;

        if(view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.spinner_item, viewGroup, false);
            textView = (TextView) view.findViewById(R.id.spinner_itemName);
            view.setTag(textView);
        }else{
            textView = (TextView) view.getTag();
        }

        textView.setText(strings[i]);
        return view;
    }


}
