package com.meishipintu.milai.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.meishipintu.milai.beans.Task;

import java.util.Calendar;

public class MyService extends Service {
    private AlarmManager am;
    private PendingIntent sender;
    private Intent intent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(" onBind","走到了");
        return new MyBind();
    }

    public class MyBind extends Binder {
        public MyService getMyService() {
            return MyService.this;
        }
    }

    public void setAlarm(Task task){
        intent = new Intent();
        int id = Integer.parseInt(task.getId());
        intent.setAction("AutoReceiver");
        intent.putExtra("id", id);
        //获取活动开始时间，将提醒设为活动开始日的9点50分
        long startTime = Long.parseLong(task.getStart_time());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime*1000);
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 50);

        sender = PendingIntent.getBroadcast(this, id, intent, 0);
        am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC,
                calendar.getTimeInMillis(), sender);
    }

    public void cancelAlarm(Task task){
        intent = new Intent();
        int id = Integer.parseInt(task.getId());
        intent.putExtra("id",id);
        intent.setAction("AutoReceiver");
        sender = PendingIntent.getBroadcast(this,id, intent,0);
        am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(sender);
    }

}
