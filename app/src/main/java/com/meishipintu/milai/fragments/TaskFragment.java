package com.meishipintu.milai.fragments;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.meishipintu.milai.R;
import com.meishipintu.milai.activitys.MainActivity;
import com.meishipintu.milai.activitys.TaskDetailActivity;
import com.meishipintu.milai.adapter.MyTaskAdapter;
import com.meishipintu.milai.application.Cookies;
import com.meishipintu.milai.application.RxBus;
import com.meishipintu.milai.beans.Task;
import com.meishipintu.milai.netDao.NetApi;
import com.meishipintu.milai.utils.ConstansUtils;
import com.meishipintu.milai.utils.StringUtils;
import com.meishipintu.milai.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/8/8.
 */
public class TaskFragment extends BaseFragment  {

    private static final int LOAD_SUCCESS = 1;
    private static TaskFragment instance;
    private MyTaskAdapter adapter;
    private ArrayList<Task> list=new ArrayList<>();
    private NetApi netApi;
    private int currentPage = 1;
    private int cityId = 385;     //nj



    public static TaskFragment getInstance() {
        if (instance == null) {
            instance = new TaskFragment();
        }
        return instance;
    }


    @Override
    public Fragment getFragment() {
        return TaskFragment.this;
    }

    /*
        parameter 用于判断GETDATA是从那调用的  parameter==1是初始化页面调用的
        ，parameter==2是下拉刷新时候调用的，parameter==3是上拉加载时候调用的
    */
    @Override
    public void getData(final Handler handler , final int parameter) {
        switch (parameter){
            case 1:
                currentPage=1;
                mSwipeRefreshLayout.setRefreshing(true);
                break;
            case 2:
                list.clear();
                currentPage=1;
                break;
            case 3:
                currentPage++;
                break;
        }

        if (list == null) {
            list = new ArrayList<>();
        }
            netApi = NetApi.getInstance();
            //首次加载只加载第一页
            netApi.getTask(cityId, currentPage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<Task>>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getActivity(), "无网络链接", Toast.LENGTH_SHORT).show();
                            mSwipeRefreshLayout.setRefreshing(false);
                            myProgressBar.setVisibility(View.INVISIBLE);
                            currentPage=1;//页面初始化
                        }

                        @Override
                        public void onNext(List<Task> tasks) {
                            if(tasks.size()==0&&parameter==3){
                                currentPage--;
//                                Toast.makeText(getContext(), "客官~木有更多的信息咯！", Toast.LENGTH_SHORT).show();
                                mSwipeRefreshLayout.setRefreshing(false);
                                myProgressBar.setVisibility(View.INVISIBLE);
                            }
                            else {
                                list.addAll(tasks);
                                handler.sendEmptyMessage(LOAD_SUCCESS);
                                mSwipeRefreshLayout.setRefreshing(false);
                                myProgressBar.setVisibility(View.INVISIBLE);
                                Log.e("test3", list.size() + "");
                            }
                        }
                    });
        Log.e("test4", currentPage+"");
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        adapter = new MyTaskAdapter(list, getActivity(), new MyTaskAdapter.TaskOnItemClickListener() {
            @Override
            public void onItemClick(View view, String detail, String shareTitle) {
                if (!StringUtils.isNullOrEmpty(detail) && detail.startsWith("http")) {
                    if (!StringUtils.isNullOrEmpty(Cookies.getUserId())) {
                        Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
                        intent.putExtra("detail", detail);
                        intent.putExtra("shareTitle", shareTitle);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), R.string.login_please, Toast.LENGTH_SHORT).show();
                        RxBus.getDefault().send(ConstansUtils.LOGIN_FIRST);
                    }
                } else {
                    ToastUtils.show(getActivity(), "即将上线");
                }
            }
        });
        return adapter;

    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(getUserVisibleHint()) {
            gesture();//开启上拉触发
        } else {
            if(myTouchListener!=null&&getActivity()!=null) {
                ((MainActivity) getActivity()).unRegisterMyTouchListener(myTouchListener);//关闭上拉触发
            }
        }
    }

}