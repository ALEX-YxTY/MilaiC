package com.meishipintu.milai.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

import com.meishipintu.milai.R;
import com.meishipintu.milai.activitys.MainActivity;
import com.meishipintu.milai.activitys.WelfareDetailActivity;
import com.meishipintu.milai.adapter.MyWelfareAdapter;
import com.meishipintu.milai.beans.Welfare;
import com.meishipintu.milai.netDao.NetApi;
import com.meishipintu.milai.utils.ConstansUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/8/8.
 */
public class WelfareFragment extends BaseFragment {

    private static final int LOAD_SUCCESS = 1;
    private static final int COMPRESS_BITMAP_OK = 2;
    private static WelfareFragment instance;
    private Handler mhandler;
    private MyWelfareAdapter adapter;
    private NetApi netApi;
    private Intent intent;
    private ArrayList<Welfare> list=new ArrayList<>();
    private byte[] bitmapByte;
    private Bitmap afterCompress;
    private LoggingStatusListener listener;
    private int currentPage = 1;

    private int cityId = 385;       //nj

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (LoggingStatusListener) context;
    }

    //    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case LOAD_SUCCESS:
//                    adapter.notifyDataSetChanged();
//                    break;
//                case COMPRESS_BITMAP_OK:
//                    if (intent != null) {
////                        intent.putExtra("bitmap", afterCompress);
//                        intent.putExtra("bitmap", bitmapByte);
//                        startActivity(intent);
//                    }
//                    break;
//                default:
//                    break;
//            }
//        }
//    };

    @BindView(R.id.rv)
    RecyclerView rv;

    public static WelfareFragment getInstance() {
        if (instance == null) {
            instance = new WelfareFragment();
        }
        return instance;
    }

//
//    @Nullable
//    @Override
//    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_task_welfare, container, false);
//        ButterKnife.bind(this, view);
//        //恢复显示时直接调用adapter
//        if (list == null) {
//            list = new ArrayList<>();
//        }
//        adapter = new MyWelfareAdapter(getContext(), list, new MyWelfareAdapter.OnWelfareItemClickListener() {
//            @Override
//            public void onItemClickListener(int position, Welfare welfare) {
//                //获取屏幕截图
//                View viewCache = getActivity().getWindow().getDecorView();  //  获取屏幕view
//
//                getWindowBitmap(viewCache);
//
//                intent = new Intent(getContext(), WelfareDetailActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("welfare", welfare);
//                intent.putExtras(bundle);
//            }
//        });
//        rv.setLayoutManager(new LinearLayoutManager(getContext()));
//        rv.setItemAnimator(new DefaultItemAnimator());
//        rv.setAdapter(adapter);
//        return view;
//    }

    //获取屏幕截图方法
    private void getWindowBitmap(final View viewCache) {
            long startTime = System.currentTimeMillis();
            // 允许当前窗口保存缓存信息
            viewCache.setDrawingCacheEnabled(true);
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            int widths = display.getWidth();
            int heights = display.getHeight();
            //生成bitmap
            Bitmap bitmapCache = Bitmap.createBitmap(viewCache.getDrawingCache(), 0, 0, widths, heights);
            //压缩bitmap质量
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapCache.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            //压缩图片大小
            ByteArrayInputStream is = new ByteArrayInputStream(baos.toByteArray());
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;     //只读边界
            BitmapFactory.decodeStream(is, null, opts);

            opts.inSampleSize = 4;              //压缩倍率
            opts.inJustDecodeBounds = false;    //改回读图片

            //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
            is = new ByteArrayInputStream(baos.toByteArray());
            afterCompress = BitmapFactory.decodeStream(is, null, opts);
            //压缩bitmap质量
            baos.reset();
            afterCompress.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            //转化为byteArray通过intent传递
            bitmapByte = baos.toByteArray();
            Log.i("test", "coast time:" + (System.currentTimeMillis() - startTime) + "ms");
            mhandler.sendEmptyMessage(COMPRESS_BITMAP_OK);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //恢复显示时不再加载数据

    }

    @Override
    public Fragment getFragment() {
        return WelfareFragment.this;

    }

    @Override
    public void getData(final Handler handler, final int parameter) {
        mhandler=handler;
        switch (parameter){
            case 1:
                currentPage=1;
                break;
            case 2:
                list.clear();
                currentPage=1;
                break;
            case 3:
                currentPage++;
                break;
        }
            netApi = NetApi.getInstance();
            //首次加载只加载第一页
            netApi.getWelfare(cityId, currentPage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<Welfare>>() {
                        @Override
                        public void onCompleted() {
                            Log.e("-----------------------","掉成功了");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("-----------------------","失败了");
                            Toast.makeText(getActivity(), "无网络链接", Toast.LENGTH_SHORT).show();
                            mSwipeRefreshLayout.setRefreshing(false);
                            myProgressBar.setVisibility(View.INVISIBLE);
                            currentPage=1;//页面初始化
                            Log.i("test", "e:" + e.toString());
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNext(List<Welfare> welfares) {
                            if( welfares.size()==0&&parameter==3){
                                Log.e("test3",  welfares.size() + "");
                                currentPage--;
                                Toast.makeText(getContext(), "客官~木有更多的信息咯！", Toast.LENGTH_SHORT).show();
                                mSwipeRefreshLayout.setRefreshing(false);
                                myProgressBar.setVisibility(View.INVISIBLE);
                            }
                            else {
                                list.addAll( welfares);
                                handler.sendEmptyMessage(LOAD_SUCCESS);
                                mSwipeRefreshLayout.setRefreshing(false);
                                myProgressBar.setVisibility(View.INVISIBLE);
                                Log.e("testa", "调成功");
                            }
                        }
                    });
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        adapter = new MyWelfareAdapter(getContext(), list, new MyWelfareAdapter.OnWelfareItemClickListener() {
            @Override
            public void onItemClickListener(int position, Welfare welfare) {
                Log.e("-----------------------","有没有到这");
                //获取屏幕截图
                View viewCache = getActivity().getWindow().getDecorView();  //  获取屏幕view

                getWindowBitmap(viewCache);
                intent = new Intent(getContext(), WelfareDetailActivity.class);
                intent.putExtra("bitmap", bitmapByte);
                //获取登录状态
//                MainActivity mainActivity = (MainActivity) instance.getActivity();
//                intent.putExtra("isLogging", mainActivity.isLogging);
                intent.putExtra("isLogging", listener.getLoggingStatus());

                Bundle bundle = new Bundle();
                bundle.putSerializable("welfare", welfare);
                intent.putExtras(bundle);
                getActivity().startActivityForResult(intent, ConstansUtils.IS_LOGGING);
            }
        });
        return adapter;
    }

    public interface LoggingStatusListener {
        boolean getLoggingStatus();
    }
    public void setUserVisibleHint(boolean isVisibleToUser) {//这个方法是代替fragment.isVisible()的。判断当前显示页面的
        super.setUserVisibleHint(isVisibleToUser);

        if(getUserVisibleHint()) {
            gesture();//开启上拉触发
        } else {
            if(myTouchListener!=null&&getActivity()!=null) {
                ((MainActivity) this.getActivity()).unRegisterMyTouchListener(myTouchListener);
            }

        }
    }

}