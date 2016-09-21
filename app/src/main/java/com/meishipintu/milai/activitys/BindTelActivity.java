package com.meishipintu.milai.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.meishipintu.milai.R;
import com.meishipintu.milai.beans.BindTelInfo;
import com.meishipintu.milai.beans.GetVCodeRequest;
import com.meishipintu.milai.beans.LoginInfo;
import com.meishipintu.milai.beans.UserInfo;
import com.meishipintu.milai.netDao.NetApi;
import com.meishipintu.milai.tasks.MyTimeDelayTask;
import com.meishipintu.milai.utils.ConstansUtils;
import com.meishipintu.milai.utils.Immersive;
import com.meishipintu.milai.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BindTelActivity extends BaseActivity {

    private NetApi netApi;
    private String verifyCode;
    private MyTimeDelayTask task;
    private UserInfo info;
    private LoginInfo loginInfo;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_tel)
    EditText etTel;
    @BindView(R.id.et_vCode)
    EditText etVCode;
    @BindView(R.id.btn_get_v_code)
    Button btnGetVCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Immersive.immersive(0x99999999,0,this);
        setContentView(R.layout.activity_bind_tel);
        ButterKnife.bind(this);
        netApi = NetApi.getInstance();
        info = (UserInfo) getIntent().getExtras().get("user_info");
        loginInfo = (LoginInfo) getIntent().getExtras().get("login_info");
        tvTitle.setText(R.string.bind);
    }

    @OnClick({R.id.iv_back, R.id.btn_get_v_code, R.id.bt_bind_tel})
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
                                task = new MyTimeDelayTask(60, btnGetVCode, BindTelActivity.this);
                                task.execute();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(BindTelActivity.this
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
            case R.id.bt_bind_tel:
                String tel = etTel.getText().toString();
                String vCode = etVCode.getText().toString();
                if (StringUtils.isNullOrEmpty(tel) || StringUtils.isNullOrEmpty(vCode)) {
                    Toast.makeText(this, "手机号或验证码不能为空", Toast.LENGTH_SHORT).show();
                }
                final BindTelInfo bindInfo = new BindTelInfo();
                bindInfo.setUid(info.getUid());
                bindInfo.setFrom(loginInfo.getFrom());
                bindInfo.setKey(loginInfo.getKey());
                bindInfo.setMemberTel(tel);
                bindInfo.setVerify(vCode);
                Log.i("test", "bindinfo:" + bindInfo.toString());
                netApi.bindTel(bindInfo).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<String>() {
                            @Override
                            public void onCompleted() {
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("user_info", info);
                                intent.putExtras(bundle);
                                setResult(ConstansUtils.BIND_SUCCESS, intent);
                                BindTelActivity.this.finish();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(BindTelActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onNext(String s) {
                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    info.setUid(jsonObject.getString("uid"));
                                    info.setTel(jsonObject.getString("memberTel"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                break;
        }
    }

}
