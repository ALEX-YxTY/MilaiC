package com.meishipintu.milai.activitys;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.meishipintu.milai.R;
import com.meishipintu.milai.beans.AppInfo;
import com.meishipintu.milai.netDao.NetApi;
import com.meishipintu.milai.tasks.MyAsy;
import com.meishipintu.milai.utils.ConstansUtils;
import com.meishipintu.milai.utils.Immersive;
import com.meishipintu.milai.utils.SystemUtils;
import com.meishipintu.milai.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_cache)
    TextView tvCache;

    private NetApi netApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Immersive.immersive(0x99999999,0,this);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        netApi = NetApi.getInstance();
        initUI();
    }

    private void initUI() {
        tvTitle.setText(R.string.settings);
        //获取缓存并设置
    }

    @OnClick({R.id.iv_back, R.id.rl_person_detail, R.id.clear_cache, R.id.rl_about, R.id.rl_contact, R.id.btn_logout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.rl_person_detail:
                //进入个人信息设置页面
                startActivity(new Intent(SettingActivity.this, UserInfoSettingActivity.class));
                break;
            case R.id.clear_cache:
                break;
            case R.id.rl_about:
                checkVersion();
                break;
            case R.id.rl_contact:
                startActivity(new Intent(SettingActivity.this,ContactActivity.class));
                break;
            case R.id.btn_logout:
                setResult(ConstansUtils.LOG_OUT);
                this.finish();
                break;
        }
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
                            if (appInfo.getApp_version() > SystemUtils.getVersionCode(SettingActivity.this)) {
                                AlertDialog.Builder builder= new AlertDialog.Builder(SettingActivity.this);
                                builder.setTitle("发现新版本");
                                builder.setMessage("最新版本：" + appInfo.getApp_version_name()
                                        + "\n更新内容：" + appInfo.getApp_update_desc());
                                builder.setPositiveButton("现在升级", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        new MyAsy(SettingActivity.this).execute(appInfo.getApp_file());
                                    }
                                });
                                builder.setNegativeButton("稍后再说", null);
                                builder.show();
                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                                builder.setTitle("版本信息")
                                        .setMessage("当前已是最新版本：版本号"+SystemUtils.getVersionName(SettingActivity.this))
                                        .setPositiveButton("确定", null);
                                builder.show();
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public boolean useSwipeBack() {
        return true;
    }
}
