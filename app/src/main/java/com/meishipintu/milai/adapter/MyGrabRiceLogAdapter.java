package com.meishipintu.milai.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.meishipintu.milai.R;
import com.meishipintu.milai.beans.GrabRiceLog;
import com.meishipintu.milai.utils.DateUtils;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/8/22.
 */
public class MyGrabRiceLogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Serializable{

    private static final int ITEM_TAIL = 1;
    private static final int ITEM_BODY = 0;
    private static final int ITEM_NULL = -1;


    private Context context;
    private List<GrabRiceLog> data;
    private Picasso picasso;

    public MyGrabRiceLogAdapter(Context context, List<GrabRiceLog> data) {
        this.context = context;
        this.data = data;
        picasso = Picasso.with(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType != ITEM_TAIL) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_get_mi_log_body, parent, false);
            return new LogViewHolder(view);
        } else if (viewType == ITEM_TAIL) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_dian, parent, false);
            return new TailViewHolder(view);
        } else return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (data.size() > 0) {
            if (getItemViewType(position) != ITEM_TAIL) {
                GrabRiceLog log = data.get(position);
                //非尾节点
                String[] time = DateUtils.getTimeArray(log.getTime());

                LogViewHolder logHolder = (LogViewHolder) holder;
                logHolder.tvMount.setText(log.getRice()+"米");
                logHolder.tvMount2.setText(log.getRice()+"米");
                logHolder.tvDesc.setText(log.getRinfo().getTitle());
                picasso.load("http://" + log.getRinfo().getImg()).into(logHolder.ivTask);
                logHolder.tvMonth.setText(time[0]+"月");
                logHolder.tvDay.setText(time[1]);
                logHolder.tvTime.setText(time[2]);
            }
        }

    }


    @Override
    public int getItemViewType(int position) {
        if (data.size() > 0) {
            //position从0开始，比size小1
            if (position == data.size()) {
                return ITEM_TAIL;
            } else {
                return ITEM_BODY;
            }
        } else return ITEM_NULL;

    }

    @Override
    public int getItemCount() {
        if (data.size() == 0) {
            return 0;
        }else return data.size() + 1;
    }


    class LogViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.day)
        TextView tvDay;
        @BindView(R.id.month)
        TextView tvMonth;
        @BindView(R.id.tv_mi1)
        TextView tvMount2;
        @BindView(R.id.iv_photo)
        ImageView ivTask;
        @BindView(R.id.tv_name)
        TextView tvDesc;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_mi)
        TextView tvMount;

        public LogViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class TailViewHolder extends RecyclerView.ViewHolder {

        public TailViewHolder(View itemView) {
            super(itemView);
        }
    }


}
