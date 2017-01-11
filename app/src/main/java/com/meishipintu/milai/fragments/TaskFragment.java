package com.meishipintu.milai.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.meishipintu.milai.R;
import com.meishipintu.milai.activitys.TaskDetailActivity;
import com.meishipintu.milai.adapter.MyTaskAdapter;
import com.meishipintu.milai.application.Cookies;
import com.meishipintu.milai.application.RxBus;
import com.meishipintu.milai.beans.Task;
import com.meishipintu.milai.netDao.NetApi;
import com.meishipintu.milai.service.MyService;
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
public class TaskFragment extends BaseFragment implements MyTaskAdapter.TaskAlarmListener {

    private static TaskFragment instance;
    private MyTaskAdapter adapter;
    private ArrayList<Task> list=new ArrayList<>();
    private NetApi netApi;
    private int currentPage = 1;
    private int cityId = 385;     //nj
    private MyService mService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        startAndBindService();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

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
            case ConstansUtils.REFRESH:
                if (!mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
                list.clear();
                currentPage=1;
                break;
            case ConstansUtils.LOAD_MORE:
                currentPage++;
                break;
        }

        if (list == null) {
            list = new ArrayList<>();
        }
            netApi = NetApi.getInstance();
            //首次加载只加载第一页
            netApi.getTask(cityId, currentPage,Cookies.getUserId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<Task>>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i("test", e.getMessage());
                            ToastUtils.show(getActivity(), "获取信息失败，请检查网络");
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
                                handler.sendEmptyMessage(ConstansUtils.LOAD_SUCCESS);
                                mSwipeRefreshLayout.setRefreshing(false);
                                myProgressBar.setVisibility(View.INVISIBLE);
                                Log.e("test3", list.size() + "");
                            }
                        }
                    });
        Log.i("test4", currentPage+"");
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        adapter = new MyTaskAdapter(list,getActivity(),this, new MyTaskAdapter.TaskOnItemClickListener() {
            @Override
            public void onItemClick(View view, Task task) {
                Log.i("test", "task is :" + task.toString());

               if (StringUtils.isNullOrEmpty(task.getType_detail())) {

                   ToastUtils.show(getActivity(), "即将上线");

               } else {
                   String url;
                   switch (Integer.valueOf(task.getType())) {
                       case 3:
                           ToastUtils.show(getActivity(), task.getType_detail());
                           break;
                       case 1:
                           if (StringUtils.isNullOrEmpty(Cookies.getUserId())) {
                                Toast.makeText(getActivity(), R.string.login_please, Toast.LENGTH_SHORT).show();
                                RxBus.getDefault().send(ConstansUtils.LOGIN_FIRST);
                           } else {
                               url = task.getType_detail() + "/uid/" + Cookies.getUserId();
                               Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
                               intent.putExtra("detail", url);
                               startActivity(intent);
                           }
                           break;
                       case 2:
                               url = task.getType_detail();
                               Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
                               intent.putExtra("detail", url);
                               startActivity(intent);
                           break;
                   }
               }
            }

        });
        return adapter;

    }

    public void startAndBindService(){
        //启动并绑定servie
        Intent serviceIntent = new Intent(getActivity(), MyService.class);
        getActivity().startService(serviceIntent);
        getActivity().bindService(serviceIntent, connection, Context. BIND_AUTO_CREATE);
    }

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("test", "service disconnected");
            mService = null;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("test","service connected");
            MyService.MyBind myBind = (MyService.MyBind) service;
            mService = myBind.getMyService();
        }
    };

    @Override
    public void setAlarm(Task task) {
        if (mService != null) {
            mService.setAlarm(task);
            Log.i("test", "setAlarm is set");
        }
    }

    @Override
    public void cancelAlarm(Task task) {
        if (mService != null) {
            mService.cancelAlarm(task);
            Log.i("test", "setAlarm is canceled");
        }
    }
}