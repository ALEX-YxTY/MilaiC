package com.meishipintu.milai.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.meishipintu.milai.R;
import com.meishipintu.milai.activitys.MainActivity;
import com.meishipintu.milai.application.Cookies;
import com.meishipintu.milai.beans.Task;

import java.util.Random;

/**
 * Created by Administrator on 2016/12/12 0012.
 */

public class AutoReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra("id",0);
        String title = Cookies.getTaskTitle(id);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);
        // 通过Notification.Builder来创建通知，注意API Level
        // API16之后才支持
        Notification notify = new Notification.Builder(context)
                .setSmallIcon(R.drawable.btn_remind_sel_xhdp)
                .setTicker("您有新短消息，请注意查收！")
                .setContentTitle("活动提醒")
                .setContentText(title)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent)
                .build();
        // 需要注意build()是在API level16及之后增加的，API11可以使用getNotificatin()来替代
        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
        // 在Android进行通知处理，首先需要重系统哪里获得通知管理器NotificationManager，它是一个系统Service。
        NotificationManager manager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id, notify);// 步骤4：通过通知管理器来发起通知。如果id不同，则每click，在status哪里增加一个提示
    }
}

