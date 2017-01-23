package com.meishipintu.milai.utils;

/**
 * Created by Administrator on 2016/8/5.
 */
public class StringUtils {

    public static boolean isNullOrEmpty(String s) {
        if (s == null) {
            return true;
        }else{
            s = s.trim().replace(" ", "");
            if (s.equals("")) {
                return true;
            }
        }

        return false;
    }

    public static String stringWithSpace(String s) {
        int length = s.length();
        StringBuffer result = new StringBuffer();

        int start = 0;
        for(int i=0;i<length;i++) {
            //每四位加一个空格
            if (i != 0 && (i % 4 == 0)) {
                result.append(s.subSequence(start, i));
                result.append(" ");
                start = i;
            }
        }
        //把最后一段加上
        result.append(s.substring(start));

        return result.toString();
    }

    public static String stringWithSpaceTel(String s) {
        int length = s.length();
        StringBuffer result = new StringBuffer();
        result.append(s.substring(0, 3));
        result.append(" ");
        int start = 3;
        for(int i=0;i<length-3;i++) {
            if (i != 0 & i % 4 == 0) {
                result.append(s.substring(start, start + i));
                result.append(" ");
                start += i;
            }
        }
        //把最后一段加上
        result.append(s.substring(start));
        return result.toString();
    }
}
