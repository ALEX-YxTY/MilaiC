package com.meishipintu.milai.utils;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by Administrator on 2016/8/29.
 */
public class SystemUtils {

    public static int getVersionCode(Context context) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
    }

    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
