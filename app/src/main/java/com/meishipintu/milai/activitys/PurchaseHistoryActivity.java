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

//    ListView listView;
//    MyListAdapter listAdapter;
//    ViewHolder holder = null;
//    private static final int TYPE_head = 0;//头
//    private static final int TYPE_body = 1;//身
//    private static final int TYPE_tail = 2;//尾
//    private static final int TYPE_ending = 3;//结尾的点

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
        adapter = new MyConsumRecordAdapter(data, PurchaseHistoryActivity.this);
        rv.setAdapter(adapter);
    }

    private void initData(int page, boolean refreshing) {
        netApi.getConsumeRecord(Cookies.getUserId(), page).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<ConsumeRecordInfo>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(PurchaseHistoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<ConsumeRecordInfo> consumeRecordInfos) {
                        Log.i("test", "list.size:" + consumeRecordInfos.size());
                        data.addAll(consumeRecordInfos);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

//    private List<Map<String, Object>> getData() {
//        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//        for (i = 0; i <= 12; i++) {
//            if (i == 0 | i == 3 | i == 6 | i == 9) {
//                Map<String, Object> map = new HashMap<String, Object>();
//                map.put("tv_day", "25");
//                map.put("tv_month", "7月");
//                map.put("tv_name", "曼度广场");
//                map.put("tv_time", "10:35");
//                list.add(map);
////                <!--年：25：tv_day-->
////                <!--月：07月：tv_month-->
////                <!--店铺名称：店铺名：tv_name-->
////                <!--时间：10.35：tv_time-->
//            } else if (i == 1 | i == 4 | i == 7 | i == 10) {
//                Map<String, Object> map = new HashMap<String, Object>();
//                map.put("tv_money1", "100元");
//                map.put("tv_money2", "20元");
//                map.put("tv_money3", "20元");
//                map.put("tv_money4", "60元");
//                list.add(map);
//            }
////            <!--消费金额：100元：tv_money1-->
////            <!--优惠抵扣：20元：tv_money2-->
////            <!--米抵扣：10元：tv_money3-->
////            <!--实际支付：70元tv_money4-->
//            else {
//                Map<String, Object> map = new HashMap<String, Object>();
//                list.add(map);
//            }
//
//        }
//        return list;
//    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        onBackPressed();
    }


//    static class ViewHolder {
//        public TextView tv_day;
//        public TextView tv_month;
//        public TextView tv_name;
//        public TextView tv_time;
//        public TextView tv_money1;
//        public TextView tv_money2;
//        public TextView tv_money3;
//        public TextView tv_money4;
//    }
//
//    class MyListAdapter extends BaseAdapter {
//        Context context1;
//        LayoutInflater minflater;
//        public MyListAdapter(Context context) {
//            context1=context;
//            minflater = LayoutInflater.from(context1);
//        }
//
//        @Override
//        public int getCount() {
//            return data.size();
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
//            //如果缓存convertView为空，则需要创建View
//            int currentType = getItemViewType(position);
//            if (currentType == 0) {
//
//                if (convertView == null) {
//                    holder = new ViewHolder();
//                    //根据自定义的Item布局加载布局
//                    convertView = minflater.inflate(R.layout.item_recordsofconsumption_head, null);
//                    holder.tv_day = (TextView) convertView.findViewById(R.id.tv_day);
//                    holder.tv_month = (TextView) convertView.findViewById(R.id.tv_month);
//                    holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
//                    holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
//                    convertView.setTag(holder);
//                }else {
//                    holder = (ViewHolder)convertView.getTag();
//                    holder.tv_day.setText((String) data.get(position).get("tv_day"));
//                    holder.tv_month.setText((String) data.get(position).get("tv_month"));
//                    holder.tv_name .setText((String) data.get(position).get("tv_name"));
//                    holder.tv_time .setText((String) data.get(position).get("tv_time"));}
//            }
//            if (currentType == 1) {
//
//                if (convertView == null) {
//                    holder = new ViewHolder();
//                    //根据自定义的Item布局加载布局
//                    convertView = minflater .inflate(R.layout.item_records_of_consume, null);
//                    holder.tv_money1 = (TextView) convertView.findViewById(R.id.tv_money1);
//                    holder.tv_money2 = (TextView) convertView.findViewById(R.id.tv_money2);
//                    holder.tv_money3 = (TextView) convertView.findViewById(R.id.tv_mi_money3);
//                    holder.tv_money4 = (TextView) convertView.findViewById(R.id.tv_money4);
//                    convertView.setTag(holder);
//                } else {
//                    holder = (ViewHolder)convertView.getTag();
//                    holder.tv_money1.setText((String) data.get(position).get("tv_money1"));
//                    holder.tv_money2.setText((String) data.get(position).get("tv_money2"));
//                    holder.tv_money3.setText((String) data.get(position).get("tv_money3"));
//                    holder.tv_money4.setText((String) data.get(position).get("tv_money4"));
//                }
//            }
//            if (currentType == 2) {
//                //根据自定义的Item布局加载布局
//                convertView = minflater.inflate(R.layout.item_tail,null);
//            }
//            if (currentType == 3) {
//                //根据自定义的Item布局加载布局
//                convertView = minflater.inflate(R.layout.item_dian,null);
//            }
//            return convertView;
//        }
//
//        public int getItemViewType(int position) {
//            if(position==0|position==3|position==6|position==9){
//                return  TYPE_head;
//            }
//            else if(position==1|position==4|position==7|position==10) {
//                return TYPE_body;
//            }
//
//            else if(position==12) {
//                return TYPE_ending ;
//            }
//            else  {
//                return TYPE_tail ;
//            }
//
//
//
//        }
//
//        public int getViewTypeCount() {
//            return 4;
//        }
//    }
}


