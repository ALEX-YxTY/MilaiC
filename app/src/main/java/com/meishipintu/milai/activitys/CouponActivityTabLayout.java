package com.meishipintu.milai.activitys;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.meishipintu.milai.R;
import com.meishipintu.milai.adapter.CouponFragmentAdapter;
import com.meishipintu.milai.application.Cookies;
import com.meishipintu.milai.beans.Coupon;
import com.meishipintu.milai.fragments.CouponFragment;
import com.meishipintu.milai.netDao.NetApi;
import com.meishipintu.milai.utils.Immersive;
import com.meishipintu.milai.utils.ToastUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CouponActivityTabLayout extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager vPager;

    private NetApi netApi;
    private CouponFragment selectFragment;

    private List<Coupon> coupon;
    private List<Coupon> usedcoupons;
    private List<Coupon> machinecoupons;
    private CouponFragment couponFragment;
    private CouponFragment usedCouponFragment;
    private CouponFragment machinecouponsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Immersive.immersive(0x99999999,0,this);
        setContentView(R.layout.activity_coupon_tab_layout);
        ButterKnife.bind(this);
        netApi = NetApi.getInstance();
        tvTitle.setText(R.string.my_coupon);

        //初始化fragment
        initFragment();
        //初始化tabLayout和viewpager
        initTabAndViewPager();
    }

    private void initFragment() {
        coupon = new ArrayList<>();
        usedcoupons=new ArrayList<>();
        machinecoupons=new ArrayList<>();

        //通过setArgument向Fragment传值
        couponFragment=new CouponFragment();
        Bundle bundleCanUse = new Bundle();
        bundleCanUse.putSerializable("data", (Serializable) coupon);
        couponFragment.setArguments(bundleCanUse);

        usedCouponFragment =new CouponFragment();
        Bundle bundleUsed = new Bundle();
        bundleUsed.putSerializable("data", (Serializable) usedcoupons);
        usedCouponFragment.setArguments(bundleUsed);

        machinecouponsFragment =new CouponFragment();
        Bundle bundleMachine = new Bundle();
        bundleMachine.putSerializable("data", (Serializable) machinecoupons);
        machinecouponsFragment.setArguments(bundleMachine);
    }

    private void initTabAndViewPager() {
        vPager = (ViewPager) findViewById(R.id.viewPager);

        CouponFragmentAdapter pagerAdapter = new CouponFragmentAdapter(getSupportFragmentManager());

        pagerAdapter.addFragment( machinecouponsFragment,"机器码");
        pagerAdapter.addFragment(couponFragment,"未使用");
        pagerAdapter.addFragment(usedCouponFragment,"已使用");

        vPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(vPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

    }

    private void getData() {
        for(int tense=0;tense<3;tense++) {
            final int finalTense = tense;
            netApi.getCoupon(Cookies.getUserId(),tense).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<Coupon>>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            ToastUtils.show(CouponActivityTabLayout.this, e.getMessage());
                        }

                        @Override
                        public void onNext(List<Coupon> coupons) {
                            switch (finalTense){
                                case 0:    //机器码
                                    machinecoupons.clear();
                                    machinecoupons.addAll(coupons);
                                    selectFragment = machinecouponsFragment;
                                    break;
                                case 1:     //未使用
                                    coupon.clear();
                                    coupon.addAll(coupons);
                                    selectFragment = couponFragment;
                                    break;
                                case 2:     //已使用
                                    usedcoupons.clear();
                                    usedcoupons.addAll(coupons);
                                    selectFragment = usedCouponFragment;
                                    break;
                                default:
                                    break;
                            }/*上面一段代码解析：因为想在进入米券页面时，同时获取到未使用、已使用和已过期三个状态时的数据。所以过去到getdata(1)后再获取getdata(2)..等获取完成后刷新*/
                            selectFragment.refreshUI();
                        }
                    });
        }
    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        onBackPressed();
    }

    @Override
    public boolean useSwipeBack() {
        if (vPager.getCurrentItem() == 0) {
            return true;
        }
        return super.useSwipeBack();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //初始化数据
        getData();
    }
}

