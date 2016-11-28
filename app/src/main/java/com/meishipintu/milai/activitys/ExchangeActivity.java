package com.meishipintu.milai.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.meishipintu.milai.R;
import com.meishipintu.milai.application.Cookies;
import com.meishipintu.milai.beans.Exchange;
import com.meishipintu.milai.beans.Welfare;
import com.meishipintu.milai.netDao.NetApi;
import com.meishipintu.milai.utils.ToastUtils;
import com.meishipintu.milai.views.CircleImageView;
import com.meishipintu.milai.views.ExchangAlertdiaog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ExchangeActivity extends BaseActivity{

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_condition)
    TextView tvCondition;
    @BindView(R.id.civ_head_view)
    CircleImageView civHeadView;
    @BindView(R.id.tv_balance)
    TextView tvBalance;
    String name, numbermi,id,Tel;
    private Welfare welfare;
    private Picasso picasso;
    private NetApi netApi;
    private List<Exchange> list=new ArrayList<>();
    private ExchangAlertdiaog exchangAlertdiaog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        ButterKnife.bind(this);
        welfare = (Welfare) getIntent().getExtras().get("welfare");
        tvTitle.setText("兑换权益");
        tvName.setText(welfare.getTitle());
        tvCondition.setText(welfare.getRice()+"米");
        picasso = Picasso.with(this);
        picasso.load("http://" + welfare.getDetail_logo()).into(civHeadView);
        name=welfare.getTitle();
        netApi = NetApi.getInstance();
        getMi();
        numbermi=welfare.getRice();
        id=welfare.getId();
        Tel=Cookies.getTel();
    }

    @OnClick({R.id.rl_title_location_scan, R.id.exchange_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_title_location_scan:
                onBackPressed();
                break;
            case R.id.exchange_button:
                exchangAlertdiaog = new ExchangAlertdiaog(this, name, numbermi, id, new
                        View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                gatExchange();
                            }
                        });
                exchangAlertdiaog.show();
                break;
        }
    }

    //执行兑换操作
    public void gatExchange() {
        netApi.getExchange(id, Tel).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.show(ExchangeActivity.this, "兑换错误，请检查网络");
                        exchangAlertdiaog.dismiss();
                    }

                    @Override
                    public void onNext(String s) {
                        Log.i("Msgs", s);
                        Toast.makeText(ExchangeActivity.this, s, Toast.LENGTH_SHORT).show();
                        exchangAlertdiaog.dismiss();
                        Intent intent = new Intent(ExchangeActivity.this, CouponActivityTabLayout.class);
                        startActivity(intent);
                    }
                });
    }

    public void getMi() {
        netApi.getMi(Cookies.getUserId()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("test", "error:" + e.getMessage());
                        Toast.makeText(ExchangeActivity.this, "获取米数失败，请检查网络", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNext(String string) {
                       tvBalance.setText(string+"米");
                    }

                });
    }
}
