package com.meishipintu.milai.views;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.meishipintu.milai.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/10/11 0011.
 */

public class ExchangAlertdiaog extends AlertDialog{
    @BindView(R.id.tv_content_1)
    TextView tvContent1;
    @BindView(R.id.tv_content_3)
    TextView tvContent3;
    public String name,numbermi,id;
    public Context context;
    private View.OnClickListener listener;


    public ExchangAlertdiaog (Context context , String name, String numbermi, String id, View.OnClickListener listener) {
        super(context);
        this.context=context;
        this.name=name;
        this.numbermi=numbermi;
        this.id=id;
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alertdialog_exchange);
        ButterKnife.bind(this);
        tvContent1.setText("即将从你的账户扣除" + numbermi + "米，");
        tvContent3.setText("【"+name+"】" + "吗？");
    }



    @OnClick({R.id.Close_button, R.id.confirm_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Close_button:
                this.dismiss();
                break;
            case R.id.confirm_button:

                listener.onClick(view);
//                new ExchangesuccessAlertdiaog(context).show();
                break;
        }
    }
}
