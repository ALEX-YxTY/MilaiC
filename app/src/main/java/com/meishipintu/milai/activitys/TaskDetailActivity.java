package com.meishipintu.milai.activitys;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
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

    String shareTitle;
    String mainUrl;

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
        mainUrl = getIntent().getStringExtra("detail");
        shareTitle = getIntent().getStringExtra("shareTitle");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //webview在onResume中初始化以解决onPause中webview被暂定的问题
        initWebView(mainUrl);
    }

    private void initWebView(String url) {
        wv.setWebViewClient(new WebViewClient(){
            //在webview中点击url链接时会调用此接口
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http://a.milaipay.com")) {
                    if (StringUtils.isNullOrEmpty(Cookies.getUserId())) {
                        Toast.makeText(TaskDetailActivity.this, R.string.login_please, Toast.LENGTH_SHORT).show();
                        RxBus.getDefault().send(ConstansUtils.LOGIN_FIRST);
                        TaskDetailActivity.this.finish();
                    } else {
                        url = url + "/uid/" + Cookies.getUserId();
                        view.loadUrl(url);
                    }
                    return true;
                } else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }
        });
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
        wv.loadUrl(url);
//        wv.loadUrl(mainUrl + "/uid/112233");
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

    //---修改时间20161202
//---HTML页面通过JS可以调用此方法获取用户UID
//---JS写法<input type="button"  value="点击调用java代码" onclick="window.android.startInterface()" />
    @JavascriptInterface
    public void startInterface(){
        //调用JS方法
        wv.loadUrl("javascript:javacalljswith('"+ Cookies.getUserId()+"')");
    }
//--JS写法<input type="button"  value="点击调用java代码并传递参数" onclick="window.android.startFunction('参数')"  />

    @JavascriptInterface
    public void startFunction(final String text){
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
//text保存HTML返回来的参数

            }
        });

    }

//---修改时间20161202


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
