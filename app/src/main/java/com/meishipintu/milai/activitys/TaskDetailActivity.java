package com.meishipintu.milai.activitys;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.meishipintu.milai.R;
import com.meishipintu.milai.application.Cookies;
import com.meishipintu.milai.utils.Immersive;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TaskDetailActivity extends BaseActivity {

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
        String url = getIntent().getStringExtra("detail");
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
                break;
        }
    }
}
