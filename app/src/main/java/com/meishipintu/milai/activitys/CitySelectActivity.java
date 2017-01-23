package com.meishipintu.milai.activitys;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.meishipintu.milai.R;
import com.meishipintu.milai.application.Cookies;
import com.meishipintu.milai.netDao.NetApi;
import com.meishipintu.milai.utils.DialogUtils;
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

    private static final int REQUEST_LOCATION_PERMISSION = 100;

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
        locationWapper();
    }

    private void locationWapper() {
        Log.i("test", "locationWapper click");

        int hasLoactionPermission = ContextCompat.checkSelfPermission(this
                , Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasLoactionPermission != PackageManager.PERMISSION_GRANTED) {       //未授权
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this
                    , Manifest.permission.ACCESS_FINE_LOCATION)) {              //系统申请权限框不再弹出
                Log.i("test", "dialog show ," + System.currentTimeMillis());
                DialogUtils.showCustomDialog(this, "本应用需要获取位置权限"
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(CitySelectActivity.this
                                        ,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                                        , REQUEST_LOCATION_PERMISSION);
                                dialog.dismiss();
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest
                    .permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        initAMap();
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

    @Override
    public boolean useSwipeBack() {
        //使用左滑返回
        return false;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //授权通过
                    locationWapper();
                } else {
                    //拒绝授权
                    Toast.makeText(this, "无定位权限，无法获取位置信息，请在系统设置中增加应用的相应授权", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }

    }
}
