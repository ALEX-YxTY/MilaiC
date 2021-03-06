package com.meishipintu.milai.activitys;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.meishipintu.milai.R;
import com.meishipintu.milai.adapter.MyFragmentPageAdapter;
import com.meishipintu.milai.application.Cookies;
import com.meishipintu.milai.application.RxBus;
import com.meishipintu.milai.beans.AppInfo;
import com.meishipintu.milai.beans.Uid;
import com.meishipintu.milai.beans.UserDetailInfo;
import com.meishipintu.milai.beans.UserInfo;
import com.meishipintu.milai.fragments.LoginFragment;
import com.meishipintu.milai.fragments.MineFragment;
import com.meishipintu.milai.fragments.TaskFragment;
import com.meishipintu.milai.fragments.WelfareFragment;
import com.meishipintu.milai.netDao.NetApi;
import com.meishipintu.milai.tasks.MyAsy;
import com.meishipintu.milai.utils.ConstansUtils;
import com.meishipintu.milai.utils.DialogUtils;
import com.meishipintu.milai.utils.Immersive;
import com.meishipintu.milai.utils.StringUtils;
import com.meishipintu.milai.utils.SystemUtils;
import com.meishipintu.milai.utils.ToastUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements WelfareFragment.LoggingStatusListener{

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final int REQUEST_STORAGE_PERMISSION = 300;

    private ArrayList<Fragment> fragmentList;
    private MyFragmentPageAdapter adapter;
    private NetApi netApi;
    private long exitTime = 0L;         //标注点击返回按钮的时间，初始值为0

    public boolean isLogging = false;       //判断用户是否登录中

    private TaskFragment taskFragment;
    private WelfareFragment welfareFragment;
    private LoginFragment loginFragment;
    private MineFragment mineFragment;
    private Subscription rxBusSubscription;      //标注和消息总线的连接情况

    private String fileDir;
    private String versionName;

    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.ll_location)
    LinearLayout llLocation;
    @BindView(R.id.iv_scan)
    ImageView ivScan;
    @BindView(R.id.iv_setting)
    ImageView ivSetting;
    @BindView(R.id.fragment_viewPage)
    ViewPager viewPager;
    @BindView(R.id.rg_tab)
    RadioGroup rgTab;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    @OnClick(R.id.ll_location)
    void selectCity() {
        //选择城市
        startActivityForResult(new Intent(MainActivity.this, CitySelectActivity.class)
                , ConstansUtils.SELECT_CITY);
    }

    @OnClick(R.id.iv_scan)
    void getScaner() {
        cameraWapper();
    }

    //请求相机权限包装方法
    private void cameraWapper() {

        Log.i("test", "cameraWapper click");

        int hasLoactionPermission = ContextCompat.checkSelfPermission(this
                , Manifest.permission.CAMERA);
        if (hasLoactionPermission != PackageManager.PERMISSION_GRANTED) {       //未授权
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this
                    , Manifest.permission.CAMERA)) {                            //系统申请权限框不再弹出
                Log.i("test", "dialog show ," + System.currentTimeMillis());
                DialogUtils.showCustomDialog(this, "本应用需要获取相机权限"
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this
                                        ,new String[]{Manifest.permission.CAMERA}
                                        , REQUEST_CAMERA_PERMISSION);
                                dialog.dismiss();
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest
                    .permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return;
        }
        startScan();
    }

    private void startScan() {
        //启动扫描页面
        Intent intent = new Intent(MainActivity.this, CaptureHanlderActivity.class);
        intent.putExtra("CHECK_CODE", 0);       //0 - 从主页启动
        startActivityForResult(intent, ConstansUtils.SCAN_MAIN);
    }

    @OnClick(R.id.iv_setting)
    void goSetting() {
        //启动设置页面
        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
        startActivityForResult(intent,ConstansUtils.LOGGING_SITUATION);
    }

    @OnClick(R.id.rb_task)
    void getTask(){
        //进入任务fragment
        viewPager.setCurrentItem(0);
    }

    @OnClick(R.id.rb_mine)
    void myCenter(){
        //进入个人中心fragment
        viewPager.setCurrentItem(2);
    }

    @OnClick(R.id.rb_welfare)
    void welfare(){
        //进入福利fragment
        viewPager.setCurrentItem(1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Immersive.immersive(0x99999999,0,this);//沉浸式状态栏
        netApi = NetApi.getInstance();
        initUI();
        checkVersion();
        //注册时间监听RxBus
        rxBusSubscription = RxBus.getDefault().getObservable(Integer.class).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                if (integer == ConstansUtils.LOGIN_FIRST) {
                    selectFragment(2);          //跳转登录界面
                }
            }
        });
        //TODO 测试
        //判断是否是第一次登陆APP
        if (Cookies.getShowGuide()) {
            startActivity(new Intent(MainActivity.this, GuideActivity.class));
        }
    }

    private void initUI() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        checkLogging();
    }

    private void checkVersion() {
        netApi.getSystemInfo(1).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AppInfo>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("test", "error:" + e.getMessage());
                    }

                    @Override
                    public void onNext(final AppInfo appInfo) {
                        Log.i("test", "appinfo:" + appInfo.toString());
                        try {
                            if (appInfo.getApp_version() > SystemUtils.getVersionCode(MainActivity.this)) {
                                AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("发现新版本");
                                builder.setMessage("最新版本：" + appInfo.getApp_version_name()
                                        + "\n更新内容：" + appInfo.getApp_update_desc());
                                builder.setPositiveButton("现在升级", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        fileDir = appInfo.getApp_file();
                                        versionName = appInfo.getApp_version_name();
                                        downloadWapper();
                                    }
                                });
                                builder.setNegativeButton("稍后再说", null);
                                builder.show();
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //申请读写sd卡权限包装方法
    private void downloadWapper() {
        int hasStoragePermission = ContextCompat.checkSelfPermission(this
                , android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasStoragePermission != PackageManager.PERMISSION_GRANTED) {        //未授权
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this
                    , android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {    //系统申请权限框不再弹出
                DialogUtils.showCustomDialog(this, "本应用需要获取读写内存卡权限"
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android
                                        .Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
                                dialog.dismiss();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                return;
            }
            //系统框弹出时直接申请
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{android
                    .Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_STORAGE_PERMISSION);
            return;
        }
        new MyAsy(MainActivity.this,versionName).execute(fileDir);
    }

    //判断当前uid账户是否存在
    private void checkLogging() {
        if (StringUtils.isNullOrEmpty(Cookies.getUserId())) {
            //默认状态载入fragment
            Cookies.clearUserInfo();
            initFragment();
        } else {
            netApi.getUserDetailInfo(new Uid(Cookies.getUserId()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<UserDetailInfo>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i("test", "error:"+e.getMessage());
                            //默认状态载入fragment
                            Cookies.clearUserInfo();
                            initFragment();
                        }

                        @Override
                        public void onNext(UserDetailInfo userDetailInfo) {
                            if (!StringUtils.isNullOrEmpty(userDetailInfo.getTel())) {       //当前uid可用
                                //判定此账号可登录，并修改当前状态
                                isLogging = true;
                            } else {
                                //uid不可用，擦除储存信息
                                Cookies.clearUserInfo();
                            }
                            initFragment();
                        }
                    });
        }
    }

    private void initFragment() {
        taskFragment = TaskFragment.getInstance();
        welfareFragment = WelfareFragment.getInstance();
        fragmentList = new ArrayList<>();
        fragmentList.add(taskFragment);
        fragmentList.add(welfareFragment);
        if (isLogging) {
            mineFragment = MineFragment.getInstance();
            fragmentList.add(mineFragment);
            //绑定Jpush
            JPushInterface.setAliasAndTags(this, Cookies.getUserId(), null);
        } else {
            loginFragment = LoginFragment.getInstance();
            fragmentList.add(loginFragment);

        }
        //设置储存的离线fragment数量
        //viewPager.setOffscreenPageLimit(3);
        adapter = new MyFragmentPageAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                clickRgTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        //设置初始情况
        selectFragment(0);
    }

    //选择页面并调整radioGroup
    private void selectFragment(int position) {
        viewPager.setCurrentItem(position);
        clickRgTab(position);
    }

    private void clickRgTab(int position) {
        switch (position) {
            case 0:     //米来
                rgTab.check(R.id.rb_task);
                optionVisible(false, true, true);
                tvTitle.setText(R.string.app_name);
                break;
            case 1:     //用米地带
                rgTab.check(R.id.rb_welfare);
                optionVisible(false, true, true);
                tvTitle.setText(R.string.welfare);
                break;
            case 2:     //登录|我的米来
                rgTab.check(R.id.rb_mine);
                if (StringUtils.isNullOrEmpty(Cookies.getUserId())||!Cookies.getAutoLogin()) {
                    tvTitle.setText(R.string.login);
                    optionVisible(false, false, false);

                } else {
                    tvTitle.setText(R.string.mine);
                    optionVisible(true, false, false);
                }
                break;
        }
    }

    //设置选项的显示与否
    private void optionVisible(boolean b1, boolean b2, boolean b3) {
        ivSetting.setVisibility(b1 ? View.VISIBLE:View.GONE);
        ivScan.setVisibility(b2 ? View.VISIBLE : View.GONE);
        llLocation.setVisibility(b3 ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i("test", "requestCode:" + requestCode + ",resultCode:" + resultCode);
        switch (requestCode) {
            case ConstansUtils.LOGGING_SITUATION:
                if (resultCode == ConstansUtils.LOG_OUT) {
                    Log.i("test", "logout!");
                    //退出登录
                    fragmentList.remove(mineFragment);
                    mineFragment = null;
                    if (loginFragment == null) {
                        loginFragment = LoginFragment.getInstance();
                    }
                    fragmentList.add(loginFragment);
                    viewPager.removeAllViews();
                    adapter.notifyDataSetChanged();
                    //擦除缓存
                    Cookies.clearUserInfo();
                    //刷新首页的数据
                    taskFragment.refreshType(ConstansUtils.REFRESH);
                    //解绑Jpush
                    JPushInterface.setAliasAndTags(this, "", null);

                    //还原登录状态
                    isLogging = false;
                    //还原显示
                    ivSetting.setVisibility(View.GONE);
                    //还原标题
                    tvTitle.setText(R.string.login);
                } else if (resultCode == ConstansUtils.LOG_IN){
                    //登陆成功
                    Bundle bundle = data.getExtras();
                    UserInfo userInfo = (UserInfo) bundle.get("user_info");
//                    boolean savePassword = data.getBooleanExtra("save_password", true);
                    if (mineFragment == null) {
                        mineFragment = MineFragment.getInstance();
                    }
                    fragmentList.remove(loginFragment);
                    if (!fragmentList.contains(mineFragment)) {
                        fragmentList.add(mineFragment);
                    }
                    loginFragment = null;
                    viewPager.removeAllViews();
                    adapter.notifyDataSetChanged();
                    //写入缓存
                    Cookies.clearUserInfo();
                    Cookies.setUserInfo(userInfo);
                    //刷新首页的数据
                    taskFragment.refreshType(ConstansUtils.REFRESH);
//                    if (!savePassword) {
//                        Cookies.setAutoLogin(false);
//                    }
                    //绑定Jpush
                    JPushInterface.setAliasAndTags(this, Cookies.getUserId(), null);

                    //刷新登录状态
                    isLogging = true;
                    ivSetting.setVisibility(View.VISIBLE);
                    tvTitle.setText(R.string.mine);
                }
                break;
            case ConstansUtils.SCAN_MAIN:
                if (resultCode == RESULT_OK) {
                    String result = data.getStringExtra("dynamicId");
                    if (!result.startsWith("http://a.milaipay.com/")) {
                        Toast.makeText(this, data.getStringExtra("dynamicId"), Toast.LENGTH_SHORT).show();
                    } else if (StringUtils.isNullOrEmpty(Cookies.getUserId())) {
                        Toast.makeText(this, R.string.login_please, Toast.LENGTH_SHORT).show();
                        RxBus.getDefault().send(ConstansUtils.LOGIN_FIRST);
                        break;
                    } else {
                        String url = data.getStringExtra("dynamicId") + "/uid/" + Cookies.getUserId();
                        Intent intent = new Intent(this, TaskDetailActivity.class);
                        intent.putExtra("detail", url);
                        startActivity(intent);
                        break;
                    }
                }
                break;
            case ConstansUtils.SELECT_CITY:
                if ((resultCode == RESULT_OK)) {
                    tvLocation.setText(data.getStringExtra("select_city"));
                }
                break;
            case ConstansUtils.IS_LOGGING:
                if (resultCode == ConstansUtils.LOGIN_FIRST) {
                    //跳转登录页面
                    selectFragment(2);
                }
            default:
                break;
        }
    }

    //解除与总线的绑定
    @Override
    protected void onDestroy() {
        if (!rxBusSubscription.isUnsubscribed()) {
            rxBusSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    /*
            Welfare.LoggingStatusListener
         */
    @Override
    public boolean getLoggingStatus() {
        return isLogging;
    }

    /*-------------------------下拉刷新个上拉加载分割线---------------------*/
    public interface MyTouchListener {
         void onTouchEvent(MotionEvent event);
    }

    // 保存MyTouchListener接口的列表
    private ArrayList<MyTouchListener> myTouchListeners = new ArrayList<MainActivity.MyTouchListener>();

    /**
     * 提供给Fragment通过getActivity()方法来注册自己的触摸事件的方法
     * @param listener
     */
    public void registerMyTouchListener(MyTouchListener listener) {
        myTouchListeners.add(listener);
    }

    /**
     * 提供给Fragment通过getActivity()方法来取消注册自己的触摸事件的方法
     * @param listener
     */
    public  void unRegisterMyTouchListener(MyTouchListener listener) {
        myTouchListeners.remove( listener );
    }

    /**
     * 分发触摸事件给所有注册了MyTouchListener的接口
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (MyTouchListener listener : myTouchListeners) {
            listener.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {

        //两次点击返回键（Toast.SHORT显示时间内）
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtils.show(this, "再按一次退出米来");
            exitTime = System.currentTimeMillis();
        } else {
            //解绑Jpush
            JPushInterface.setAliasAndTags(MainActivity.this, "", null);
            MainActivity.super.onBackPressed();
        }

    }

    /*-------------------------下拉刷新个上拉加载分割线---------------------*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions
            , @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //授权通过
                    cameraWapper();
                } else {
                    //拒绝授权
                    Toast.makeText(this, "无相机权限，无法进行扫描操作，请在系统设置中增加应用的相应授权", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case REQUEST_STORAGE_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //授权通过
                    downloadWapper();
                } else {
                    //拒绝授权
                    Toast.makeText(this, "无读写内存卡权限，无法进行下载任务,请在系统设置中增加应用的相应授权", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }

    }
}
