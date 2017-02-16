package com.meishipintu.milai.activitys;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.meishipintu.milai.R;
import com.meishipintu.milai.application.Cookies;
import com.meishipintu.milai.application.RxBus;
import com.meishipintu.milai.beans.Task;
import com.meishipintu.milai.listener.UmListener;
import com.meishipintu.milai.netDao.NetApi;
import com.meishipintu.milai.utils.ConstansUtils;
import com.meishipintu.milai.utils.DateUtils;
import com.meishipintu.milai.utils.Immersive;
import com.meishipintu.milai.utils.StringUtils;
import com.meishipintu.milai.utils.ToastUtils;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class WelfareDetailNewActivity extends AppCompatActivity {

    @BindView(R.id.sv)
    ScrollView sv;
    @BindView(R.id.bt_participate)
    Button btParticipate;
    @BindView(R.id.iv_main)
    ImageView ivMain;
    @BindView(R.id.iv_1)
    ImageView ivLikes;
    @BindView(R.id.likes_number)
    TextView tvLikes;
    @BindView(R.id.tv_forward_number)
    TextView tvForward;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_description)
    TextView tvDescription;
    @BindView(R.id.tv_business)
    TextView tvBusiness;

    private Task data;
    private NetApi netApi;
    private Picasso picasso;
    private UMShareListener umShareListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Immersive.immersive(0x99999999, 0, this);
        setContentView(R.layout.activity_welfare_detail_new);
        ButterKnife.bind(this);
        netApi = NetApi.getInstance();
        picasso = Picasso.with(this);
        Initialization();
    }

    // 初始化
    private void Initialization() {
        Intent intent = getIntent();
        data = (Task) intent.getSerializableExtra("data");
        sv.setBackgroundColor(intent.getIntExtra("foregroundColor", 0xffffff));
        btParticipate.setBackgroundColor(intent.getIntExtra("buttonColor", 0xffffff));
        picasso.load("http://" + data.getLogo()).placeholder(R.drawable.bg_erweima).into(ivMain);
        tvName.setText(data.getTitle());
        tvTime.setText(DateUtils.getTimePeriod(data.getStart_time(), data.getEnd_time()));
        tvDescription.setText(data.getAc_detail());
        tvBusiness.setText(data.getShop_detail());
        //获取活动实时点赞转发信息
        netApi.getEachTask(data.getId(), Cookies.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Task>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("test", "error:" + e.getMessage());
                    }

                    @Override
                    public void onNext(Task task) {
                        Log.i("test", "task:" + task.toString());
                        tvLikes.setText(data.getLikes());
                        tvForward.setText(data.getForward());
                        ivLikes.setImageResource("1".equals(data.getIslikes()) ? R.drawable.icon_like_sel : R.drawable.icon_like_nor);
                    }
                });
    }

    @OnClick({R.id.bt_likes, R.id.bt_forward, R.id.bt_participate, R.id.iv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            //点赞
            case R.id.bt_likes:
                Log.i("test", "task is likes:" + data.getIslikes());
                if ("1".equals(data.getIslikes())) {
                    ToastUtils.show(this, "已收藏~");
                } else {
                    ivLikes.setImageResource(R.drawable.icon_like_sel);
                    ivLikes.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_up));
                    doLikes(data.getId());
                    data.setIslikes("1");
                }
                break;
            //转发
            case R.id.bt_forward:
                final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
                        {SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,};
                umShareListener = new UmListener(this) {
                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        String type = "1";  //微信
                        Log.i("onResult", "分享成功");
                        if (share_media.equals(SHARE_MEDIA.WEIXIN_CIRCLE)) {
                            type = "2";//朋友圈
                        }
                        doForward(data.getId(), type);
                        super.onResult(share_media);
                    }

                };
                new ShareAction(this).setDisplayList(displaylist)
                        .withTitle("下载关注米来")
                        .withText("支付级数字营销传播者")
                        .withTargetUrl(data.getType_detail())
                        .withMedia(new UMImage(this
                                , BitmapFactory.decodeResource(this.getResources(), R.drawable.icon_small)))
                        .setListenerList(umShareListener)
                        .open();
                break;
            //参与活动
            case R.id.bt_participate:
                if (StringUtils.isNullOrEmpty(data.getType_detail())) {
                    ToastUtils.show(this, "即将上线");
                } else if (StringUtils.isNullOrEmpty(Cookies.getUserId())) {
                    RxBus.getDefault().send(ConstansUtils.LOGIN_FIRST);
                    this.finish();
                } else {
                    String url = data.getType_detail() + "/uid/" + Cookies.getUserId();
                    Intent intent = new Intent(this, TaskDetailActivity.class);
                    intent.putExtra("detail", url);
                    intent.putExtra("type", 1);     //米来红包
                    startActivity(intent);
                }
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
        overridePendingTransition(R.anim.anim_in_alpha, R.anim.anim_out_scale);
    }

    //点赞调用
    private void doLikes(String id) {
        netApi.likes(Cookies.getUserId(), id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("test", "error:" + e.getMessage());
                        ToastUtils.show(WelfareDetailNewActivity.this, "点赞失败，请检查网络");
                    }

                    @Override
                    public void onNext(String s) {
                        tvLikes.setText(s);
                    }
                });
    }

    //转发调用
    private void doForward(String taskId, String type) {
        netApi.doForward(Cookies.getUserId(), taskId, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("test", "error:" + e.getMessage());
                        ToastUtils.show(WelfareDetailNewActivity.this, "转发失败，请检查网络");
                    }

                    @Override
                    public void onNext(String s) {
                        tvForward.setText(s);
                    }
                });

    }
}
