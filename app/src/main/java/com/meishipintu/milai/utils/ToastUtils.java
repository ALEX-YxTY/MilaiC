package com.meishipintu.milai.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.meishipintu.milai.R;

/**
 * Created by Administrator on 2016/8/4.
 */
public class ToastUtils {

    public static void show(Context context, String message) {
        View toastView = View.inflate(context, R.layout.toast, null);
        TextView tvToast = (TextView) toastView.findViewById(R.id.tv_toast);
        tvToast.setText(message);
        Toast toast = new Toast(context);
        toast.setView(toastView);
        toast.show();
    }

    public static void show(Context context, String message, int drawableResouceId) {
        View toastView = View.inflate(context, R.layout.toast, null);
        TextView tvToast = (TextView) toastView.findViewById(R.id.tv_toast);
        ImageView ivToast = (ImageView) toastView.findViewById(R.id.iv_toast);
        ivToast.setBackgroundResource(drawableResouceId);
        tvToast.setText(message);
        Toast toast = new Toast(context);
        toast.setView(toastView);
        toast.show();
    }

    public static void show(Context context, String message, Bitmap bitmap) {
        View toastView = View.inflate(context, R.layout.toast, null);
        TextView tvToast = (TextView) toastView.findViewById(R.id.tv_toast);
        ImageView ivToast = (ImageView) toastView.findViewById(R.id.iv_toast);
        ivToast.setImageBitmap(bitmap);
        tvToast.setText(message);
        Toast toast = new Toast(context);
        toast.setView(toastView);
        toast.show();
    }
}
