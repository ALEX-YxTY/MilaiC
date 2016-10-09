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
import com.meishipintu.milai.adapter.MyNoticeAdapter;
import com.meishipintu.milai.beans.Notice;
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

public class NoticeActivity extends BaseActivity {


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv)
    RecyclerView rv;
    private List<Notice> data;
    private MyNoticeAdapter adapter;
    private NetApi netApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Immersive.immersive(0x99999999, 0, this);
        setContentView(R.layout.activity_notice);
        ButterKnife.bind(this);
        netApi = NetApi.getInstance();
        tvTitle.setText(R.string.name_notice);
        data = new ArrayList<>();
        initRecyclerView();//初始化
        initData();
    }

    private void initRecyclerView() {
        rv.setLayoutManager(new LinearLayoutManager(NoticeActivity.this));
        rv.setItemAnimator(new DefaultItemAnimator());
        adapter = new MyNoticeAdapter(data, NoticeActivity.this);
        rv.setAdapter(adapter);
    }

    private void initData() {
        netApi.getNotice()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Notice>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(NoticeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<Notice> Notice) {
                        Log.i("test", "list.size:" + Notice.size());
                        data.addAll(Notice);
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
