package com.meishipintu.milai.activitys;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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

public class SearchActivity extends BaseActivity {

    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.et_search)
    EditText etSearch;

    private NetApi netApi;
    private ActivityListAdapter adapter;
    private int type;               //type=1,活动列表，type=2，收藏列表
    private List<Task> searchList;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:             //有搜索数据
                    if (adapter == null) {
                        adapter = new ActivityListAdapter(searchList, SearchActivity.this);
                        rv.setAdapter(adapter);
                    }
                    adapter.notifyDataSetChanged();
                    rv.setVisibility(View.VISIBLE);
                    break;
                case 2:             //无搜索数据
                    rv.setVisibility(View.GONE);
                    ToastUtils.show(SearchActivity.this, "未找到相应的活动~");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        netApi = NetApi.getInstance();
        type = getIntent().getIntExtra("type", 1);
        init();
    }

    //初始化SearchView
    private void init() {
        searchList = new ArrayList<>();
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setLayoutManager(new LinearLayoutManager(this));
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if ("".equals(etSearch.getText().toString())) {
                        //输入为空，返回全部活动
                        netApi.getTaskNew(Cookies.getUserId(), 2)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<Task>() {
                                    @Override
                                    public void onCompleted() {
                                        handler.sendEmptyMessage(1);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        ToastUtils.show(SearchActivity.this, R.string.network_error);
                                        Log.d(ConstansUtils.APP_NAME, "error:" + e.getMessage());
                                    }

                                    @Override
                                    public void onNext(Task task) {
                                        searchList.add(task);
                                    }

                                    @Override
                                    public void onStart() {
                                        super.onStart();
                                        searchList.clear();
                                    }
                                });
                    } else {
                        netApi.searchActivty(type, etSearch.getText().toString(), Cookies.getUserId())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<List<Task>>() {
                                    @Override
                                    public void onCompleted() {
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        ToastUtils.show(SearchActivity.this, R.string.network_error);
                                        Log.d(ConstansUtils.APP_NAME, "error:" + e.getMessage());
                                    }

                                    @Override
                                    public void onNext(List<Task> tasks) {
                                        searchList.clear();
                                        if (tasks.size() == 0) {
                                            //无搜索数据
                                            handler.sendEmptyMessage(2);
                                        } else {
                                            //有搜索数据
                                            searchList.addAll(tasks);
                                            handler.sendEmptyMessage(1);
                                        }
                                    }
                                });
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean useSwipeBack() {
        return true;
    }

    @OnClick(R.id.tv_cancel)
    public void onClick() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.anim_out_transy_up);
    }
}
