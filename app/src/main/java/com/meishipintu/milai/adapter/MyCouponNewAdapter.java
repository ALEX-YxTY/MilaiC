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
public class MyCouponNewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_EMPTY = 1;
    private static final int TYPE_NORMAL = 2;

    private List<Coupon> data;
    private Context context;
    private boolean isEmpty;
    private int mCoupon_type;

    public MyCouponNewAdapter(Context context, List<Coupon> list, int mCoupon_type) {
        this.data = list;
        this.context = context;
        this.mCoupon_type = mCoupon_type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_NORMAL) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_coupon_new, parent, false);
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
            holder.tvName.setText(coupon.getName());
            holder.tvTime.setText(coupon.getEndTime());

            holder.tvMoney.setText(NumUtil.NumberFormatAuto(coupon.getValue()));
            holder.tvCondition.setText("满" + NumUtil.NumberFormatFromDouble(coupon.getMinPrice(), 0)
                    + "元使用");
            holder.tvNumber.setText(StringUtils.stringWithSpace(coupon.getCouponSn()));

            if (mCoupon_type != ConstansUtils.COUPON_USAD) {
                //可使用卡券
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
            } else {
                Log.d(ConstansUtils.APP_NAME, "tem_flag:" + coupon.getTemp_flag());
                //不可用卡券
                holder.rlColor.setBackgroundColor(0x66ebebeb);
                holder.tvConfig.setTextColor(0xff666666);
                holder.tvMoney.setTextColor(0xff666666);
                holder.tvCondition.setTextColor(0xff999999);
                holder.tvName.setTextColor(0xff999999);
                holder.tvNumber.setTextColor(0xff999999);
                holder.tv1.setTextColor(0xff999999);
                holder.tvTime.setTextColor(0xff999999);
                holder.tvOutDate.setVisibility(View.VISIBLE);
                if ("1".equals(coupon.getTemp_flag())) {
                    //已使用
                    holder.ivUsed.setImageResource(R.drawable.flag_used);
                } else if ("2".equals(coupon.getTemp_flag())) {
                    //过期
                    holder.ivUsed.setImageResource(R.drawable.flag_out_date);
                }
                holder.ivUsed.setVisibility(View.VISIBLE);
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

        @BindView(R.id.tv_coupon)
        TextView tvCoupon;
        @BindView(R.id.tv_lading)
        TextView tvOutDate;
        @BindView(R.id.tv_config)
        TextView tvConfig;
        @BindView(R.id.tv_money)
        TextView tvMoney;
        @BindView(R.id.tv_condition)
        TextView tvCondition;
        @BindView(R.id.rl_color)
        RelativeLayout rlColor;
        @BindView(R.id.tv_1)
        TextView tv1;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_number)
        TextView tvNumber;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.iv_used)
        ImageView ivUsed;

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
