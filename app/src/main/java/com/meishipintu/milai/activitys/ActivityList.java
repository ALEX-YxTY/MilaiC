package com.meishipintu.milai.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.meishipintu.milai.R;
import com.meishipintu.milai.adapter.ActivityListAdapter;
import com.meishipintu.milai.application.Cookies;
import com.meishipintu.milai.beans.Task;
import com.meishipintu.milai.netDao.NetApi;
import com.meishipintu.milai.utils.ConstansUtils;
import com.meishipintu.milai.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ActivityList extends BaseActivity {

    private List<Task> dataList;
    private int type;               //type=1,活动列表，type=2，收藏列表
    private NetApi netApi;
    private ActivityListAdapter adapter;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv)
    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_list);
        ButterKnife.bind(this);
        type = getIntent().getIntExtra("type", 1);
        if (type == 1) {
            tvTitle.setText("活动列表");
        } else {
            tvTitle.setText("收藏列表");
        }
        netApi = NetApi.getInstance();
        initRv();
    }

    private void initRv() {
        dataList = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());
        adapter = new ActivityListAdapter(dataList, this);
        rv.setAdapter(adapter);
        getData();
    }

    private void getData() {
        if (type == 1) {            //活动列表
            netApi.getTaskNew(Cookies.getUserId(), 2)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Task>() {
                        @Override
                        public void onCompleted() {
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(ConstansUtils.APP_NAME, "error:" + e.getMessage());
                            ToastUtils.show(ActivityList.this, "网络加载错误，请稍后重试");
                        }

                        @Override
                        public void onNext(Task task) {
                            dataList.add(task);
                        }
                    });
        } else {                    //收藏列表
            netApi.getMyLikes(Cookies.getUserId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<Task>>() {
                        @Override
                        public void onCompleted() {
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(ConstansUtils.APP_NAME, "error:" + e.getMessage());
                            ToastUtils.show(ActivityList.this, "网络加载错误，请稍后重试");
                        }

                        @Override
                        public void onNext(List<Task> tasks) {
                            dataList.clear();
                            dataList.addAll(tasks);
                        }
                    });
        }
    }

    @OnClick({R.id.iv_back,R.id.iv_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_search:
                Intent intent = new Intent(this, SearchActivity.class);
                intent.putExtra("type", type);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_in_transy_down, 0);
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
        overridePendingTransition(0,R.anim.anim_out_transy);
    }

    @Override
    public boolean useSwipeBack() {
        return true;
    }

}
