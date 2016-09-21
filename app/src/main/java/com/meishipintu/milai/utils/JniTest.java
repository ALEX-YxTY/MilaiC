package com.meishipintu.milai.utils;

/**
 * Created by Administrator on 2016/9/2.
 */
public class JniTest {

    static {
        System.loadLibrary("jnitest");//导入生成的so
    }


    public static native String getTotp(String phone,int time);
}
