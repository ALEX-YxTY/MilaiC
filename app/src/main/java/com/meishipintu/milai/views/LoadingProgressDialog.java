package com.meishipintu.milai.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.meishipintu.milai.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/10/9.
 */

public class LoadingProgressDialog extends Dialog {

    RotateAnimation animation;

    @BindView(R.id.progressBar_rotate)
    ImageView progressBarRotate;

    public LoadingProgressDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        ButterKnife.bind(this);
        startAnime();
    }

    public void startAnime() {
        animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF
                , 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setRepeatMode(Animation.INFINITE);
        animation.setDuration(500);
        progressBarRotate.startAnimation(animation);
    }

    @Override
    public void dismiss() {
        animation.cancel();
        super.dismiss();
    }
    @Override
    public void show() {
        super.show();
    }
}
