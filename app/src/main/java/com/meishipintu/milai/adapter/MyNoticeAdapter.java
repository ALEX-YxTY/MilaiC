package com.meishipintu.milai.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.meishipintu.milai.R;
import com.meishipintu.milai.beans.Notice;
import com.meishipintu.milai.utils.DateUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/9/12 0012.
 */
public class MyNoticeAdapter extends RecyclerView.Adapter<MyNoticeAdapter.ViewHolder> {


    private List<Notice> list;
    private Context context;

    public MyNoticeAdapter(List<Notice> list, Context context) {
        this.list = list;
        this.context = context;
    }


    //
    @Override
    public  ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notice, parent, false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }


    //
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Notice info = list.get(position);
        String[] time = DateUtils.getTimeArray(info.getTime());
        holder.tvTime.setText(time[0]+"/"+time[1]);
        holder.tvTitleNotice.setText(info.getTitle());
        holder.tvNoticeContent.setText(info.getContent());


    }

    //
    @Override
    public int getItemCount() {

            return list.size();

    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_Icon)
        ImageView ivIcon;
        @BindView(R.id.tv_title_notice)
        TextView tvTitleNotice;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_notice_content)
        TextView tvNoticeContent;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}






