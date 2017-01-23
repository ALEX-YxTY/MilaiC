package com.meishipintu.milai.application;

import android.app.Application;
import android.content.SharedPreferences;

import com.meishipintu.milai.R;
import com.umeng.socialize.PlatformConfig;

import cn.jpush.android.api.JPushInterface;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

//import com.umeng.socialize.PlatformConfig;

/**
 * Created by Administrator on 2016/8/2.
 */
public class MilaiApplication extends Application {

    private static MilaiApplication singleton;

    public static MilaiApplication getSingleton() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        //初始化配置weixin、sina微博、qq&Qzone
        PlatformConfig.setWeixin("wx297bb17520e8b3f3","c4551debac64a9c78e6888494ebe9580");
        PlatformConfig.setQQZone("1104296053","HU2XYqtDf1PBhK8C");
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        JPushInterface.setPushTime(this, null, 0, 23);
        //设置默认字体
//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                .setDefaultFontPath("fonts/fflt.TTF")
//                .setFontAttrId(R.attr.fontPath)
//                .build()
//        );

    }

    public SharedPreferences getSp() {
        return this.getSharedPreferences("cookies", MODE_PRIVATE);
    }
}
