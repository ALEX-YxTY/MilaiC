package com.meishipintu.milai.activitys;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.meishipintu.milai.R;

import com.meishipintu.milai.application.Cookies;
import com.meishipintu.milai.application.RxBus;
import com.meishipintu.milai.utils.ConstansUtils;

import com.meishipintu.milai.utils.Immersive;
import com.meishipintu.milai.utils.StringUtils;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TaskDetailActivity extends BaseActivity {

    private String mainUrl;
    private int type;               //打开网页类型 1-红包页面 2-外链

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
        Intent from = getIntent();
        mainUrl = from.getStringExtra("detail");
        type = from.getIntExtra("type", 0);      //默认红包
        Log.d(ConstansUtils.APP_NAME, "getIntent type:" + type);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //webview在onResume中初始化以解决onPause中webview被暂定的问题
        initWebView(mainUrl);
    }

    private void initWebView(String url) {
        //重新设置websettings  
        WebSettings s = wv.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);

//        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebViewClient(new WebViewClient(){
            //在webview中点击url链接时会调用此接口
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(ConstansUtils.APP_NAME, "click url:" + url + " type:" + type);

                if (url.startsWith("http://a.milaipay.com") && type == 2) {
                    //米来的外部链接才需要拼uid
                    if (StringUtils.isNullOrEmpty(Cookies.getUserId())) {
                        Toast.makeText(TaskDetailActivity.this, R.string.login_please, Toast.LENGTH_SHORT).show();
                        RxBus.getDefault().send(ConstansUtils.LOGIN_FIRST);
                        TaskDetailActivity.this.finish();
                    } else {
                        url = url + "/uid/" + Cookies.getUserId();
                        Log.d(ConstansUtils.APP_NAME, "come if:" + url);
                        view.loadUrl(url);
                    }
                    return true;
                } else {
                    Log.d(ConstansUtils.APP_NAME, "come else:" + url);
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }
        });
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
        Log.d(ConstansUtils.APP_NAME, "url:" + url);
        wv.loadUrl(url);
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
                        .withTargetUrl(mainUrl)
                        .withMedia(new UMImage(this, BitmapFactory.decodeResource(getResources()
                                , R.drawable.icon_small)))
                        .setListenerList(umShareListener)
                        .open();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (wv.canGoBack()) {
            wv.goBack();
        } else {
            super.onBackPressed();
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
