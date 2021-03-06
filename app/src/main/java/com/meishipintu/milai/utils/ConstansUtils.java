package com.meishipintu.milai.utils;

/**
 * Created by Administrator on 2016/8/2.
 */
public class ConstansUtils {
    public final static String URL = "http://a.milaipay.com/";

    public final static int TIME_DELAYED = 3000;        //splash页延时常量3s

    public static final int LOGGING_SITUATION = 2000;           //登入登出的RequestCode
    public static final int LOG_OUT = 2001;                     //退出登录ResultCode
    public static final int LOG_IN = 2002;                     //登录成功的ResultCode

    public static final int SCAN_MAIN = 3000;                   //从主页进入扫描页面的requestCode

    public static final int SELECT_CITY = 4000;                 //从主界面进入城市选择界面requsetCode

    public static final int REGISTER = 5000;                    //从登录界面进入注册页面
    public static final int REGISTER_OK = 5001;                 //注册成功返回

    public static final int FORGET_PASSWORD = 6000;             //从登录界面进入忘记密码界面
    public static final int FORGET_PASSWORD_SUCCESS = 6001;     //重设密码成功返回

    public static final int BIND_TEL = 7000;                    //从登录界面进入绑定电话界面
    public static final int BIND_SUCCESS = 7001;                //绑定成功返回

    public static final int LOGIN_FIRST = 8001;                 //活动介绍页面，未登录的返回值result
    public static final int IS_LOGGING = 8000;                  //从主页进入活动详情页面request

    public static final int COUPON_MACHINE_CODE = 11;           //机器码
    public static final int COUPON_USABLE = 22;                 //未使用
    public static final int COUPON_USAD = 33;                   //已使用

    public static final int LOAD_SUCCESS = 1;                  //登录成功
    public static final int LOAD_MORE = 2;                     //加载更多
    public static final int REFRESH = 1;                       //重新刷新数据

}
