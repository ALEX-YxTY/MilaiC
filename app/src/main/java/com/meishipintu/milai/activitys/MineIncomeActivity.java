package com.meishipintu.milai.activitys;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.meishipintu.milai.R;
import com.meishipintu.milai.adapter.CouponFragmentAdapter;
import com.meishipintu.milai.adapter.MyConsumRecordAdapter;
import com.meishipintu.milai.adapter.MyExchangeRiceLogAdapter;
import com.meishipintu.milai.adapter.MyGrabRiceLogAdapter;
import com.meishipintu.milai.animes.NumAnim;
import com.meishipintu.milai.application.Cookies;
import com.meishipintu.milai.beans.ConsumeRecordInfo;
import com.meishipintu.milai.beans.ExchangeRiceLog;
import com.meishipintu.milai.beans.GrabRiceLog;
import com.meishipintu.milai.fragments.MyIncomeFragment;
import com.meishipintu.milai.netDao.NetApi;
import com.meishipintu.milai.utils.Immersive;
import com.meishipintu.milai.utils.StringUtils;
import com.meishipintu.milai.utils.ToastUtils;
import com.meishipintu.milai.views.LoadingProgressDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MineIncomeActivity extends BaseActivity {

    private List<GrabRiceLog> grabList;
    private MyGrabRiceLogAdapter grabRiceAdapter;
    private List<ExchangeRiceLog> consumeList;
    private MyExchangeRiceLogAdapter consumeAdapter;
    private MyIncomeFragment grabRiceFragment, consumeFragment;

    private NetApi netApi;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_mi)
    TextView tvMi;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Immersive.immersive(0x99999999, 0, this);
        setContentView(R.layout.activity_income);
        ButterKnife.bind(this);

        tvTitle.setText(R.string.my_coin_mi);
        netApi = NetApi.getInstance();

        initFragment();
        initTabAndViewPager();
        getMi();
        getList();

    }

    private void initFragment() {
        grabList = new ArrayList<>();
        consumeList = new ArrayList<>();

        grabRiceAdapter = new MyGrabRiceLogAdapter(this, grabList);
        grabRiceFragment = new MyIncomeFragment();
        Bundle bundleGrab = new Bundle();
        bundleGrab.putSerializable("adapter", grabRiceAdapter);
        grabRiceFragment.setArguments(bundleGrab);

        consumeAdapter = new MyExchangeRiceLogAdapter(this, consumeList);
        consumeFragment = new MyIncomeFragment();
        Bundle bundleConsume = new Bundle();
        bundleConsume.putSerializable("adapter", consumeAdapter);
        consumeFragment.setArguments(bundleConsume);
    }

    private void initTabAndViewPager() {
        viewPager.setCurrentItem(0);
        CouponFragmentAdapter pagerAdapter = new CouponFragmentAdapter(getSupportFragmentManager());

        pagerAdapter.addFragment(grabRiceFragment,getResources().getString(R.string.grab_log));
        pagerAdapter.addFragment(consumeFragment, getResources().getString(R.string.consume));

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
    }

    //获取米数
    public void getMi() {
        netApi.getMi(Cookies.getUserId()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MineIncomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(String string) {
                        if (!StringUtils.isNullOrEmpty(string)) {
                            NumAnim.startAnim(tvMi, Float.valueOf(string));
                        }
                    }
                });
    }

    //获取抢米记录
    private void getList() {
        final LoadingProgressDialog dialog = new LoadingProgressDialog(this);
        dialog.show();
        Observable getMiLog = netApi.getMiLog(Cookies.getUserId());
        Observable getConsume = netApi.getExchangeLog(Cookies.getUserId());
        Observable.merge(getMiLog,getConsume).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber() {
                    @Override
                    public void onCompleted() {
                        dialog.dismiss();
                        grabRiceAdapter.notifyDataSetChanged();
                        consumeAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dialog.dismiss();
                        ToastUtils.show(MineIncomeActivity.this, e.getMessage());
                    }

                    @Override
                    public void onNext(Object o) {
                        if (o instanceof GrabRiceLog) {
                            grabList.add((GrabRiceLog) o);
                        } else if (o instanceof ExchangeRiceLog) {
                            consumeList.add((ExchangeRiceLog) o);
                        }
                    }
                });
    }

    //开启左滑返回功能
    @Override
    public boolean useSwipeBack() {
        if (viewPager.getCurrentItem() == 0) {
            return true;
        }
        return super.useSwipeBack();
    }


    @OnClick(R.id.iv_back)
    public void onClick() {
        onBackPressed();
    }
}


