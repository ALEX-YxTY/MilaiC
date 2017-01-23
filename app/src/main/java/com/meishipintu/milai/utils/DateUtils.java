package com.meishipintu.milai.utils;

import android.provider.ContactsContract;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2016/8/16.
 */
public class DateUtils {

    public static String getDateFormat(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date(Long.valueOf(timeStamp) * 1000));
    }

    public static String getDateFormatSlash(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        return format.format(new Date(Long.valueOf(timeStamp) * 1000));
    }

    public static String getTimePeriod(String startTimeStamp, String endTimeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        //网络获取时间戳是秒为单位，在java中以毫秒为单位，要先乘以1000
        return format.format(new Date(Long.valueOf(startTimeStamp) * 1000)) + "-"
                + format.format(new Date(Long.valueOf(endTimeStamp) * 1000));
    }

    public static String getTimePeriodWithSlash(String startTimeStamp, String endTimeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd");
        return format.format(new Date(Long.valueOf(startTimeStamp) * 1000)) + "-"
                + format.format(new Date(Long.valueOf(endTimeStamp) * 1000));
    }

    public static String getTimeString() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String weekDay;
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY:
                weekDay = "星期日";
                break;
            case Calendar.MONDAY:
                weekDay = "星期一";
                break;
            case Calendar.TUESDAY:
                weekDay = "星期二";
                break;
            case Calendar.WEDNESDAY:
                weekDay = "星期三";
                break;
            case Calendar.THURSDAY:
                weekDay = "星期四";
                break;
            case Calendar.FRIDAY:
                weekDay = "星期五";
                break;
            case Calendar.SATURDAY:
                weekDay = "星期六";
                break;
            default:
                weekDay = "";
                break;
        }
        return month + "月" + day + "日 " + weekDay;
    }

    public static String[] getTimeArray(String timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-HH:mm");
        String times = sdf.format(new Date(Long.valueOf(timeStamp) * 1000));
        return times.split("-");
    }


    public static String getAvalibleTime(String startTimeStamp, String endTimeStamp) {
        return getTimeStringChinese(startTimeStamp) + "-"
                + getTimeStringChinese(endTimeStamp);
    }

    public static String getTimeStringChinese(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日");
        return format.format(new Date(Long.valueOf(timeStamp) * 1000));
    }
}
