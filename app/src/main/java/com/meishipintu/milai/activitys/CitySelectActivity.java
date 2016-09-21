package com.meishipintu.milai.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.meishipintu.milai.R;
import com.meishipintu.milai.application.Cookies;
import com.meishipintu.milai.netDao.NetApi;
import com.meishipintu.milai.utils.Immersive;
import com.meishipintu.milai.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import rx.Observable;
import rx.Subscriber;

public class CitySelectActivity extends BaseActivity {

    private NetApi netApi;
    private List<Map<String,String>> cityList;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //储存当前定位城市
                    Cookies.setCity(aMapLocation.getCity());
                    //可在其中解析amapLocation获取相应内容。
                    tvLocalCity.setText(aMapLocation.getCity());
                    //销毁定位客户端，同时销毁本地定位服务。
                    mLocationClient.onDestroy();
                }else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError","location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                    mLocationClient.onDestroy();

                }
            }

        }
    };

    @BindView(R.id.local_city)
    TextView tvLocalCity;
    @BindView(R.id.lv)
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_select);
        ButterKnife.bind(this);
        initLv();
        initAMap();
    }

    private void initAMap() {
        if (!StringUtils.isNullOrEmpty(Cookies.getCity())) {
            tvLocalCity.setText(Cookies.getCity());
        }
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        prepareLocationOption();

        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();

    }

    //初始化定位options
    private void prepareLocationOption() {
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Battery_Saving，仅使用网络定位，低功耗模式。
        //设置定位模式为AMapLocationMode.Hight_Accuracy，使用网络定位和GPS，高功耗模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        /*
            获取最近3s内精度最高的一次定位结果：
            设置setOnceLocationLatest(boolean b)接口为true
            启动定位时SDK会返回最近3s内精度最高的一次定位结果。
            如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
         */
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
    }

    private void initLv() {
        cityList = new ArrayList<>();
        netApi = NetApi.getInstance();
        //此处应从网络获取
        final Observable<String> cityObservable = Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        String[] arr = getResources().getStringArray(R.array.hot_city);
                        for (String city : arr) {
                            subscriber.onNext(city);
                        }
                        subscriber.onCompleted();
                    }
                }
        );
        cityObservable.subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                SimpleAdapter adapter = new SimpleAdapter(CitySelectActivity.this, cityList
                        , R.layout.item_city, new String[]{"name"}, new int[]{R.id.city});
                lv.setAdapter(adapter);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent();
                        intent.putExtra("select_city", cityList.get(position).get("name"));
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String strings) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("name", strings);
                cityList.add(map);
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.local_city})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.local_city:
                if (!tvLocalCity.getText().toString().equals(getResources().getString(R.string.locating))) {
                    Intent intent = new Intent();
                    intent.putExtra("select_city", tvLocalCity.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //退出时销毁客户端
        mLocationClient.onDestroy();
    }

}
