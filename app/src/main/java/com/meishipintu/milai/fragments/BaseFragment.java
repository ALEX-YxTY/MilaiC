package com.meishipintu.milai.fragments;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.meishipintu.milai.R;
import com.meishipintu.milai.activitys.MainActivity;
import com.meishipintu.milai.receiver.AutoReceiver;
import com.meishipintu.milai.utils.ConstansUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Administrator on 2016/9/6 0006.
 */
public abstract class BaseFragment extends Fragment {

    float downX = 0;    //按下时 的X坐标
    float downY = 0; //按下时 的Y坐标

    private boolean firstRefreshing = true;//避免viewPage切换过程中多次刷新
    public MainActivity.MyTouchListener myTouchListener;
    public LinearLayoutManager layoutManager;


    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.SwipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.progressBar)
    ProgressBar myProgressBar;

    private Fragment fragment;
    private RecyclerView.Adapter adapter;

    //抽象方法
    public abstract Fragment getFragment();

    public abstract void getData(Handler handler, int parameter);

    public abstract RecyclerView.Adapter getAdapter();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstansUtils.LOAD_SUCCESS:

                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_welfare, container, false);
        ButterKnife.bind(this, view);
        fragment = getFragment();
        adapter = getAdapter();
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setLayoutManager(new LinearLayoutManager(fragment.getActivity()));
        BaseFragment.this.isVisible();
        rv.setAdapter(adapter);
        if (firstRefreshing) {
            refreshType(ConstansUtils.REFRESH);
            firstRefreshing = false;
        }




//----------------------分割线已下是设置下拉的---------------------------------
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);//设置颜色
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshType(ConstansUtils.REFRESH);
            }
        });
//-----------------------------分割线已上是设置下拉的--------------------------------


        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        setBackGround(view);
        return view;
    }

    public void setBackGround(View view) {
        view.setBackgroundColor(getResources().getColor(R.color.background_task));
    }

    public void refreshType(int type) {
        getData(handler, type);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(layoutManager);

//        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {/*判断当前显示是否为最后一页*/
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                int visible = layoutManager.getChildCount();
//                Log.i("visible", visible + "");
//                int total = layoutManager.getItemCount();
//                int past = layoutManager.findFirstCompletelyVisibleItemPosition();
//                if ((visible + past) >= total) {
//
////                        Toast.makeText(getContext(), "111111", Toast.LENGTH_LONG).show();
//                    b=1;
//                    Log.e("b", b+ "");
//                }
//                else{
//                    b=0;
//                }
//            }
//        });

    }

    public void gesture() {/*用来判断手指滑动，是否是上拉*/
        myTouchListener = new MainActivity.MyTouchListener() {
            @Override
            public void onTouchEvent(MotionEvent event) {
                int visible = layoutManager.getChildCount();
                Log.i("visible", visible + "");
                int total = layoutManager.getItemCount();
                int past = layoutManager.findFirstCompletelyVisibleItemPosition();
                // 处理手势事件
                float x = event.getX();
                float y = event.getY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //将按下时的坐标存储
                        downX = x;
                        downY = y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.e("Tag", "=======抬起时X：" + x);
                        Log.e("Tag", "=======抬起时Y：" + y);

                        //获取到距离差
                        float dx = x - downX;
                        float dy = downY - y;
                        Log.e("Tag1", "=======抬起时dX：" + dx);
                        Log.e("Tag1", "=======抬起时dY：" + dy);
                        //防止是按下也判断
                        if (Math.abs(dx) < 200 & dy > 200) {
                            if ((visible + past) >= total) {
                                //fragment.isVisible()这个方法没用，会一直都是ture;
                                if (fragment instanceof TaskFragment) {
                                    myProgressBar.setVisibility(View.VISIBLE);
                                    //fragment instanceof TaskFragment判断是谁调用的。
                                    TaskFragment.getInstance().refreshType(ConstansUtils.LOAD_MORE);
                                    break;

                                } else if (fragment instanceof WelfareFragment) {
                                    myProgressBar.setVisibility(View.VISIBLE);
                                    WelfareFragment.getInstance().refreshType(ConstansUtils.LOAD_MORE);
                                    break;
                                }
                            }
                        }

                        break;
                }
            }

        };
        // 将myTouchListener注册到分发列表
        ((MainActivity) this.getActivity()).registerMyTouchListener(myTouchListener);

    }


}


