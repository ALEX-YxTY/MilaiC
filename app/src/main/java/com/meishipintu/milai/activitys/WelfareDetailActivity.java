package com.meishipintu.milai.activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.enrique.stackblur.StackBlurManager;
import com.meishipintu.milai.R;
import com.meishipintu.milai.beans.Welfare;
import com.meishipintu.milai.utils.ConstansUtils;
import com.meishipintu.milai.utils.DateUtils;
import com.meishipintu.milai.utils.Immersive;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelfareDetailActivity extends BaseActivity {

    private static final int BLUR_BITMAP_OK = 100;
    private boolean isLogging;
    private Welfare welfare;
    private Picasso picasso;
    LinearLayout layout;
    private Bitmap bitmapBackground;
    private Bitmap afterBlur;
    private StackBlurManager stackBlurManager;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == BLUR_BITMAP_OK) {
                rlWhole.setBackground(new BitmapDrawable(getResources(), afterBlur));
                Log.i("test", "blur ok");
            }
        }
    };

    @BindView(R.id.rl_whole)
    RelativeLayout rlWhole;
    @BindView(R.id.iv_welfare)
    ImageView ivWelfare;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_act_desc)
    TextView tvActDesc;
    @BindView(R.id.tv_shop_desc)
    TextView tvShopDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Immersive.immersive(0x99999999, 0, this);
        setContentView(R.layout.activity_welfare_detail);
        ButterKnife.bind(this);
        picasso = Picasso.with(this);
        welfare = (Welfare) getIntent().getExtras().get("welfare");
        isLogging = getIntent().getBooleanExtra("isLogging", false);
        blurBackground();
        initUi();
    }

    private void blurBackground() {
        new Thread() {
            @Override
            public void run() {
                //取出图片
                byte[] bis = getIntent().getByteArrayExtra("bitmap");
                bitmapBackground = BitmapFactory.decodeByteArray(bis, 0, bis.length);
//                bitmapBackground = getIntent().getParcelableExtra("bitmap");
                Log.i("test", "bis.size:" + bitmapBackground.getByteCount());
                //使用stackblur写
                long startTime = System.currentTimeMillis();
                stackBlurManager = new StackBlurManager(bitmapBackground);
                afterBlur = stackBlurManager.process(30);
                handler.sendEmptyMessage(BLUR_BITMAP_OK);
                Log.i("test", "costtimeblur:" + (System.currentTimeMillis() - startTime));
//                //sdk17以上才可用blur效果
//                if (Build.VERSION.SDK_INT >= 17) {
//                    rlWhole.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//                        @Override
//                        public boolean onPreDraw() {
//                            //及时清除listener，在只需要一次的情况下
//                            rlWhole.getViewTreeObserver().removeOnPreDrawListener(this);
//                            //允许保存图片缓存
//                            rlWhole.buildDrawingCache();
////                            rlWhole.setBackground(new BitmapDrawable(bitmapBackground));
//                            blur();
//                            return true;
//                        }
//                    });
//                }
            }
        }.run();
    }

    //毛玻璃效果核心代码
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
//    private void blur() {
//        long startMs = System.currentTimeMillis();
//        //高斯模糊半径
//        float radius = 20;
//        afterBlur = Bitmap.createBitmap(bitmapBackground.getWidth(),
//                bitmapBackground.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(afterBlur);
////        canvas.translate(-rlWhole.getLeft(), -rlWhole.getTop());
////        //预先压缩图片
////        canvas.scale(1/20, 1/20);
////        Paint paint = new Paint();
////        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
//        canvas.drawBitmap(bitmapBackground, 0, 0, null);
//
//        RenderScript rs = RenderScript.create(WelfareDetailActivity.this);
//        Allocation overlayAlloc = Allocation.createFromBitmap(
//                rs, afterBlur);
//        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(
//                rs, overlayAlloc.getElement());
//        blur.setInput(overlayAlloc);
//        blur.setRadius(radius);
//        blur.forEach(overlayAlloc);
//        overlayAlloc.copyTo(afterBlur);
//        rs.destroy();
//        Log.i("test", "共使用：" + (System.currentTimeMillis() - startMs) + "ms");
//        handler.sendEmptyMessage(BLUR_BITMAP_OK);
//    }


    private void initUi() {
        picasso.load("http://" + welfare.getLogo()).into(ivWelfare);
        tvTitle.setText(welfare.getTitle());
        Log.e("tvTitle",welfare.getTitle());
        tvActDesc.setText(welfare.getAct_desc());
        tvShopDesc.setText(Html.fromHtml(welfare.getShop_desc()));
        tvShopDesc.setMovementMethod(LinkMovementMethod.getInstance());
        //因为simpleDateFormat的线程问题，只能写在一句中
        tvTime.setText("活动时间：" + DateUtils.getTimePeriod(welfare.getStart_time(), welfare.getEnd_time()));
    }

    @OnClick({R.id.iv_back, R.id.iv_share, R.id.change})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_share:
//                Toast.makeText(this,"分享",Toast.LENGTH_SHORT).show();
                final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
                        {
                                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                                SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE
                        };
                new ShareAction(this).setDisplayList(displaylist)
                        .withText("快来使用米来吧~")
                        .withTitle("米来")
                        .withTargetUrl("https://www.pgyer.com/milai_c")
                        .withMedia(new UMImage(WelfareDetailActivity.this
                                , BitmapFactory.decodeResource(getResources(), R.drawable.icon_small)))
                        .setListenerList(umShareListener)
                        .open();
                break;
            case R.id.change:
                if (isLogging) {
                    if (welfare.getFlag() != 4) {
                        Intent intent;
                        if (welfare.getFlag() == 3) {
                            intent=new Intent(WelfareDetailActivity.this, ExchangeActivity.class);
                            Bundle bundle=new Bundle();
                            //传递name参数为tinyphp
                            bundle.putSerializable("welfare", welfare);
                            intent.putExtras(bundle);
                        }else{
                            intent=new Intent(WelfareDetailActivity.this, CouponActivityTabLayout.class);
                            if (welfare.getFlag() == 2) {
                                intent.putExtra("showCanUse", true);
                            }
                        }
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(this, R.string.login_please, Toast.LENGTH_SHORT).show();
                    setResult(ConstansUtils.LOGIN_FIRST);
                    finish();
                }
                break;
        }
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA share_media) {
            Toast.makeText(WelfareDetailActivity.this, share_media + " 分享成功啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            Toast.makeText(WelfareDetailActivity.this, share_media + " 分享失败啦", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            Toast.makeText(WelfareDetailActivity.this, share_media + " 分享取消啦", Toast.LENGTH_SHORT).show();

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean useSwipeBack() {
        return true;
    }
}
