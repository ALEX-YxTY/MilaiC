package com.meishipintu.milai.activitys;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import com.meishipintu.milai.R;
import com.meishipintu.milai.adapter.CouponFragmentAdapter;
import com.meishipintu.milai.application.Cookies;
import com.meishipintu.milai.beans.Coupon;
import com.meishipintu.milai.fragments.CouponFragment;
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

public class CouponActivityTabLayout extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager vPager;

    private NetApi netApi;
    private int selecPage = 0;      //当前显示页
    private CouponFragment selectFragment;

    private List<Coupon> coupon;
    private List<Coupon> overduecoupons;
    private List<Coupon> usedcoupons;
    private List<Coupon> machinecoupons;
    private CouponFragment couponFragment;
    private CouponFragment overdueCouponFragment;
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
        //初始化数据
        getData(selecPage);
    }

    private void initFragment() {
        coupon = new ArrayList<>();
        overduecoupons=new ArrayList<>();
        usedcoupons=new ArrayList<>();
        machinecoupons=new ArrayList<>();

        couponFragment=new CouponFragment();
        couponFragment.setArguments(coupon);
        overdueCouponFragment=new CouponFragment();
        overdueCouponFragment.setArguments(overduecoupons);
        usedCouponFragment =new CouponFragment();
        usedCouponFragment.setArguments(usedcoupons);
        machinecouponsFragment =new CouponFragment();
        machinecouponsFragment.setArguments(usedcoupons);

        selectFragment = machinecouponsFragment;
    }

    private void initTabAndViewPager() {
        vPager = (ViewPager) findViewById(R.id.viewPager);
        vPager.setCurrentItem(0);

        CouponFragmentAdapter pagerAdapter = new CouponFragmentAdapter(getSupportFragmentManager());

        pagerAdapter.addFragment( machinecouponsFragment,"机器码");
        pagerAdapter.addFragment(couponFragment,"未使用");
        pagerAdapter.addFragment(usedCouponFragment,"已使用");
        pagerAdapter.addFragment( overdueCouponFragment,"已过期");

        vPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(vPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        vPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (selecPage != position) {
                    selecPage = position;
                    getData(selecPage);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void getData(final int tense) {
        netApi.getCoupon(Cookies.getUserId(),tense).subscribeOn(Schedulers.io())
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
                        switch (tense){
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
                            case 3:     //已过期
                                overduecoupons.clear();
                                overduecoupons.addAll(coupons);
                                selectFragment = overdueCouponFragment;
                                break;
                            default:
                                break;
                        }/*上面一段代码解析：因为想在进入米券页面时，同时获取到未使用、已使用和已过期三个状态时的数据。所以过去到getdata(1)后再获取getdata(2)..等获取完成后刷新*/
                        selectFragment.refreshUI();
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
        getData(selecPage);
    }
}

