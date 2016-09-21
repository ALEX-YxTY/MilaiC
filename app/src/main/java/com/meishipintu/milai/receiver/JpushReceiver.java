package com.meishipintu.milai.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

public class JpushReceiver extends BroadcastReceiver {

    private static final String TAG = "test";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "接收到推送下来的通知的ID: " + notifactionId);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "用户点击打开了通知");
            String extra_info = bundle.getString("cn.jpush.android.EXTRA");
            try {
                JSONObject jo = new JSONObject(extra_info);
                if (jo.has("t")) {
                    int t = jo.getInt("t");
                    if (t == 1) { //just notification
                        String content = bundle.getString("cn.jpush.android.ALERT");
                        String title = bundle.getString("cn.jpush.android.NOTIFICATION_CONTENT_TITLE");
                        Log.i(TAG, "接收到通知点击:" + title + "," + content);
//                        Intent in = new Intent();
//                        in.putExtra("title", title);
//                        in.putExtra("content", content);
//                        in.putExtra("type", t);
//                        in.setClass(context, ActJPushPrompt.class);
//                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(in);
                    } else if (t == 2) { //new activity
                        if (jo.has("mid")) {
                            long mid = jo.getLong("mid");
                            Log.i(TAG, "接收到通知点击:" + "mid:"+mid);

//                            Intent in = new Intent();
//                            in.putExtra(ConstUtil.MSG_ID, mid);
//                            in.setClass(context, ActScrambleSeat.class);
//                            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(in);
                        }
                    } else if (t == 3) { //coupon used
                        if (jo.has("sid")) {
                            long couponId = jo.getLong("sid");
                            Log.i(TAG, "接收到通知点击:" + "couponId:"+couponId);

//                            Intent in = new Intent();
//                            in.putExtra("cid", couponId);
//                            in.setClass(context, ActScrambleDetail.class);
//                            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(in);
                        }
                    } else if (t == 4) {
                        String content = bundle.getString("cn.jpush.android.ALERT");
                        String title = bundle.getString("cn.jpush.android.NOTIFICATION_CONTENT_TITLE");
                        Log.i(TAG, "接收到通知点击:" + title + "," + content);

//                        Intent in = new Intent();
//                        in.putExtra("title", title);
//                        in.putExtra("content", content);
//                        in.putExtra("type", t);
//                        in.setClass(context, ActJPushPrompt.class);
//                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(in);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else {
            Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }

    }
}
