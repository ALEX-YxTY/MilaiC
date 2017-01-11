package com.meishipintu.milai.application;

import android.content.SharedPreferences;

import com.meishipintu.milai.beans.UserInfo;

/**
 * Created by Administrator on 2016/8/2.
 */
public class Cookies {

    public static boolean getShowGuide() {
        SharedPreferences sp = MilaiApplication.getSingleton().getSp();
        return sp.getBoolean("ShowGuide", true);
    }

    public static void setShowGuide(boolean showGuide) {
        SharedPreferences.Editor editor = MilaiApplication.getSingleton().getSp().edit();
        editor.putBoolean("ShowGuide", showGuide);
        editor.commit();
    }

    public static String getUserId() {
        SharedPreferences sp = MilaiApplication.getSingleton().getSp();
        return sp.getString("user_id", "");
    }

    public static void setUserId(String userId) {
        SharedPreferences.Editor editor = MilaiApplication.getSingleton().getSp().edit();
        editor.putString("user_id", userId);
        editor.commit();
    }

    public static int getCityId() {
        return MilaiApplication.getSingleton().getSp().getInt("city_id", 0);
    }

    public static void clearUserInfo() {
        SharedPreferences.Editor editor = MilaiApplication.getSingleton().getSp().edit();
        editor.remove("user_id");
        editor.remove("user_name");
        editor.remove("sex");
        editor.remove("tel");
        editor.remove("from");
        editor.remove("url");
        editor.commit();
    }

    public static String getUserUrl() {
        SharedPreferences sp = MilaiApplication.getSingleton().getSp();
        return sp.getString("url", "");
    }

    public static void setUserUrl(String url) {
        SharedPreferences.Editor editor = MilaiApplication.getSingleton().getSp().edit();
        editor.putString("url", url);
        editor.commit();
    }

    public static void setUserInfo(UserInfo userInfo) {
        SharedPreferences.Editor editor = MilaiApplication.getSingleton().getSp().edit();
        editor.putString("user_id", userInfo.getUid());
        editor.putString("user_name", userInfo.getName());
        editor.putInt("sex", userInfo.getSex());
        editor.putString("tel", userInfo.getTel());
        editor.putInt("from", userInfo.getFrom());
        editor.putString("url", userInfo.getUrl());
        editor.commit();
    }

    public static void setUserName(String name) {
        SharedPreferences.Editor editor = MilaiApplication.getSingleton().getSp().edit();
        editor.putString("user_name", name);
        editor.commit();
    }

    public static void setSex(int sex) {
        SharedPreferences.Editor editor = MilaiApplication.getSingleton().getSp().edit();
        editor.putInt("sex", sex);
        editor.commit();
    }

    public static int getSex() {
        SharedPreferences sp = MilaiApplication.getSingleton().getSp();
        return sp.getInt("sex", 0);
    }

    public static void setTel(String tel) {
        SharedPreferences.Editor editor = MilaiApplication.getSingleton().getSp().edit();
        editor.putString("tel", tel);
        editor.commit();
    }

    public static String getTel() {
        SharedPreferences sp = MilaiApplication.getSingleton().getSp();
        return sp.getString("tel", "");
    }

    public static String getCity() {
        SharedPreferences sp = MilaiApplication.getSingleton().getSp();
        return sp.getString("cityId", "");
    }

    public static void setCity(String city) {
        SharedPreferences.Editor editor = MilaiApplication.getSingleton().getSp().edit();
        editor.putString("cityId", city);
        editor.commit();
    }

    public static void setAutoLogin(boolean b) {
        SharedPreferences sp = MilaiApplication.getSingleton().getSp();
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("auto_login", b);
        editor.apply();
    }

    public static boolean getAutoLogin() {
        SharedPreferences sp = MilaiApplication.getSingleton().getSp();
        return sp.getBoolean("auto_login", true);
    }

    public static String getUserName() {
        SharedPreferences sp = MilaiApplication.getSingleton().getSp();
        return sp.getString("user_name", "");
    }

    public static void setAlarm(String id, boolean Alarm) {
        SharedPreferences.Editor editor = MilaiApplication.getSingleton().getSp().edit();
        editor.putBoolean("task"+id, Alarm);
        editor.apply();
    }

    public static void setAlarm(String id, boolean alarm, String title) {
        SharedPreferences.Editor editor = MilaiApplication.getSingleton().getSp().edit();
        editor.putBoolean("task"+id, alarm);
        editor.putString("task" + id + "title", title);
        editor.apply();
    }

    public static boolean getAlarm(String id) {
        SharedPreferences sp = MilaiApplication.getSingleton().getSp();
        return sp.getBoolean("task"+id, false);
    }

    public static String getTaskTitle(int id) {
        SharedPreferences sp = MilaiApplication.getSingleton().getSp();
        return sp.getString("task" + id + "title", "");
    }

}
