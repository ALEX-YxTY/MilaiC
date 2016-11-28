package com.meishipintu.milai.activitys;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.TextView;

import com.meishipintu.milai.R;
import com.meishipintu.milai.adapter.CouponFragmentAdapter;
import com.meishipintu.milai.application.Cookies;
import com.meishipintu.milai.beans.Coupon;
import com.meishipintu.milai.fragments.CouponFragment;
import com.meishipintu.milai.netDao.NetApi;
import com.meishipintu.milai.utils.ConstansUtils;
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
//        //初始化显示选项卡
//        if(getIntent().getBooleanExtra("showCanUse", false)){
//            vPager.setCurrentItem(0);               //显示未使用
//        }
    }

    private void initFragment() {
        coupon = new ArrayList<>();
        usedcoupons=new ArrayList<>();
        machinecoupons=new ArrayList<>();

        //通过setArgument向Fragment传值
        couponFragment=new CouponFragment();
        Bundle bundleCanUse = new Bundle();
        bundleCanUse.putSerializable("data", (Serializable) coupon);
        bundleCanUse.putInt("coupon_type", ConstansUtils.COUPON_USABLE);
        couponFragment.setArguments(bundleCanUse);

        usedCouponFragment =new CouponFragment();
        Bundle bundleUsed = new Bundle();
        bundleUsed.putSerializable("data", (Serializable) usedcoupons);
        bundleUsed.putInt("coupon_type", ConstansUtils.COUPON_USAD);
        usedCouponFragment.setArguments(bundleUsed);

//        machinecouponsFragment =new CouponFragment();
//        Bundle bundleMachine = new Bundle();
//        bundleMachine.putSerializable("data", (Serializable) machinecoupons);
//        bundleMachine.putInt("coupon_type", ConstansUtils.COUPON_MACHINE_CODE);
//        machinecouponsFragment.setArguments(bundleMachine);
    }

    private void initTabAndViewPager() {
        vPager = (ViewPager) findViewById(R.id.viewPager);

        CouponFragmentAdapter pagerAdapter = new CouponFragmentAdapter(getSupportFragmentManager());

//        pagerAdapter.addFragment( machinecouponsFragment,"提货码");
        pagerAdapter.addFragment(couponFragment,"未使用");
        pagerAdapter.addFragment(usedCouponFragment,"已使用");

        vPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(vPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

    }

    private void getData(final int page) {
        netApi.getCoupon(Cookies.getUserId(),page).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Coupon>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("test", "error:" + e.getMessage());
                        ToastUtils.show(CouponActivityTabLayout.this,"获取卡券信息失败，请检查网络");
                    }

                    @Override
                    public void onNext(List<Coupon> coupons) {
                        Log.i("UID------------", Cookies.getUserId()+"");

                        switch (page){
                            case 0:    //机器码
                                machinecoupons.clear();
                                machinecoupons.addAll(coupons);
                                machinecouponsFragment.refreshUI();
                                getData(1);
                                break;
                            case 1:     //未使用
                                coupon.clear();
                                coupon.addAll(coupons);
                                couponFragment.refreshUI();
                                getData(2);
                                break;
                            case 2:     //已使用
                                usedcoupons.clear();
                                usedcoupons.addAll(coupons);
                                usedCouponFragment.refreshUI();
                                break;
                            default:
                                break;
                        }
                    }
                });
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
        getData(1);
    }
}

