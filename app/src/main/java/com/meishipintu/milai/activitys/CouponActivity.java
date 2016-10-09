package com.meishipintu.milai.activitys;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.meishipintu.milai.R;
import com.meishipintu.milai.adapter.MyCouponAdapter;
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


public class CouponActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv)
    RecyclerView rv;

    private List<Coupon> data;
    private NetApi netApi;
    private MyCouponAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Immersive.immersive(0x99999999,0,this);
        setContentView(R.layout.activity_coupon);
        ButterKnife.bind(this);
        tvTitle.setText(R.string.my_coupon);
        netApi = NetApi.getInstance();
        initData();
    }

    private void initData() {


        data = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(CouponActivity.this));
        rv.setItemAnimator(new DefaultItemAnimator());
        adapter = new MyCouponAdapter(CouponActivity.this, data);
        rv.setAdapter(adapter);

        getData();
    }

    private void getData() {
        netApi.getCoupon(Cookies.getUserId(), 1).subscribeOn(Schedulers.io())
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
                        data.addAll(coupons);
                        adapter.notifyDataSetChanged();
                    }
                });
    }


    @OnClick(R.id.iv_back)
    public void onClick() {
        onBackPressed();
    }

}
