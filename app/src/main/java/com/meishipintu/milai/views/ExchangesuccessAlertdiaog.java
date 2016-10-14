package com.meishipintu.milai.views;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.meishipintu.milai.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/10/12 0012.
 */

public class ExchangesuccessAlertdiaog extends AlertDialog {
    public ExchangesuccessAlertdiaog(Context context) {
        super(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alertdialog_exchangesuccess);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.Close_button, R.id.confirm_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Close_button:
                break;
            case R.id.confirm_button:
                break;
        }
    }
}
