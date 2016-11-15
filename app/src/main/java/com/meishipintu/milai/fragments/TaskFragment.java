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
        adapter = new MyTaskAdapter(list, getActivity(), new MyTaskAdapter.TaskOnItemClickListener() {
            @Override
            public void onItemClick(View view, Task task) {

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
                               break;
                           } else {
                               url = task.getType_detail() + "/uid/" + Cookies.getUserId();
                               Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
                               intent.putExtra("detail", url);
                               startActivity(intent);
                               break;
                           }
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