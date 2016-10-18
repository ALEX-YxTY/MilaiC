package com.meishipintu.milai.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.meishipintu.milai.R;
import com.meishipintu.milai.application.Cookies;
import com.meishipintu.milai.beans.GetVCodeRequest;
import com.meishipintu.milai.beans.UserInfo;
import com.meishipintu.milai.netDao.NetApi;
import com.meishipintu.milai.tasks.MyTimeDelayTask;
import com.meishipintu.milai.utils.ConstansUtils;
import com.meishipintu.milai.utils.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class LoginNewActivity extends BaseActivity {

    private MyTimeDelayTask task;
    private NetApi netApi;
    private String verifyCode;


    @BindView(R.id.et_tel)
    EditText etTel;
    @BindView(R.id.et_vCode)
    EditText etVCode;
    @BindView(R.id.btn_get_v_code)
    Button btnGetVCode;


    //调用login接口
    @OnClick({R.id.bt_login,R.id.btn_get_v_code,R.id.iv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_login:
                final String mobile = etTel.getText().toString();
                String verify = etVCode.getText().toString();
                if (StringUtils.isNullOrEmpty(mobile) || StringUtils.isNullOrEmpty(verify) ) {
                    Toast.makeText(this, "手机号码和验证码不能为空", Toast.LENGTH_SHORT).show();
                } else if (!etVCode.getText().toString().equals(verifyCode)) {
                    Toast.makeText(this, R.string.register_vcode_wrong, Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("test", "loginInfo:mobile," + mobile + ",verify," + verify);
                    netApi.loginNew(mobile, verify).subscribeOn(Schedulers.io())
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
                                    LoginNewActivity.this.finish();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    //dismiss Dialog
                                    Toast.makeText(LoginNewActivity.this, "登陆失败" + e.getMessage()
                                            , Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onNext(UserInfo userInfo) {
//                                    Log.i("test", "userInfo:" + userInfo.toString());
                                    Intent intent = new Intent();
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("user_info", userInfo);
                                    intent.putExtras(bundle);
                                    setResult(ConstansUtils.LOG_IN, intent);
                                    onBackPressed();
                                }
                            });
                }
                break;
            case R.id.btn_get_v_code:
                //获取验证码并记录
                if (etTel.getText().toString().equals("")) {
                    Toast.makeText(this, R.string.register_empty3, Toast.LENGTH_SHORT).show();
                    break;
                }
                netApi.getVerifyCode(new GetVCodeRequest(etTel.getText().toString())).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<String>() {
                            @Override
                            public void onCompleted() {
                                task = new MyTimeDelayTask(60, btnGetVCode, LoginNewActivity.this);
                                task.execute();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(LoginNewActivity.this, e.getMessage()
                                        , Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onNext(String s) {
                                verifyCode = s;
                                //TODO 测试用
//                                etVCode.setText(verifyCode);
                            }
                        });
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
            default:
                break;
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);
        ButterKnife.bind(this);
        if (!Cookies.getUserName().equals("")) {
            etTel.setText(Cookies.getUserName());
        }
        netApi = NetApi.getInstance();
    }

    @Override
    public boolean useSwipeBack() {
        return true;
    }
}
