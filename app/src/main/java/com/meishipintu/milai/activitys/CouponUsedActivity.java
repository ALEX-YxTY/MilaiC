package com.meishipintu.milai.activitys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meishipintu.milai.R;
import com.meishipintu.milai.adapter.MyCouponNewAdapter;
import com.meishipintu.milai.application.Cookies;
import com.meishipintu.milai.beans.Coupon;
import com.meishipintu.milai.netDao.NetApi;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CouponUsedActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_coupon_null)
    RelativeLayout ivCouponNull;
    @BindView(R.id.rv_coupon)
    RecyclerView rv;

    private List<Coupon> data;
    private NetApi netApi;
    private MyCouponNewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_new);
        ButterKnife.bind(this);
        tvTitle.setText("失效米券");
        ivCouponNull.setVisibility(View.GONE);
        netApi = NetApi.getInstance();
        initRv();
    }

    private void initRv() {
        data = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());
        adapter = new MyCouponNewAdapter(this, data, 33);
        rv.setAdapter(adapter);
        getData();
    }

    public void getData() {
        //获取可用卡券数据
        netApi.getCouponNew(Cookies.getUserId(), 2)
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
                        data.addAll(coupons);
                        adapter.notifyDataSetChanged();
                    }
                });
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
