package com.bmw.peek2slite.view.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bmw.peek2slite.R;
import com.bmw.peek2slite.model.PictureQueXianInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/9/2.
 */
public class PictureEditAdapter extends RecyclerView.Adapter<PictureEditAdapter.ViewHolder> {

    private Context context;
    private List<PictureQueXianInfo> list;
    private List<Boolean> isClicks;
    private int choosePosition = -1;

    public PictureEditAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
        isClicks = new ArrayList<>();
    }

    public void setList(List<PictureQueXianInfo> list) {
        if (list != null) {
            this.list = list;
            isClicks.clear();
            for (int i = 0; i < list.size(); i++) {
                isClicks.add(false);
            }
            notifyDataSetChanged();

        }
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.picture_edit_item, null);
        return new ViewHolder(view);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.quxian_style.setText(list.get(position).getStyle());
        holder.quexian_name.setText(list.get(position).getName());
        holder.quexian_dengji.setText(list.get(position).getGrade());

        if (!isClicks.get(position)) {
            holder.container.setBackgroundColor(context.getResources().getColor(R.color.nothing));
            setTextColorYellow(true, holder);
        } else {
            holder.container.setBackgroundColor(context.getResources().getColor(R.color.colorText));
            setTextColorYellow(false, holder);
        }
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setItemClickBg(position);
            }
        });

    }

    private void setTextColorYellow(boolean b, ViewHolder holder) {
        int colorId = 0;
        if (b) {
            colorId = context.getResources().getColor(R.color.colorText);
        } else {
            colorId = context.getResources().getColor(android.R.color.black);
        }
        holder.quxian_style.setTextColor(colorId);
        holder.quexian_name.setTextColor(colorId);
        holder.quexian_dengji.setTextColor(colorId);
    }

    private void setItemClickBg(int position) {
        if (position >= isClicks.size())
            return;

        if (isClicks.get(position)) {
            isClicks.set(position, false);
            choosePosition = -1;
        } else {

            for (int i = 0; i < isClicks.size(); i++) {
                isClicks.set(i, false);
            }
            isClicks.set(position, true);
            choosePosition = position;
        }
        notifyDataSetChanged();
        if (listener != null)
            listener.setData(choosePosition);
    }


    public void setChooseByPosition(int position){
        if(position<0 || position>=list.size())
            return;
        setItemClickBg(position);
    }

    public void setChooseByTaskId(String id){
        if(id == null )
            return;
        for(int i=0;i<list.size();i++){
            if(id.equals(list.get(i).getId())){
                setItemClickBg(i);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView quxian_style;
        private TextView quexian_name;
        private TextView quexian_dengji;
        private LinearLayout container;

        public ViewHolder(View itemView) {
            super(itemView);
            quxian_style = (TextView) itemView.findViewById(R.id.picture_edit_item_leixing);
            quexian_name = (TextView) itemView.findViewById(R.id.picture_edit_item_quexianName);
            quexian_dengji = (TextView) itemView.findViewById(R.id.picture_edit_item_dengji);
            container = (LinearLayout) itemView.findViewById(R.id.picture_edit_item_container);

        }
    }


    public interface OnDataChooseListener {
        void setData(int id);
    }

    private OnDataChooseListener listener;

    public void setOnDataChooseListener(OnDataChooseListener listener) {
        this.listener = listener;
    }

}
