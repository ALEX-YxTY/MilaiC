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
import com.meishipintu.milai.utils.StringUtils;
import com.meishipintu.milai.views.CircleImageView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CouponDetailsActivity extends BaseActivity {

    private Picasso picasso;
    private Intent intent;

    @BindView(R.id.tv_1)
    TextView tv1;
    @BindView(R.id.tv_2)
    TextView tv2;
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
        picasso = Picasso.with(this);
        tvTitle.setText(R.string.coupon_detail);
        intent = getIntent();
        initUI();
    }

    private void initUI() {
        Coupon coupon = (Coupon) intent.getExtras().get("coupon");
        tvName.setText(coupon.getName());
        String number = coupon.getCouponSn();
        if (coupon.isMachineCode()) {
            number = coupon.getMachineCode();
        }
        tvNumber.setText(StringUtils.stringWithSpace(number));
        QrUtil.createQRCodeImage(StringUtils.stringWithSpace(number), ibQuan);

//        if (!StringUtils.isNullOrEmpty(Cookies.getUserUrl())) {
//            picasso.load(ConstansUtils.URL + Cookies.getUserUrl()).into(headportrait);
//        }
        if (coupon.isMi()) {
            tv1.setVisibility(View.INVISIBLE);
            tvMoney.setText(NumUtil.NumberFormatAuto(coupon.getValue()));
            tvValue.setVisibility(View.GONE);
            tv2.setVisibility(View.VISIBLE);
        } else {
            tvMoney.setText(NumUtil.NumberFormatAuto(coupon.getValue()));
            tvValue.setText("满" + NumUtil.NumberFormatFromDouble(coupon.getMinPrice(), 0) + "元使用");
        }
        tvTime.setText("有效期至：" + coupon.getEndTime());
    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        onBackPressed();
    }

    @Override
    public boolean useSwipeBack() {
        return true;
    }
}
