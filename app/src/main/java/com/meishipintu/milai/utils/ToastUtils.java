package com.meishipintu.milai.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/8/4.
 */
public class ToastUtils {

    public static void show(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
