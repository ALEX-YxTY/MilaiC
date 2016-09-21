package com.meishipintu.milai.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.meishipintu.milai.R;
import com.meishipintu.milai.application.Cookies;
import com.meishipintu.milai.beans.LoginInfo;
import com.meishipintu.milai.beans.LoginInfoTel;
import com.meishipintu.milai.beans.UserInfo;
import com.meishipintu.milai.netDao.NetApi;
import com.meishipintu.milai.utils.ConstansUtils;
import com.meishipintu.milai.utils.Immersive;
import com.meishipintu.milai.utils.MessageDigestGenerator;
import com.meishipintu.milai.utils.StringUtils;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class LoginActivity extends BaseActivity {

    private NetApi netApi;
    private boolean savePassword = true;
    private UMShareAPI mShareAPI;
    private SHARE_MEDIA platform;

    @BindView(R.id.et_UserName)
    EditText etUseName;
    @BindView(R.id.et_Password)
    EditText etPassword;

    //点击记住我
    @OnCheckedChanged(R.id.cb_save_info)
    void onSaveChanged(CompoundButton buttonView, boolean isChecked) {
        savePassword = isChecked;
        Log.i("test", "checkbox:" + isChecked);
    }

    //注册
    @OnClick(R.id.tv_register)
    void register() {
        startActivityForResult(new Intent(LoginActivity.this, RegisterActivity.class)
                , ConstansUtils.REGISTER);
    }

    //忘记密码
    @OnClick(R.id.tv_ForgetPassword)
    void forgetPass() {
        startActivityForResult(new Intent(LoginActivity.this, ForgetPassWordActivity.class)
                , ConstansUtils.FORGET_PASSWORD);
    }


    //调用login接口
    @OnClick(R.id.bt_Login)
    void login() {
        final String userName = etUseName.getText().toString();
        String password = etPassword.getText().toString();
        if (userName.equals("") || password.equals("")) {
            Toast.makeText(this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
        } else {
            LoginInfoTel loginInfo = new LoginInfoTel(userName
                    , MessageDigestGenerator.generateHash("SHA-256", password));
            Log.i("test", "loginInfo:mobile," + loginInfo.mobile + ",pass," + loginInfo.password);
            netApi.login(loginInfo).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    //开始前启动等待dialog
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            //showDialog
                        }
                    })
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<UserInfo>() {
                        @Override
                        public void onCompleted() {
                            //dismiss Dialog
                            LoginActivity.this.finish();
                        }

                        @Override
                        public void onError(Throwable e) {
                            //dismiss Dialog
                            Toast.makeText(LoginActivity.this, "登陆失败" + e.getMessage()
                                    , Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNext(UserInfo userInfo) {
                            Log.i("test", "userInfo:" + userInfo.toString());
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("user_info", userInfo);
                            intent.putExtras(bundle);
                            intent.putExtra("save_password", savePassword);
                            setResult(ConstansUtils.LOG_IN, intent);
                            onBackPressed();
                        }
                    });
        }
    }

    //qq三方登录
    @OnClick(R.id.iv_qq_login)
    void qqLogin() {
        platform = SHARE_MEDIA.QQ;
        doAuthLogin();
    }

    //微信三方登录
    @OnClick(R.id.iv_weixin_login)
    void weixinLogin() {
        platform = SHARE_MEDIA.WEIXIN;
        doAuthLogin();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Immersive.immersive(0x99999999,0,this);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        if (!Cookies.getUserName().equals("")) {
            etUseName.setText(Cookies.getUserName());
        }
        netApi = NetApi.getInstance();
        mShareAPI = UMShareAPI.get(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //授权登陆需要重写
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstansUtils.REGISTER && resultCode == ConstansUtils.REGISTER_OK) {
            String username = data.getStringExtra("user_name");
            etUseName.setText(username);
        }
        if (requestCode == ConstansUtils.FORGET_PASSWORD
                && resultCode == ConstansUtils.FORGET_PASSWORD_SUCCESS) {
            String username = data.getStringExtra("user_name");
            etUseName.setText(username);
        }
        if (requestCode == ConstansUtils.BIND_TEL && resultCode == ConstansUtils.BIND_SUCCESS) {
            //绑定成功返回
            data.putExtra("save_password", savePassword);
            setResult(ConstansUtils.LOG_IN, data);
            finish();
        }
    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        onBackPressed();
    }

    //授权回调接口
    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(final SHARE_MEDIA platform, int action, Map<String, String> data) {
            Toast.makeText(getApplicationContext(), "授权成功", Toast.LENGTH_SHORT).show();
            //获取授权后的信息
            mShareAPI.getPlatformInfo(LoginActivity.this, platform, new UMAuthListener() {
                @Override
                public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                    Log.i("test", "onComplete");
                    final LoginInfo info = new LoginInfo();
                    info.setKey(map.get("openid"));
                    info.setPassword(MessageDigestGenerator.generateHash("SHA-256", map.get("openid")));
                    info.setName(map.get("screen_name"));
                    info.setUrl(map.get("profile_image_url"));
                    if (share_media == SHARE_MEDIA.WEIXIN) {
                        info.setFrom(4);
                        if (map.get("gender").equals("1")) {
                            info.setSex(0);
                        } else {
                            info.setSex(1);
                        }
                    } else if (share_media == SHARE_MEDIA.QQ) {
                        info.setFrom(3);
                        if (map.get("gender").equals("男")) {
                            info.setSex(0);
                        } else {
                            info.setSex(1);
                        }
                    }

                    Log.i("test", "loginInfo:" + info.toString());
                    //调用三方登录接口
                    netApi.loginAuth(info).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<UserInfo>() {
                                @Override
                                public void onCompleted() {
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.i("test", "error:" + e.getMessage());
                                    Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onNext(UserInfo userInfo) {
                                    Log.i("test", "userInfo:" + userInfo.toString());
                                    if (StringUtils.isNullOrEmpty(userInfo.getTel())||
                                            StringUtils.isNullOrEmpty(userInfo.getUid())) {
                                        //未绑定手机号 进入绑定界面
                                        Intent intent = new Intent(LoginActivity.this, BindTelActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("user_info", userInfo);
                                        bundle.putSerializable("login_info", info);
                                        intent.putExtras(bundle);
                                        startActivityForResult(intent, ConstansUtils.BIND_TEL);
                                    } else {
                                        //已绑定手机 返回主界面
                                        Intent intent = new Intent();
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("user_info", userInfo);
                                        intent.putExtra("save_password", savePassword);
                                        intent.putExtras(bundle);
                                        setResult(ConstansUtils.LOG_IN, intent);
                                        onBackPressed();
                                    }
                                }
                            });
                }

                @Override
                public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                    Toast.makeText(getApplicationContext(), "读取用户信息失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancel(SHARE_MEDIA share_media, int i) {
                    Log.i("test", "onCancel");
                }
            });
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText(getApplicationContext(), "授权失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(getApplicationContext(), "授权取消", Toast.LENGTH_SHORT).show();
        }
    };

    //授权
    private void doAuthLogin() {
        boolean isInstall = mShareAPI.isInstall(this, platform);
        Log.i("test", platform.toString() + "is clicked" + ",and is installed:" + isInstall);
        if (isInstall) {
            mShareAPI.doOauthVerify(this, platform, umAuthListener);
        } else {
            Toast.makeText(this, "本机中尚未安装该软件", Toast.LENGTH_SHORT).show();
        }
    }

}
