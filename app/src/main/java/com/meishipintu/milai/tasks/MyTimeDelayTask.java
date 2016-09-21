package com.meishipintu.milai.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;

import com.meishipintu.milai.R;

/**
 * Created by Administrator on 2016/8/24.
 */
//倒计时任务
public class MyTimeDelayTask extends AsyncTask<Void,Integer,Void> {

    private int defaultTime ;
    private Button btn;
    private Context context;

    public MyTimeDelayTask(int time, Button button, Context context) {
        super();
        defaultTime = time;
        btn = button;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        Log.i("test", "preExcute");
        btn.setClickable(false);
        btn.setTextColor(context.getResources().getColor(R.color.red));
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        while (defaultTime > 0 && !this.isCancelled()) {
            defaultTime--;
            publishProgress(defaultTime);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        btn.setText(values[0] + " s");
        super.onProgressUpdate(values);
    }


    @Override
    protected void onCancelled(Void aVoid) {
        Log.i("test", "onCancelled");
        btn.setClickable(true);
        btn.setTextColor(context.getResources().getColor(R.color.tv_gray_normal));
        btn.setText(context.getResources().getString(R.string.verify_code));
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.i("test", "postExcute");
        btn.setClickable(true);
        btn.setTextColor(context.getResources().getColor(R.color.tv_gray_normal));
        btn.setText(context.getResources().getString(R.string.verify_code));
        super.onPostExecute(aVoid);
    }

}