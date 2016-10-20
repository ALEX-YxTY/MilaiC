package com.meishipintu.milai.activitys;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.meishipintu.milai.R;
import com.meishipintu.milai.application.Cookies;
import com.meishipintu.milai.utils.Immersive;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TaskDetailActivity extends BaseActivity {

    String shareTitle;
    String url;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.web_view)
    WebView wv;
    @BindView(R.id.pb)
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Immersive.immersive(0x99999999, 0, this);
        setContentView(R.layout.activity_task_detail);
        ButterKnife.bind(this);
        tvTitle.setText(R.string.app_name);
        url = getIntent().getStringExtra("detail");
        shareTitle = getIntent().getStringExtra("shareTitle");
        initWebView(url);
    }

    private void initWebView(String url) {
        wv.setWebViewClient(new WebViewClient());
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    pb.setVisibility(View.GONE);
                } else {
                    pb.setProgress(newProgress);
                }
            }
        });
        wv.loadUrl(url + "/uid/" + Cookies.getUserId());
//        wv.loadUrl(url + "/uid/112233");
    }


    @Override
    protected void onPause() {
        super.onPause();
        //重写onPause使得webview在退出时停止声音播放
        wv.reload();
        wv.onPause();
    }

    @OnClick({R.id.iv_back, R.id.iv_share})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_share:
                //分享页面
                //友盟分享
                final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
                        {
                                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                        };
                new ShareAction(this).setDisplayList(displaylist)
                        .withText("惊喜好米抢不停")
//                        .withText(shareTitle)
                        .withTitle("第二十三届中国国际广告节")
                        .withTargetUrl(url)
                        .withMedia(new UMImage(this, BitmapFactory.decodeResource(getResources()
                                , R.drawable.icon_small)))
                        .setListenerList(umShareListener)
                        .open();
                break;
        }
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA share_media) {
            Toast.makeText(TaskDetailActivity.this, share_media + " 分享成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            Toast.makeText(TaskDetailActivity.this, share_media + " 分享失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            Toast.makeText(TaskDetailActivity.this, share_media + " 分享被取消", Toast.LENGTH_SHORT).show();
        }
    };
}
