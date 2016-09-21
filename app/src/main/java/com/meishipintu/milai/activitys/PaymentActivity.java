package com.meishipintu.milai.activitys;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.client.android.QrUtil;
import com.meishipintu.milai.R;
import com.meishipintu.milai.application.Cookies;
import com.meishipintu.milai.utils.Immersive;
import com.meishipintu.milai.utils.JniTest;
import com.meishipintu.milai.utils.StringUtils;
import com.meishipintu.milai.views.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/8/19 0019.
 */
public class PaymentActivity extends BaseActivity {


    private Picasso picasso;
    private String key;//6位加密码
    private String code;
    private Timer timer;
    private QrUtil mQrUtil;


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.iv_QRcode)
    ImageView ivQRcode;
    @BindView(R.id.headportrait)
    CircleImageView headportrait;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Immersive.immersive(0x99999999,0,this);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);

        mQrUtil = new QrUtil();
        //禁止截屏
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        picasso = Picasso.with(this);
        if (!StringUtils.isNullOrEmpty(Cookies.getUserUrl())) {
            picasso.load(Cookies.getUserUrl()).into(headportrait);
        }
        tvName.setText(Cookies.getUserName());
        tvPhone.setText(Cookies.getTel());
        tvTitle.setText(R.string.pay_money);
        tvName.setText(Cookies.getUserName());
        initEnCodeString();//执行动作

        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                initEnCodeString();//执行动作
            }
        };
        timer.schedule(task,60000,60000);

    }

    private void initEnCodeString() {
        key=JniTest.getTotp(Cookies.getTel(), (int) (System.currentTimeMillis()/1000/60));//传入电话和时间戳
//        String ran3 = "";
//        String ran4 = "";
//        Random random = new Random(System.currentTimeMillis());
//        for (int i = 0; i < 3; i++) {
//            ran3 += random.nextInt(10) + "";
//        }
//        for (int i = 0; i < 4; i++) {
//            ran4 += random.nextInt(10) + "";
//        }
//        String source = ran3 + Cookies.getUserId() + ran4 + Cookies.getTel();
//
//        try {
//            // 从字符串中得到公钥
//            // PublicKey publicKey = RSAUtils.loadPublicKey(PUCLIC_KEY);
//            // 从文件中得到公钥
//            InputStream inPublic = getResources().getAssets().open("milaip.pem");
//            PublicKey publicKey = RsaUtils.loadPublicKey(inPublic);
//            // 加密
//            byte[] encryptByte = RsaUtils.encryptData(source.getBytes(), publicKey);
//            afterencrypt = Base64.encodeToString(encryptByte, Base64.DEFAULT);
            //生成二维码
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        code="47"+(Integer.parseInt(key)*9785935849L+Long.parseLong(Cookies.getTel().substring(1)));
        Log.i("test", "tel:" + Cookies.getTel() + ",totp:" + key + ",code:" + code);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mQrUtil.createQRCodeImage(code, ivQRcode);
            }
        });
    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        onBackPressed();
    }

//    private void initDecodeString() {
//        try
//        {
//            // 从字符串中得到私钥
//            // PrivateKey privateKey = RSAUtils.loadPrivateKey(PRIVATE_KEY);
//            // 从文件中得到私钥
//            InputStream inPrivate = getResources().getAssets().open("milaicpr.pem");
//            PrivateKey privateKey = RsaUtils.loadPrivateKey(inPrivate);
//            byte[] decryptByte = RsaUtils.decryptData(Base64.decode(afterencrypt, Base64.DEFAULT), privateKey);
//            String decryptStr = new String(decryptByte);
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

}


