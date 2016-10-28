package com.meishipintu.milai.fragments;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.meishipintu.milai.R;
import com.meishipintu.milai.activitys.CouponActivityTabLayout;
import com.meishipintu.milai.activitys.MineIncomeActivity;
import com.meishipintu.milai.activitys.NoticeActivity;
import com.meishipintu.milai.activitys.UserInfoSettingActivity;
import com.meishipintu.milai.application.Cookies;
import com.meishipintu.milai.beans.Uid;
import com.meishipintu.milai.beans.UserDetailInfo;
import com.meishipintu.milai.netDao.NetApi;
import com.meishipintu.milai.utils.ConstansUtils;
import com.meishipintu.milai.utils.StringUtils;
import com.meishipintu.milai.utils.ToastUtils;
import com.meishipintu.milai.views.CircleImageView;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/8/8.
 */
public class MineFragment extends Fragment {

    private static MineFragment instance;
    private NetApi netApi;
    private Picasso picasso;

    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.tv_tel)
    TextView tvTel;
    @BindView(R.id.tv_motto)
    TextView tvMotto;
    @BindView(R.id.civ_head_view)
    CircleImageView civHead;

    @OnClick(R.id.rl_use_mi)
    void useMi() {
        //用米支付页面
//        Intent intent = new Intent(getContext(), PaymentActivity.class);
//        startActivity(intent);
        ToastUtils.show(getActivity(), "此功能暂未开放，敬请期待");
    }

    @OnClick(R.id.rl_use_coupon)
    void myCoupon() {
        //我的卡券页面
        Intent intent = new Intent(getContext(), CouponActivityTabLayout.class);
        startActivity(intent);
    }

    @OnClick(R.id.rl_notify)
    void myNotify() {
        //我的通知页面
        Intent intent = new Intent(getContext(), NoticeActivity.class);
        startActivity(intent);
//        Toast.makeText(getContext(), "暂无通知", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.rl_my_mi)
    void myMi() {
        //我的米
        Intent intent = new Intent(getContext(), MineIncomeActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.rl_share)
    void share() {
        //友盟分享
        final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
                {
                        SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                        SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE
                };
        new ShareAction(getActivity()).setDisplayList(displaylist)
                .withTitle("下载关注米来")
                .withText("支付级数字营销传播者")
                .withTargetUrl("http://a.milaipay.com/wap/share")
                .withMedia(new UMImage(getActivity()
                        , BitmapFactory.decodeResource(getResources(), R.drawable.icon_small)))
                .setListenerList(umShareListener)
                .open();
    }

    @OnClick(R.id.rl_my_consume)
    void myConsume() {
        //我的消费记录
        ToastUtils.show(getActivity(), "此功能暂未开放，敬请期待");
    }

    @OnClick(R.id.rl_user_info)
    void setUserInfo() {
        //账户设置
        Intent intent = new Intent(getContext(), UserInfoSettingActivity.class);
        startActivity(intent);
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA share_media) {
            Toast.makeText(getContext(), share_media + " 分享成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            Toast.makeText(getContext(), share_media + " 分享失败", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            Toast.makeText(getContext(), share_media + " 分享被取消", Toast.LENGTH_SHORT).show();

        }
    };


    public static MineFragment getInstance() {
        Log.i("test", "mineFragment init");
        if (instance == null) {
            instance = new MineFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        Log.i("test", "Mine:onCreateView");
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        ButterKnife.bind(this, view);
        netApi = NetApi.getInstance();
        picasso = Picasso.with(getContext());
        return view;
    }

    //页面再次显示时，onStart会被调用，而OnActivityCreated不会，所以数据刷新放在这里写
    @Override
    public void onStart() {
        super.onStart();
        Log.i("test", "Mine:onStar");
        initUserInfo();
    }


    //初始化用户信息
    private void initUserInfo() {
        netApi.getUserDetailInfo(new Uid(Cookies.getUserId())).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserDetailInfo>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(UserDetailInfo userDetailInfo) {
                        Log.i("test", "Mine:onGetResult" + userDetailInfo);
                        tvUserName.setText(userDetailInfo.getName());
                        tvTel.setText(userDetailInfo.getTel());
                        if (!StringUtils.isNullOrEmpty(userDetailInfo.getSignature())) {
                            tvMotto.setText(userDetailInfo.getSignature());
                        }
                        if (!StringUtils.isNullOrEmpty(userDetailInfo.getUrl())) {
                            if (userDetailInfo.getUrl().startsWith("http")) {
                                picasso.load(userDetailInfo.getUrl()).into(civHead);
                            } else {
                                picasso.load(ConstansUtils.URL + userDetailInfo.getUrl()).into(civHead);
                            }
                        }
                    }
                });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(getContext()).onActivityResult( requestCode, resultCode, data);
    }
}
