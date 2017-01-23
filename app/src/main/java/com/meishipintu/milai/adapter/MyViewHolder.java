package com.meishipintu.milai.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.meishipintu.milai.R;

import butterknife.ButterKnife;


/**
 * Created by Administrator on 2017/1/4.
 */


public class MyViewHolder extends RecyclerView.ViewHolder {
    public ImageView iv;
    public TextView tv;
    public TextView tvTime;
    public TextView tvName;
    public CardView cv;
    public TextView tvCityTime;

    public MyViewHolder(View itemView) {
        super(itemView);
        iv = (ImageView) itemView.findViewById(R.id.iv);
        tv = (TextView) itemView.findViewById(R.id.title);
        tvTime = (TextView) itemView.findViewById(R.id.tv_item_time);
        tvName = (TextView) itemView.findViewById(R.id.tv_item_name);
        cv=(CardView) itemView.findViewById(R.id.cv);
        tvCityTime = (TextView) itemView.findViewById(R.id.tv_city_time);
    }
}
