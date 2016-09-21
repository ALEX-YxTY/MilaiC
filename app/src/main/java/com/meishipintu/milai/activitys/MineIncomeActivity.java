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

public class MineIncomeActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{

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

//        int i = 0;
//        list = new ArrayList<Map<String, Object>>();
//        for (i = 0; i <= 13; i++) {
//            if (i == 0 | i == 4 | i == 8) {
//                Map<String, Object> map = new HashMap<String, Object>();
//                map.put("day", "25");
//                map.put("month", i + "月");
//                map.put("tv_time", "10:25");
//                map.put("tv_mi1", "200米");
//                list.add(map);
//            } else if (i == 3 | i == 7 | i == 12 | i == 13) {
//                Map<String, Object> map = new HashMap<String, Object>();
//                list.add(map);
//            } else {
//                Map<String, Object> map = new HashMap<String, Object>();
//                map.put("tv_name", "抢米免费做地铁");
//                map.put("state", "被你抢了");
//                map.put("tv_time", "10:25");
//                map.put("tv_mi", "100米");
//                map.put("iv_photo", R.drawable.task1);
//                list.add(map);
//            }
//        }
    }

    @Override
    public void onRefresh() {
        getList();
    }


//    class MyListAdapter extends BaseAdapter {
//        LayoutInflater minflater;
//
//        public MyListAdapter(Context context) {
//            minflater = LayoutInflater.from(context);
//        }
//
//        @Override
//        public int getCount() {
//            return list.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return position;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder holder ;
//            //如果缓存convertView为空，则需要创建View
//            int currentType = getItemViewType(position);
//            if (currentType == 0) {
//
//                if (convertView == null) {
//                    holder = new ViewHolder();
//                    //根据自定义的Item布局加载布局
//                    convertView = minflater.inflate(R.layout.item_head, null);
//                    holder.day = (TextView) convertView.findViewById(R.id.day);
//                    holder.month = (TextView) convertView.findViewById(R.id.month);
//                    holder.tv_mi1 = (TextView) convertView.findViewById(R.id.tv_mi1);
//                    convertView.setTag(holder);
//                } else {
//                    holder = (ViewHolder) convertView.getTag();
//                    holder.day.setText((String) list.get(position).get("day"));
//                    holder.month.setText((String) list.get(position).get("month"));
//                    holder.tv_mi1.setText((String) list.get(position).get("tv_mi1"));
//                }
//
//            }
//            if (currentType == 1) {
//
//                if (convertView == null) {
//                    holder = new ViewHolder();
//                    //根据自定义的Item布局加载布局
//                    convertView = minflater.inflate(R.layout.item_get_mi_log_body, null);
//                    holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
//                    holder.state = (TextView) convertView.findViewById(R.id.state);
//                    holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
//                    holder.tv_mi = (TextView) convertView.findViewById(R.id.tv_mi);
//                    holder.iv_photo = (ImageView) convertView.findViewById(R.id.iv_photo);
//                    convertView.setTag(holder);
//                } else {
//                    holder = (ViewHolder) convertView.getTag();
//                    holder.iv_photo.setBackgroundResource((Integer) list.get(position).get("iv_photo"));
//                    holder.tv_mi.setText((String) list.get(position).get("tv_mi"));
//                    holder.tv_time.setText((String) list.get(position).get("tv_time"));
//                    holder.state.setText((String) list.get(position).get("state"));
//                    holder.tv_name.setText((String) list.get(position).get("tv_name"));
//                }
//            }
//            if (currentType == 2) {
//                //根据自定义的Item布局加载布局
//                convertView = minflater.inflate(R.layout.item_tail, null);
//            }
//            if (currentType == 3) {
//                //根据自定义的Item布局加载布局
//                convertView = minflater.inflate(R.layout.item_dian, null);
//            }
//            return convertView;
//        }
//
//        public int getItemViewType(int position) {
//            if (position == 0 | position == 4 | position == 8) {
//                return TYPE_head;
//            } else if (position == 3 | position == 7 | position == 12) {
//                return TYPE_tail;
//            } else if (position == 13) {
//                return TYPE_ending;
//            } else {
//                return TYPE_body;
//            }
//
//
//        }
//
//        public int getViewTypeCount() {
//            return 4;
//        }
//    }
//
//    static class ViewHolder {
//        public ImageView iv_photo;
//        public TextView tv_name;
//        public TextView state;
//        public TextView tv_time;
//        public TextView tv_mi;
//        public TextView day;
//        public TextView month;
//        public TextView tv_mi1;
//    }
}


