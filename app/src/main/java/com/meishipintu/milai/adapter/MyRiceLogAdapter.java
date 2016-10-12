package com.meishipintu.milai.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.meishipintu.milai.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 抢米明细和用米明细的抽象类Adapter
 * Created by Administrator on 2016/10/12.
 */

public abstract class MyRiceLogAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    protected static final int ITEM_TAIL = 1;
    protected static final int ITEM_BODY = 0;
    protected static final int ITEM_NULL = -1;

    protected Context context;
    protected List<T> data;
    protected Picasso picasso;
    protected boolean isEmpty = false;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (getItemCount() == 1) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_no_info, parent, false);
            return new MyRiceLogAdapter.EmptyViewHolder(view);
        } else {
            if (viewType != ITEM_TAIL) {
                View view = LayoutInflater.from(context).inflate(R.layout.item_get_mi_log_body, parent, false);
                return new MyRiceLogAdapter.LogViewHolder(view);
            } else {
                View view = LayoutInflater.from(context).inflate(R.layout.item_dian, parent, false);
                return new MyRiceLogAdapter.TailViewHolder(view);
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
            isEmpty = true;
        }
        return data.size() + 1;
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
        @BindView(R.id.tv_qiang)
        TextView tvQiang;
        @BindView(R.id.state)
        TextView state;

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

    class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

}
