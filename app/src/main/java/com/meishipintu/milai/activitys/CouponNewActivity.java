package com.meishipintu.milai.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.meishipintu.milai.R;
import com.meishipintu.milai.adapter.MyCouponNewAdapter;
import com.meishipintu.milai.application.Cookies;
import com.meishipintu.milai.beans.Coupon;
import com.meishipintu.milai.netDao.NetApi;
import com.meishipintu.milai.utils.Immersive;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class CouponNewActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_coupon)
    RecyclerView rv;

    private List<Coupon> data;
    private NetApi netApi;
    private MyCouponNewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Immersive.immersive(0x99999999, 0, this);
        setContentView(R.layout.activity_coupon_new);
        ButterKnife.bind(this);
        netApi = NetApi.getInstance();
        tvTitle.setText("我的米券");
        initData();
    }

    private void initData() {
        data = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(CouponNewActivity.this));
        rv.setItemAnimator(new DefaultItemAnimator());
        adapter = new MyCouponNewAdapter(CouponNewActivity.this, data, 11);
        rv.setAdapter(adapter);
    }

    private void getData() {
        //获取可用卡券数据
        netApi.getCouponNew(Cookies.getUserId(), 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Coupon>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(List<Coupon> coupons) {
                        data.clear();
                        data.addAll(coupons);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    @OnClick({R.id.iv_back,R.id.iv_coupon_null})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_coupon_null:
                startActivity(new Intent(CouponNewActivity.this, CouponUsedActivity.class));
                break;
        }
    }

    @Override
    public boolean useSwipeBack() {
        return true;
    }
}
