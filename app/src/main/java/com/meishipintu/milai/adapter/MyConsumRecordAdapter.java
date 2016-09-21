package com.meishipintu.milai.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.meishipintu.milai.R;
import com.meishipintu.milai.beans.ConsumeRecordInfo;
import com.meishipintu.milai.utils.DateUtils;
import com.meishipintu.milai.utils.NumUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/8/25.
 */
public class MyConsumRecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ConsumeRecordInfo> list;
    private Context context;

    //主体节点和尾节点
    private final int TYPE_BODY = 1;
    private final int TYPE_TAIL = 0;

    public MyConsumRecordAdapter(List<ConsumeRecordInfo> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_TAIL) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_dian, parent, false);
            return new MyConsumeRecordTailViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_records_of_consume, parent, false);
            return new MyConsumeRecordViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_BODY) {
            ConsumeRecordInfo info = list.get(position);
            String[] time = DateUtils.getTimeArray(info.getCreateTime());
            MyConsumeRecordViewHolder bodyHolder = (MyConsumeRecordViewHolder) holder;
            bodyHolder.tvMonth.setText(time[0] + "月");
            bodyHolder.tvDay.setText(time[1]);
            bodyHolder.tvTime.setText(time[2]);
            bodyHolder.tvQiang.setText(info.getShopName());
            bodyHolder.tvMoney1.setText(NumUtil.NumberFormat(info.getTradeValue(), 0) + "元");
            bodyHolder.tvMoney2.setText(NumUtil.NumberFormat(info.getCouponDiscount(), 0) + "元");
            bodyHolder.tvMiMoney3.setText(NumUtil.NumberFormat(info.getMiDiscount(), 0) + "元");
            bodyHolder.tvMoney4.setText(NumUtil.NumberFormat(info.getRealPay(), 0) + "元");
        }
    }

    @Override
    public int getItemCount() {
        if (list.size() == 0) {
            return 0;
        } else {
            return list.size() + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        //position从0开始，比size小1
        if (position == list.size()) {
            return TYPE_TAIL;
        } else {
            return TYPE_BODY;
        }
    }

    class MyConsumeRecordViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_day)
        TextView tvDay;
        @BindView(R.id.tv_qiang)
        TextView tvQiang;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_month)
        TextView tvMonth;
        @BindView(R.id.tv_money1)
        TextView tvMoney1;
        @BindView(R.id.tv_money2)
        TextView tvMoney2;
        @BindView(R.id.tv_mi_money3)
        TextView tvMiMoney3;
        @BindView(R.id.tv_money4)
        TextView tvMoney4;

        public MyConsumeRecordViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class MyConsumeRecordTailViewHolder extends RecyclerView.ViewHolder {

        public MyConsumeRecordTailViewHolder(View itemView) {
            super(itemView);
        }
    }
}
