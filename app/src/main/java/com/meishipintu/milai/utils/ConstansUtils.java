package com.meishipintu.milai.utils;

/**
 * Created by Administrator on 2016/8/2.
 */
public class ConstansUtils {

    public final static int TIME_DELAYED = 2000;        //splash页延时常量2s

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
}
