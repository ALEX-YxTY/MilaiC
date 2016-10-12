package com.meishipintu.milai.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.meishipintu.milai.beans.ExchangeRiceLog;
import com.meishipintu.milai.utils.DateUtils;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;


/**
 * Created by Administrator on 2016/8/22.
 */
public class MyExchangeRiceLogAdapter extends MyRiceLogAdapter<ExchangeRiceLog> implements Serializable {

    public MyExchangeRiceLogAdapter(Context context, List<ExchangeRiceLog> data) {
        this.context = context;
        this.data = data;
        picasso = Picasso.with(context);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (data.size() > 0) {
            if (getItemViewType(position) != ITEM_TAIL) {
                ExchangeRiceLog log = data.get(position);
                //非尾节点
                String[] time = DateUtils.getTimeArray(log.getTime());

                LogViewHolder logHolder = (LogViewHolder) holder;
                logHolder.tvQiang.setText("兑换了");
                logHolder.state.setText("被你兑换了");
                logHolder.tvMount.setText(log.getUse_rice() + "米");
                logHolder.tvMount2.setText(log.getUse_rice() + "米");
                logHolder.tvDesc.setText(log.getTitle());
                picasso.load("http://" + log.getDetail_logo()).into(logHolder.ivTask);
                logHolder.tvMonth.setText(time[0] + "月");
                logHolder.tvDay.setText(time[1]);
                logHolder.tvTime.setText(time[2]);
            }
        }
    }

}
