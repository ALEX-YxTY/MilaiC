package com.meishipintu.milai.activitys;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.meishipintu.milai.R;
import com.meishipintu.milai.application.Cookies;
import com.meishipintu.milai.beans.Uid;
import com.meishipintu.milai.beans.UserDetailInfo;
import com.meishipintu.milai.netDao.NetApi;
import com.meishipintu.milai.utils.Immersive;
import com.meishipintu.milai.utils.StringUtils;
import com.meishipintu.milai.views.CircleImageView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserInfoSettingActivity extends BaseActivity {

    private UserDetailInfo userDetailInfo;
    private NetApi netApi;
    private Picasso picasso;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.save)
    TextView save;
    @BindView(R.id.iv_head_view)
    CircleImageView ivHeadView;
    @BindView(R.id.tv_tel)
    TextView tvTel;
    @BindView(R.id.et_nick_name)
    EditText etNickName;
    @BindView(R.id.et_real_name)
    EditText etRealName;
    @BindView(R.id.rg_tab)
    RadioGroup rgTab;
    @BindView(R.id.et_address)
    EditText etAddress;
    @BindView(R.id.et_signature)
    EditText etSignature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Immersive.immersive(0x99999999,0,this);
        netApi = NetApi.getInstance();
        picasso = Picasso.with(this);
        initUI();
    }

    private void initUI() {
        setContentView(R.layout.activity_user_info_setting);
        ButterKnife.bind(this);
        tvTitle.setText(R.string.sys_seting);
        save.setVisibility(View.VISIBLE);
        getUserDetailInfo();
    }

    @OnClick({R.id.iv_back, R.id.save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.save:
                if (!StringUtils.isNullOrEmpty(etNickName.getText().toString())) {
                    userDetailInfo.setName(etNickName.getText().toString());
                }
                if (!StringUtils.isNullOrEmpty(etRealName.getText().toString())) {
                    userDetailInfo.setRealname(etRealName.getText().toString());
                }
                if (!StringUtils.isNullOrEmpty(etAddress.getText().toString())) {
                    userDetailInfo.setAddress(etAddress.getText().toString());
                }
                if (!StringUtils.isNullOrEmpty(etSignature.getText().toString())) {
                    userDetailInfo.setSignature(etSignature.getText().toString());
                }
                int sex = rgTab.getCheckedRadioButtonId() == R.id.rb_male ? 0 : 1;
                userDetailInfo.setSex(sex);

                Log.i("test", "userinfopost:" + userDetailInfo.toString());
                netApi.updateUserDetail(userDetailInfo).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<String>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(UserInfoSettingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onNext(String s) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoSettingActivity.this);
                                builder.setMessage(s)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Cookies.setSex(rgTab.getCheckedRadioButtonId() == R.id.rb_male ? 0 : 1);
                                                Cookies.setUserName(etNickName.getText().toString());
                                                dialog.dismiss();
                                            }
                                        });
                                builder.create().show();
                            }
                        });
                break;
            default:
                break;
        }
    }

    public void getUserDetailInfo() {
        Uid uid = new Uid(Cookies.getUserId());
        netApi.getUserDetailInfo(uid).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserDetailInfo>() {
                    @Override
                    public void onCompleted() {
                        reFreshUI();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(UserInfoSettingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(UserDetailInfo userDetailInfo) {
                        UserInfoSettingActivity.this.userDetailInfo = userDetailInfo;
                    }
                });
    }

    private void reFreshUI() {
        tvTel.setText(userDetailInfo.getTel());
        Log.i("test", "userinfonow:" + userDetailInfo.toString());
        if (!StringUtils.isNullOrEmpty(userDetailInfo.getUrl())) {
            picasso.load(userDetailInfo.getUrl()).into(ivHeadView);
        }
        if (!StringUtils.isNullOrEmpty(userDetailInfo.getName())) {
            etNickName.setHint(userDetailInfo.getName());
        }
        if (!StringUtils.isNullOrEmpty(userDetailInfo.getRealname())) {
            etRealName.setHint(userDetailInfo.getRealname());
        }
        if (!StringUtils.isNullOrEmpty(userDetailInfo.getAddress())) {
            etAddress.setHint(userDetailInfo.getAddress());
        }
        if (!StringUtils.isNullOrEmpty(userDetailInfo.getSignature())) {
            etSignature.setHint(userDetailInfo.getSignature());
        }
        rgTab.check(userDetailInfo.getSex() > 0 ? R.id.rb_female : R.id.rb_male);
    }

}
