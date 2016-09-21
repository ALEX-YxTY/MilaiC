package com.meishipintu.milai.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.meishipintu.milai.R;
import com.meishipintu.milai.utils.ConstansUtils;
import com.meishipintu.milai.utils.Immersive;

import cn.jpush.android.api.JPushInterface;

public class SplashActivity extends BaseActivity {

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent = new Intent();
            switch (msg.what) {
                case 0:
                    //TODO 测试
                    if (false) {
                        intent.setClass(SplashActivity.this, GuideActivity.class);
                        startActivity(intent);
                        SplashActivity.this.finish();
                    } else {
                        intent.setClass(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        SplashActivity.this.finish();
                    }
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Immersive.immersive(0x99999999,0,this);
        setContentView(R.layout.activity_splash);
        handler.sendEmptyMessageDelayed(0, ConstansUtils.TIME_DELAYED);
    }

}
