package com.meishipintu.milai.listener;

import android.content.Context;
import android.widget.Toast;

import com.meishipintu.milai.activitys.HomepageActivity;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * Created by Administrator on 2017/1/18.
 */

public class UmListener implements UMShareListener {

    private Context context;

    public UmListener(Context context) {
        this.context = context;
    }

    @Override
    public void onResult(SHARE_MEDIA share_media) {
        Toast.makeText(context, share_media + " 分享成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
        Toast.makeText(context, share_media + " 分享失败", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onCancel(SHARE_MEDIA share_media) {
        Toast.makeText(context, share_media + " 分享被取消", Toast.LENGTH_SHORT).show();

    }
}
