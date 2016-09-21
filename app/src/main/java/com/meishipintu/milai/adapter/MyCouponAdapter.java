package com.meishipintu.milai.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.meishipintu.milai.R;
import com.meishipintu.milai.activitys.CouponDetailsActivity;
import com.meishipintu.milai.beans.Coupon;
import com.meishipintu.milai.utils.NumUtil;
import com.meishipintu.milai.utils.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/8/22.
 */
public class MyCouponAdapter extends RecyclerView.Adapter<MyCouponAdapter.MyCouponViewHolder> {


    private List<Coupon> data;
    private Context context;

    public MyCouponAdapter(Context context, List<Coupon> list) {
        this.data = list;
        this.context = context;
    }

    @Override
    public MyCouponViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_coupon, parent, false);
        return new MyCouponViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyCouponViewHolder holder, int position) {
        final Coupon coupon = data.get(position);
        holder.tvName.setText(coupon.getName());
        holder.tvTime.setText(coupon.getEndTime());
        if (coupon.isMi()) {
            holder.tvMoney.setText(NumUtil.NumberFormatFromDouble(coupon.getValue(), 0) + "米");
            holder.tvYuan.setVisibility(View.INVISIBLE);
            holder.tvCondition.setVisibility(View.INVISIBLE);
            holder.tvNumber.setText(coupon.getCouponShow());
        } else {
            holder.tvMoney.setText(NumUtil.NumberFormatFromDouble(coupon.getValue(), 0));
            holder.tvCondition.setText("满" + NumUtil.NumberFormatFromDouble(coupon.getMinPrice(), 0)
                    + "元使用");
            holder.tvYuan.setVisibility(View.VISIBLE);
            holder.tvNumber.setText(StringUtils.stringWithSpace(coupon.getCouponSn()));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CouponDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("coupon", coupon);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyCouponViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_money)
        TextView tvMoney;
        @BindView(R.id.tv_condition)
        TextView tvCondition;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_number)
        TextView tvNumber;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.yuan)
        TextView tvYuan;

        public MyCouponViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
