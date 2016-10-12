package com.meishipintu.milai.activitys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.meishipintu.milai.R;
import com.meishipintu.milai.adapter.MyConsumRecordAdapter;
import com.meishipintu.milai.application.Cookies;
import com.meishipintu.milai.beans.ConsumeRecordInfo;
import com.meishipintu.milai.netDao.NetApi;
import com.meishipintu.milai.utils.Immersive;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PurchaseHistoryActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv)
    RecyclerView rv;

    private List<ConsumeRecordInfo> data;
    private MyConsumRecordAdapter adapter;
    private NetApi netApi;
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Immersive.immersive(0x99999999,0,this);
        setContentView(R.layout.activity_purchase_history);
        ButterKnife.bind(this);
        netApi = NetApi.getInstance();
        tvTitle.setText(R.string.consume);
        data = new ArrayList<>();
        initRecyclerView();
        //初始载入第一页,刷新数据
        initData(1,false);
    }

    private void initRecyclerView() {
        rv.setLayoutManager(new LinearLayoutManager(PurchaseHistoryActivity.this));
        rv.setItemAnimator(new DefaultItemAnimator());
        adapter = new MyConsumRecordAdapter(PurchaseHistoryActivity.this, data);
        rv.setAdapter(adapter);
    }

    private void initData(int page, boolean refreshing) {
        netApi.getConsumeRecord(Cookies.getUserId(), page).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ConsumeRecordInfo>() {
                    @Override
                    public void onCompleted() {
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(PurchaseHistoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(ConsumeRecordInfo consumeRecordInfo) {
                        data.add(consumeRecordInfo);
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


