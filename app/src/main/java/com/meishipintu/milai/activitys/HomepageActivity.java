package com.meishipintu.milai.activitys;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meishipintu.milai.R;
import com.meishipintu.milai.adapter.BannerAdapter;
import com.meishipintu.milai.adapter.MyPagerAdapter;
import com.meishipintu.milai.animes.MyTransformer;
import com.meishipintu.milai.application.Cookies;
import com.meishipintu.milai.application.RxBus;
import com.meishipintu.milai.beans.AppInfo;
import com.meishipintu.milai.beans.Task;
import com.meishipintu.milai.beans.Uid;
import com.meishipintu.milai.beans.UserDetailInfo;
import com.meishipintu.milai.beans.UserInfo;
import com.meishipintu.milai.fragments.LoginFragment;
import com.meishipintu.milai.fragments.MineFragment;
import com.meishipintu.milai.listener.UmListener;
import com.meishipintu.milai.netDao.NetApi;
import com.meishipintu.milai.tasks.MyAsy;
import com.meishipintu.milai.utils.ConstansUtils;
import com.meishipintu.milai.utils.DateUtil;
import com.meishipintu.milai.utils.DateUtils;
import com.meishipintu.milai.utils.DialogUtils;
import com.meishipintu.milai.utils.StringUtils;
import com.meishipintu.milai.utils.SystemUtils;
import com.meishipintu.milai.utils.ToastUtils;
import com.meishipintu.milai.views.CircleImageView;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class HomepageActivity extends AppCompatActivity {

    private static final int REQUEST_STORAGE_PERMISSION = 300;
    private static final int REQUEST_CAMERA_PERMISSION = 200;

    private List<Task> dataList, bannerList;
    private int[] colorArray;
    private int pointIndex = 0;                         // 圆圈标志位
    int colorForeground, colorNext;

    private View mainView;                              //主页面view
    private long exitTime = 0L;         //标注点击返回按钮的时间，初始值为0
    private NetApi netApi;
    private Subscription rxBusSubscription;      //标注和消息总线的连接情况
    private String fileDir;
    private String versionName;
    private UMShareListener umShareListener;         //分享监听
    private Picasso picasso;
    private BannerAdapter bannerAdapter;            //轮播adapter
    private MyPagerAdapter pagerAdapter;            //卡片adapter

    @BindView(R.id.vp_banner)
    ViewPager vpBanner;
    @BindView(R.id.tv_slogan)
    TextView tvPicTitle;
    @BindView(R.id.ll_circle)
    LinearLayout indicatorLayout;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.vp)
    ViewPager vpTask;
    @BindView(R.id.rl_activity)
    RelativeLayout rl;
    @BindView(R.id.drawerLayout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.tv_numerator)
    TextView tvNumerator;
    @BindView(R.id.tv_denominators)
    TextView tvDenominators;
    @BindView(R.id.civ_head_view)
    CircleImageView civHeadView;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_tel)
    TextView tvTel;

    Handler mhandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                vpBanner.setCurrentItem(vpBanner.getCurrentItem() + 1);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        ButterKnife.bind(this);
        showGuide();
        colorArray = getResources().getIntArray(R.array.colorBackground);
        netApi = NetApi.getInstance();
        picasso = Picasso.with(this);
        rxBusSubscription = RxBus.getDefault().getObservable(Integer.class).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                if (integer == ConstansUtils.LOGIN_FIRST) {
                    login();
                }
            }
        });
        checkVersion();
        initUI();
    }

    //判断是否需要显示引导页
    private void showGuide() {
        if (Cookies.getShowGuide()) {
            startActivity(new Intent(this, GuideActivity.class));
        }
    }

    private void initUI() {
        if (dataList == null) {
            dataList = new ArrayList<>();
            bannerList = new ArrayList<>();
            //设置初始背景
            rl.setBackgroundColor(colorArray[0]);
            tvTime.setText(DateUtils.getTimeString());
        } else {
            dataList.clear();
            bannerList.clear();
        }
        //获取活动数据,flag=1 首页活动和轮播活动，flag=2 全部活动
        netApi.getTaskNew(Cookies.getUserId(), 1).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Task>() {
                    @Override
                    public void onCompleted() {
                        Log.d("debug", "banner size " + bannerList.size());
                        Log.d("debug", "data size " + dataList.size());
                        initBannerAndDrawer();
                        initViewPager();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("debug", "error:" + e.getMessage());
                    }

                    @Override
                    public void onNext(Task task) {
                        //根据is_banner属性区分活动位置，1-轮播，2-卡片
                        if (task.getIs_banner().equals("1")) {
                            bannerList.add(task);
                        } else {
                            dataList.add(task);
                        }
                    }
                });
    }

    //版本检查
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
                            if (appInfo.getApp_version() > SystemUtils.getVersionCode(HomepageActivity.this)) {
                                AlertDialog.Builder builder= new AlertDialog.Builder(HomepageActivity.this);
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
                                ActivityCompat.requestPermissions(HomepageActivity.this, new String[]{android
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
            ActivityCompat.requestPermissions(HomepageActivity.this,new String[]{android
                    .Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_STORAGE_PERMISSION);
            return;
        }
        new MyAsy(HomepageActivity.this,versionName).execute(fileDir);
    }

    //请求相机权限包装方法
    private void cameraWapper() {
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
                                ActivityCompat.requestPermissions(HomepageActivity.this
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
        Intent intent = new Intent(this, CaptureHanlderActivity.class);
        intent.putExtra("CHECK_CODE", 0);       //0 - 从主页启动
        startActivityForResult(intent, ConstansUtils.SCAN_MAIN);
    }

    //初始化菜单栏和轮播banner
    private void initBannerAndDrawer() {
        indicatorLayout.removeAllViews();
        //画小圆点
        for (int i = 0; i < bannerList.size(); i++) {
            // 设置圆圈点
            View view = new View(HomepageActivity.this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(40, 8);
            params.leftMargin = 10;
            view.setBackgroundColor(0x77aaaaaa);
            view.setLayoutParams(params);
            view.setEnabled(false);
            indicatorLayout.addView(view);
        }
        //取中间数来作为起始位置
        int index = (500) - (500 % bannerList.size());
        indicatorLayout.getChildAt(pointIndex).setBackgroundColor(0xffffffff);
        if (bannerAdapter == null) {
            bannerAdapter = new BannerAdapter(HomepageActivity.this, bannerList);
            vpBanner.setAdapter(bannerAdapter);
            vpBanner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == ViewPager.SCROLL_STATE_IDLE) {
                        mhandler.sendEmptyMessageDelayed(1, 3000);
                    } else if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                        mhandler.removeMessages(1);
                    }
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                }

                @Override
                public void onPageSelected(int position) {
                    int newPosition = position % bannerList.size();
                    tvPicTitle.setText(bannerList.get(newPosition).getTitle());
                    indicatorLayout.getChildAt(newPosition).setBackgroundColor(0xffffffff);
                    indicatorLayout.getChildAt(pointIndex).setBackgroundColor(0x77aaaaaa);
                    // 更新标志位
                    pointIndex = newPosition;
                }
            });
        } else {
            bannerAdapter.notifyDataSetChanged();
        }
        vpBanner.setCurrentItem(index);


        //使Drawer拉出使下部不覆盖阴影
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (mainView == null) {
                    mainView = mDrawerLayout.getChildAt(0);        //因为drawerLayout只有2个子view，所以第一个为mainView
                }
                mainView.setScaleY(1 - slideOffset / 5);
                mainView.setScaleX(1 - slideOffset / 5);
                mainView.setTranslationX(mainView.getMeasuredWidth() * (slideOffset * 0.7f));
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
        initMenu();
    }

    //初始化menu显示
    private void initMenu() {
        Log.d(ConstansUtils.APP_NAME, "uid:" + Cookies.getUserId() + ", url:" + Cookies.getUserUrl());
        if (!StringUtils.isNullOrEmpty(Cookies.getUserId())) {
            tvName.setText(Cookies.getUserName());
            tvName.setOnClickListener(null);
            tvName.setTextColor(getResources().getColor(R.color.white));
            tvName.setBackground(getResources().getDrawable(R.drawable.shape_dialog_round_small_orange));
            tvTel.setText(StringUtils.stringWithSpaceTel(Cookies.getTel()));
        } else {
            tvName.setText("登录");
            tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    login();
                }
            });
            tvName.setTextColor(getResources().getColor(R.color.orange_bottom));
            tvName.setBackground(getResources().getDrawable(R.drawable.shape_dialog_round_small));
            tvTel.setText("");
        }
        String url = Cookies.getUserUrl();
        if (url.startsWith("http")) {
            picasso.load(url).into(civHeadView);
        } else if (!StringUtils.isNullOrEmpty(url)) {
            picasso.load(ConstansUtils.URL + url).into(civHeadView);
        } else {
            picasso.load(R.drawable.bg_mine_ava).into(civHeadView);
        }
    }

    //初始化数据
    private void initViewPager() {
        //总页数
        tvDenominators.setText("/" + dataList.size() + "");
        if (pagerAdapter == null) {
            pagerAdapter = new MyPagerAdapter(this, dataList);
            vpTask.setAdapter(pagerAdapter);
            vpTask.setOffscreenPageLimit(3);
            vpTask.setPageMargin(10);
            //设置与滚动关联的动效需要实现Transformer类
            vpTask.setPageTransformer(false, new MyTransformer(this));
            vpTask.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    //positon 表示当前位置，positionOffset为[0,1)参数，表示滑动程度，positionOffsetPixels为上一个参数的像素表示
                    //当前背景色
                    colorForeground = colorArray[position % colorArray.length];
                    //下一个背景色
                    colorNext = colorArray[(position + 1) % colorArray.length];
                    //计算rgb差值
                    int deltRed = Color.red(colorNext) - Color.red(colorForeground);
                    int deltGreen = Color.green(colorNext) - Color.green(colorForeground);
                    int deltBlue = Color.blue(colorNext) - Color.blue(colorForeground);
                    //算出当前背景色
                    int red = (int) (Color.red(colorForeground) + deltRed * positionOffset);
                    int green = (int) (Color.green(colorForeground) + deltGreen * positionOffset);
                    int blue = (int) (Color.blue(colorForeground) + deltBlue * positionOffset);
                    int colorCurrent = Color.rgb(red, green, blue);
                    rl.setBackgroundColor(colorCurrent);
                }

                @Override
                public void onPageSelected(int position) {
                    //当前页数
                    tvNumerator.setText(vpTask.getCurrentItem() % dataList.size() + 1 + "");
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        } else {
            pagerAdapter.notifyDataSetChanged();
        }
        vpTask.setCurrentItem(100 - 100 % dataList.size());
        //初始当前背景色和下一背景色
        colorForeground = colorArray[0];
        colorNext = colorArray[1];
    }

    //登录
    private void login() {
        Intent intent = new Intent(this, LoginNewActivity.class);
        startActivityForResult(intent, ConstansUtils.LOGGING_SITUATION);
        ToastUtils.show(this, "请先登录");
    }

    //menu菜单收起
    public void closeMenu() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else {
            //两次点击返回键（Toast.SHORT显示时间内）
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastUtils.show(this, "再按一次退出米来");
                exitTime = System.currentTimeMillis();
            } else {
                //解绑Jpush
                JPushInterface.setAliasAndTags(HomepageActivity.this, "", null);
                HomepageActivity.super.onBackPressed();
            }
        }
    }

    @OnClick({R.id.iv_notice, R.id.civ_head_view, R.id.iv_setting, R.id.tv_main, R.id.tv_coupon
            , R.id.tv_collection, R.id.tv_share, R.id.iv_menu, R.id.iv_scan, R.id.iv_list, R.id.tv_name})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.iv_notice:
                //我的通知页面
                intent = new Intent(this, NoticeActivity.class);
                startActivity(intent);
                break;
            case R.id.civ_head_view:
                if (!StringUtils.isNullOrEmpty(Cookies.getUserId())) {
                    //跳转账户设置
                    intent = new Intent(this, UserInfoSettingActivity.class);
                    startActivity(intent);
                } else {
                    login();
                }
                break;
            case R.id.iv_setting:
                if (!StringUtils.isNullOrEmpty(Cookies.getUserId())) {
                    //启动设置页面
                    intent = new Intent(this, SettingActivity.class);
                    startActivityForResult(intent, ConstansUtils.LOGGING_SITUATION);
                } else {
                    login();
                }
                break;
            case R.id.tv_main:
                onBackPressed();
                break;
            case R.id.tv_coupon:
                if (!StringUtils.isNullOrEmpty(Cookies.getUserId())) {
                    //我的卡券页面
                    intent = new Intent(this, CouponNewActivity.class);
                    startActivity(intent);
                } else {
                    login();
                }
                break;
            case R.id.tv_collection:
                if (!StringUtils.isNullOrEmpty(Cookies.getUserId())) {
                    //我的收藏页面
                    intent = new Intent(this, ActivityList.class);
                    intent.putExtra("type", 2);     //type=1 活动列表，type=2 抽藏列表
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_in_transy, R.anim.anim_out_alpha);
                } else {
                    login();
                }
                break;
            case R.id.tv_share:
                //友盟分享
                final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
                        {
                                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                                SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE
                        };
                umShareListener = new UmListener(this);
                new ShareAction(this).setDisplayList(displaylist)
                        .withTitle("下载关注米来")
                        .withText("支付级数字营销传播者")
                        .withTargetUrl("http://a.milaipay.com/wap/share")
                        .withMedia(new UMImage(this
                                , BitmapFactory.decodeResource(getResources(), R.drawable.icon_small)))
                        .setListenerList(umShareListener)
                        .open();
                break;
            case R.id.iv_menu:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.iv_list:
                //调起列表页面
                intent = new Intent(this, ActivityList.class);
                intent.putExtra("type", 1);     //type=1 活动列表，type=2 抽藏列表
                startActivity(intent);
                overridePendingTransition(R.anim.anim_in_transy, R.anim.anim_out_alpha);
                break;
            case R.id.iv_scan:
                cameraWapper();
                break;
            case R.id.tv_name:
                if (StringUtils.isNullOrEmpty(Cookies.getUserId())) {
                    //调起登录
                    login();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ConstansUtils.LOGGING_SITUATION:
                if (resultCode == ConstansUtils.LOG_OUT) {
                    Log.i("test", "logout!");
                    //擦除缓存
                    Cookies.clearUserInfo();
                    //解绑Jpush
                    JPushInterface.setAliasAndTags(this, "", null);
                } else if (resultCode == ConstansUtils.LOG_IN){
                    //登陆成功
                    Bundle bundle = data.getExtras();
                    UserInfo userInfo = (UserInfo) bundle.get("user_info");
                    //写入缓存
                    Cookies.clearUserInfo();
                    Cookies.setUserInfo(userInfo);
                    //绑定Jpush
                    JPushInterface.setAliasAndTags(this, Cookies.getUserId(), null);
                }
                //刷新UI
                initUI();
                break;
            case ConstansUtils.SCAN_MAIN:
                if (resultCode == RESULT_OK) {
                    String result = data.getStringExtra("dynamicId");
                    if (!result.startsWith("http://a.milaipay.com/")) {
                        Toast.makeText(this, data.getStringExtra("dynamicId"), Toast.LENGTH_SHORT).show();
                    } else if (StringUtils.isNullOrEmpty(Cookies.getUserId())) {
                        login();
                    } else {
                        String url = data.getStringExtra("dynamicId") + "/uid/" + Cookies.getUserId();
                        Intent intent = new Intent(this, TaskDetailActivity.class);
                        intent.putExtra("detail", url);
                        startActivity(intent);
                    }
                }
                break;
            default:
                break;
        }
        UMShareAPI.get(this).onActivityResult( requestCode, resultCode, data);
    }

    //申请权限回调
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

    @Override
    protected void onPause() {
        super.onPause();
        //停止轮播
        mhandler.removeMessages(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //启动轮播
        mhandler.sendEmptyMessageDelayed(1, 3000);
    }
}