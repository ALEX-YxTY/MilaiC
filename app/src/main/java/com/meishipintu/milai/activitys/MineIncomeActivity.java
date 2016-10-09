package com.meishipintu.milai.activitys;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.meishipintu.milai.R;
import com.meishipintu.milai.adapter.MyGrabRiceLogAdapter;
import com.meishipintu.milai.animes.NumAnim;
import com.meishipintu.milai.application.Cookies;
import com.meishipintu.milai.beans.GrabRiceLog;
import com.meishipintu.milai.netDao.NetApi;
import com.meishipintu.milai.utils.Immersive;
import com.meishipintu.milai.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MineIncomeActivity extends BaseActivity {

    private List<GrabRiceLog> list;
    private MyGrabRiceLogAdapter adapter;
    private NetApi netApi;

    private static final int TYPE_head = 0;//头
    private static final int TYPE_body = 1;//身
    private static final int TYPE_tail = 2;//尾
    private static final int TYPE_ending = 3;//结尾的点

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_mi)
    TextView tvMi;
    @BindView(R.id.recyclerView)
    RecyclerView rv;
    @BindView(R.id.SwipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Immersive.immersive(0x99999999,0,this);
        setContentView(R.layout.activity_income);
        ButterKnife.bind(this);

        tvTitle.setText(R.string.my_coin_mi);
        netApi = NetApi.getInstance();
        list = new ArrayList<>();
        adapter = new MyGrabRiceLogAdapter(MineIncomeActivity.this, list);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(adapter);

        getMi();
        getList();
//        listAdapter = new MyListAdapter(this);
//        listView.setAdapter(listAdapter);
    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        onBackPressed();
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
                        Toast.makeText(MineIncomeActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
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
        netApi.getMiLog(Cookies.getUserId()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<GrabRiceLog>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MineIncomeActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<GrabRiceLog> grabRiceLogs) {
                        Log.i("test", "getinfo");
                        for (GrabRiceLog log : grabRiceLogs) {
                            Log.i("test", "log:" + log.toString());
                        }
                        if (grabRiceLogs.size() > 0 ) {
                            if (mSwipeRefreshingLayout.isRefreshing()) {
                                mSwipeRefreshingLayout.setRefreshing(false);
                            }
                            list.addAll(grabRiceLogs);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

    }

}


