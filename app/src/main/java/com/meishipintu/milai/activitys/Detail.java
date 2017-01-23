package com.meishipintu.milai.activitys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.meishipintu.milai.R;


public class Detail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ImageView iv = (ImageView) findViewById(R.id.iv);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.activity_detail);
        int position = getIntent().getIntExtra("position", 0);
        int[] colorArrar = getResources().getIntArray(R.array.colorBackground);
        //给颜色值添加alpha值
        rl.setBackgroundColor(0xFF << 24 |colorArrar[position % colorArrar.length]);
        int imageResId = getIntent().getIntExtra("pic", 0);
        if (imageResId != 0) {
            iv.setImageResource(imageResId);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_in_alpha,R.anim.anim_out_scale);
    }
}
