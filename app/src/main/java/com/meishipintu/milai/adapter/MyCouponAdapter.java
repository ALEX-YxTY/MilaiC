package com.meishipintu.milai.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meishipintu.milai.R;
import com.meishipintu.milai.activitys.CouponDetailsActivity;
import com.meishipintu.milai.beans.Coupon;
import com.meishipintu.milai.utils.ConstansUtils;
import com.meishipintu.milai.utils.NumUtil;
import com.meishipintu.milai.utils.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/8/22.
 */
public class MyCouponAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int TYPE_EMPTY = 1;
    private static final int TYPE_NORMAL = 2;


    private List<Coupon> data;
    private Context context;

    private boolean isEmpty;
    private int mCoupon_type;

    public MyCouponAdapter(Context context, List<Coupon> list, int mCoupon_type) {
        this.data = list;
        this.context = context;
        this.mCoupon_type = mCoupon_type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_NORMAL) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_coupon, parent, false);
            return new MyCouponViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_empty, parent, false);
            return new MyEmptyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {
        if (getItemViewType(position) == TYPE_NORMAL) {
            MyCouponViewHolder holder = (MyCouponViewHolder) holder1;
            final Coupon coupon = data.get(position);
            Log.i("test", "coupon_type:" + mCoupon_type);
            //根据卡券类型调整显示
            switch (mCoupon_type) {
                case ConstansUtils.COUPON_MACHINE_CODE:
                    holder.coupon.setBackgroundResource(R.drawable.bg_machine_coupon);
                    holder.circle.setBackgroundResource(R.drawable.shape_cicrle_machine);
                    holder.tvMoney.setTextColor(context.getResources().getColor(R.color.machine_blue));
                    holder.tvMi.setTextColor(context.getResources().getColor(R.color.machine_blue));
                    holder.tvYuan.setTextColor(context.getResources().getColor(R.color.machine_blue));
                    break;
                case ConstansUtils.COUPON_USAD:
                    holder.circle.setBackgroundResource(R.drawable.shape_cicrle_unuse);
                    holder.tvMoney.setTextColor(context.getResources().getColor(R.color.unused_gray));
                    holder.tvMi.setTextColor(context.getResources().getColor(R.color.unused_gray));
                    holder.tvYuan.setTextColor(context.getResources().getColor(R.color.unused_gray));
                    holder.tvName.setTextColor(context.getResources().getColor(R.color.unused_gray));
                    holder.tvNumber.setTextColor(context.getResources().getColor(R.color.unused_gray));
                    holder.used.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }

            holder.tvName.setText(coupon.getName());
            holder.tvTime.setText(coupon.getEndTime());
            if (coupon.isMi()) {
                holder.tvMoney.setText(NumUtil.NumberFormatFromDouble(coupon.getValue(), 0));
                holder.tvYuan.setVisibility(View.GONE);
                holder.tvMi.setVisibility(View.VISIBLE);
                holder.tvCondition.setVisibility(View.GONE);
                holder.tvNumber.setText(coupon.getCouponShow());
            } else {
                holder.tvMoney.setText(NumUtil.NumberFormatFromDouble(coupon.getValue(), 0));
                holder.tvCondition.setVisibility(View.VISIBLE);
                holder.tvCondition.setText("满" + NumUtil.NumberFormatFromDouble(coupon.getMinPrice(), 0)
                        + "元使用");
                holder.tvYuan.setVisibility(View.VISIBLE);
                holder.tvMi.setVisibility(View.GONE);
                holder.tvNumber.setText(StringUtils.stringWithSpace(coupon.getCouponSn()));
            }

            //已使用无点击效果
            if (mCoupon_type != ConstansUtils.COUPON_USAD) {
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
        }
    }

    @Override
    public int getItemCount() {
        isEmpty = false;
        if (data.size() == 0) {
            isEmpty = true;
            return 1;
        }
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isEmpty && position == 0) {
            return TYPE_EMPTY;
        }
        return TYPE_NORMAL;
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
        @BindView(R.id.mi)
        TextView tvMi;
        @BindView(R.id.circle)
        RelativeLayout circle;
        @BindView(R.id.used)
        ImageView used;
        @BindView(R.id.coupon)
        LinearLayout coupon;

        public MyCouponViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class MyEmptyViewHolder extends RecyclerView.ViewHolder {

        public MyEmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

}
