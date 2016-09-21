package com.meishipintu.milai.activitys;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.meishipintu.milai.R;
import com.meishipintu.milai.beans.GetVCodeRequest;
import com.meishipintu.milai.beans.ResetPwdInfo;
import com.meishipintu.milai.netDao.NetApi;
import com.meishipintu.milai.tasks.MyTimeDelayTask;
import com.meishipintu.milai.utils.ConstansUtils;
import com.meishipintu.milai.utils.Immersive;
import com.meishipintu.milai.utils.MessageDigestGenerator;
import com.meishipintu.milai.utils.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ForgetPassWordActivity extends BaseActivity {

    private MyTimeDelayTask task;
    private NetApi netApi;
    private String verifyCode;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_tel)
    EditText etTel;
    @BindView(R.id.btn_get_v_code)
    Button btnGetVCode;
    @BindView(R.id.et_vCode)
    EditText etVCode;
    @BindView(R.id.et_new_password)
    EditText etNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Immersive.immersive(0x99999999,0,this);
        setContentView(R.layout.activity_forget_pass_word);
        ButterKnife.bind(this);
        tvTitle.setText(R.string.forgot_password);
        netApi = NetApi.getInstance();
        etTel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (task != null && !task.isCancelled()) {
                        task.cancel(true);
                        task = null;
                    }
                    if (!StringUtils.isNullOrEmpty(etVCode.getText().toString())) {
                        etVCode.setText("");
                    }
                    if (!StringUtils.isNullOrEmpty(etNewPassword.getText().toString())) {
                        etNewPassword.setText("");
                    }
                }
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.btn_get_v_code, R.id.bt_set_password})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
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
                                task = new MyTimeDelayTask(60, btnGetVCode, ForgetPassWordActivity.this);
                                task.execute();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(ForgetPassWordActivity.this
                                        , e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onNext(String s) {
                                verifyCode = s;
                                //TODO 测试用
//                                etVCode.setText(verifyCode);
                            }
                        });
                break;
            case R.id.bt_set_password:
                if (etTel.getText().toString().equals("")
                        || etNewPassword.getText().toString().equals("")) {
                    Toast.makeText(this, R.string.register_empty, Toast.LENGTH_SHORT).show();
                    break;
                }
                if (etVCode.getText().toString().equals("")) {
                    Toast.makeText(this, R.string.register_empty2, Toast.LENGTH_SHORT).show();
                    break;
                }
                if (!etVCode.getText().toString().equals(verifyCode)) {
                    Toast.makeText(this, R.string.register_vcode_wrong, Toast.LENGTH_SHORT).show();
                    break;
                }
                //调用注册接口
                ResetPwdInfo info = new ResetPwdInfo();
                info.setMobile(etTel.getText().toString());
                info.setVerify(verifyCode);
                info.setPassword(MessageDigestGenerator.generateHash("SHA-256"
                        , etNewPassword.getText().toString()));
                netApi.resetPwd(info).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<String>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(ForgetPassWordActivity.this
                                        , e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onNext(String s) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPassWordActivity.this);
                                builder.setMessage(s).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Intent intent = new Intent();
                                        intent.putExtra("user_name", etTel.getText().toString());
                                        setResult(ConstansUtils.FORGET_PASSWORD_SUCCESS, intent);
                                        finish();
                                    }
                                }).show();
                            }
                        });
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //activity消失时清除task
        if (task != null && !task.isCancelled()) {
            task.cancel(true);
            task = null;
        }
    }
}
