package com.meishipintu.milai.activitys;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;

import com.meishipintu.milai.utils.Immersive;

import cn.jpush.android.api.JPushInterface;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Administrator on 2016/9/13.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private float actionX = 0, actionY = 0;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        Immersive.immersive(0x99999999,0,this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    //是否使用左滑返回功能,需要使用此功能的activity重写此方法，返回true即可
    public boolean useSwipeBack() {
        return false;
    }


    //左滑返回
    //重写dispatchTouchEvent而不是OnTouchEvent,是为了在edtiText、scrollview、viewpager之类
    //控件之前获取事件，而写在onTouchEvent中则可能事件已被消费而获取不到
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.i("test", "ontouch:" + useSwipeBack());
        if (useSwipeBack()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    actionX = event.getRawX();
                    actionY = event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    float deltX = event.getRawX() - actionX;
                    float deltY = event.getRawY() - actionY;
                    Log.i("test", "event:" + deltX + "," + deltY);
                    if (deltX > 300 && Math.abs(deltY)< 200) {
                        onBackPressed();
                        return true;
                    }
                    break;
                default:
                    break;
            }
        }

        return super.dispatchTouchEvent(event);
    }
}
