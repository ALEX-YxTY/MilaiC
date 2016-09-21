package com.meishipintu.milai.utils;

/**
 * Created by Administrator on 2016/8/21.
 */
public class NumUtil {

    public static String NumberFormat(float f,int m){
        return String.format("%."+m+"f",f);
    }

    public static float NumberFormatFloat(float f,int m){
        String strfloat = NumberFormat(f,m);
        return Float.parseFloat(strfloat);
    }

    public static String NumberFormatFromDouble(double d, int m) {
        return String.format("%." + m + "f", d);
    }
}
