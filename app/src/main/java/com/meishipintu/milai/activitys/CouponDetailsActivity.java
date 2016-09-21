package com.meishipintu.milai.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.client.android.QrUtil;
import com.meishipintu.milai.R;
import com.meishipintu.milai.beans.Coupon;
import com.meishipintu.milai.utils.Immersive;
import com.meishipintu.milai.utils.NumUtil;
import com.meishipintu.milai.views.CircleImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CouponDetailsActivity extends BaseActivity {


    private Intent intent;

    @BindView(R.id.tv_1)
    TextView tv1;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.headportrait)
    CircleImageView headportrait;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_value)
    TextView tvValue;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.ib_quan)
    ImageView ibQuan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Immersive.immersive(0x99999999, 0, this);
        setContentView(R.layout.activity_coupon_details);
        ButterKnife.bind(this);
        tvTitle.setText(R.string.coupon_detail);
        intent = getIntent();
        initUI();
    }

    private void initUI() {
        Coupon coupon = (Coupon) intent.getExtras().get("coupon");
        tvName.setText(coupon.getName());
        tvNumber.setText(coupon.getCouponSn());
        if (coupon.isMi()) {
            tv1.setVisibility(View.INVISIBLE);
            tvMoney.setText(NumUtil.NumberFormatFromDouble(coupon.getValue(), 1) + "米");
            tvValue.setVisibility(View.GONE);
        } else {
            tvMoney.setText(NumUtil.NumberFormatFromDouble(coupon.getValue(), 1));
            tvValue.setText("满" + NumUtil.NumberFormatFromDouble(coupon.getMinPrice(), 0) + "元使用");
        }
        QrUtil.createQRCodeImage(coupon.getCouponSn(), ibQuan);
        tvTime.setText("有效期至：" + coupon.getEndTime());
    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        onBackPressed();
    }

}
