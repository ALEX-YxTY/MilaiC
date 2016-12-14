package com.meishipintu.milai.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.meishipintu.milai.R;

/**
 * Created by Administrator on 2016/8/24.
 */
//倒计时任务
public class MyTimeDelaySplashTask extends AsyncTask<Void,Integer,Void> {

    private int defaultTime ;
    private Button btn;
    private Context context;
    private DoOnPostExecute listener;

    public MyTimeDelaySplashTask(int time, Button button, Context context, DoOnPostExecute listener) {
        super();
        defaultTime = time;
        btn = button;
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        Log.i("test", "preExcute");
        btn.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        while (defaultTime > 0 && !this.isCancelled()) {
            publishProgress(defaultTime);
            defaultTime--;
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
        btn.setText(values[0] + " 跳过");
        super.onProgressUpdate(values);
    }


    @Override
    protected void onCancelled(Void aVoid) {
        Log.i("test", "onCancelled");
        btn.setText(context.getResources().getString(R.string.skip));
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        this.listener.doOnPostExecute();
        super.onPostExecute(aVoid);
    }

    public interface DoOnPostExecute {
        void doOnPostExecute();
    }
}
