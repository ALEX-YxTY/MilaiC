package com.meishipintu.milai.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.meishipintu.milai.R;
import com.meishipintu.milai.netDao.NetApi;
import com.meishipintu.milai.tasks.MyTimeDelaySplashTask;
import com.meishipintu.milai.tasks.MyTimeDelayTask;
import com.meishipintu.milai.utils.ConstansUtils;
import com.meishipintu.milai.utils.Immersive;
import com.meishipintu.milai.utils.ToastUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.utils.Log;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.meishipintu.milai.application.Cookies.getShowGuide;

public class SplashActivity extends BaseActivity {
    private NetApi netApi;
    private List<String> pictureList=new ArrayList<>();
    private MyTimeDelaySplashTask task;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent = new Intent();
            switch (msg.what) {
                case 0:
                    intent.setClass(SplashActivity.this, MainActivity.class);
                    break;
            }
            startActivity(intent);
            SplashActivity.this.finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Immersive.immersive(0x99999999,0,this);
        setContentView(R.layout.activity_splash);
        Button skip= (Button) findViewById(R.id.bt_skip);
        ImageView imageView= (ImageView) findViewById(R.id.img_app);
        netApi = NetApi.getInstance();
        Picasso picasso= Picasso.with(this);
        startpicture(imageView,skip,picasso);
        //当网络连接不好时，该页面最长显示4秒
        handler.sendEmptyMessageDelayed(0, 4000);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (task != null && !task.isCancelled()) {
                    task.cancel(true);
                }
                handler.removeMessages(0);
                handler.sendEmptyMessage(0);
            }
        });
    }

    private void startpicture(final ImageView imageView, final Button skip, final Picasso picasso){
        netApi.startPicture()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<String>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("test", "error:" + e.getMessage());
                    }

                    @Override
                    public void onNext(List<String> strings) {
                        pictureList.addAll(strings);
                        Log.i("pictureList",pictureList.get(0));
                        picasso.load( pictureList.get(0)).into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                handler.removeMessages(0);      //清除之前的默认计时
//                                handler.sendEmptyMessageDelayed(0, ConstansUtils.TIME_DELAYED);
                                //启动一个倒计时任务
                                task = new MyTimeDelaySplashTask(3, skip, SplashActivity.this, new MyTimeDelaySplashTask.DoOnPostExecute() {
                                    @Override
                                    public void doOnPostExecute() {
                                        handler.sendEmptyMessage(0);
                                    }
                                });
                                task.execute();
                            }

                            @Override
                            public void onError() {
                            }
                        });
                    }
                });


    }


}
