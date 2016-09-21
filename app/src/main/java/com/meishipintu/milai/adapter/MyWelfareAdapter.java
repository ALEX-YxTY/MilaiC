package com.meishipintu.milai.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.meishipintu.milai.R;
import com.meishipintu.milai.beans.Welfare;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/8/9.
 */
public class MyWelfareAdapter extends RecyclerView.Adapter<MyWelfareViewHolder> {

    private Context context;
    private List<Welfare> list;
    private Picasso picasso;
    private OnWelfareItemClickListener listener;

    public MyWelfareAdapter(Context context, List<Welfare> list, OnWelfareItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        picasso = Picasso.with(context);
    }

    @Override
    public MyWelfareViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_welfare, parent, false);
        return new MyWelfareViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyWelfareViewHolder holder, final int position) {
        final Welfare welfare = list.get(position);
        picasso.load("http://" + welfare.getLogo()).into(holder.ivWelfare);
        holder.tvTitle.setText(welfare.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClickListener(position,list.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnWelfareItemClickListener{
        void onItemClickListener(int position, Welfare welfare);

    }
}

class MyWelfareViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_welfare)
    RoundedImageView ivWelfare;

    public MyWelfareViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
