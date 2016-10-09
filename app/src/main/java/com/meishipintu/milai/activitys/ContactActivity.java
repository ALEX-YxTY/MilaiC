package com.meishipintu.milai.activitys;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.meishipintu.milai.R;
import com.meishipintu.milai.utils.DialogUtils;
import com.meishipintu.milai.utils.Immersive;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContactActivity extends BaseActivity {

    private static final int REQUEST_CALLPHONE_PERMISSION = 400;

    @BindView(R.id.rl_telephone)
    RelativeLayout rlTelephone;
    @BindView(R.id.rl_email)
    RelativeLayout rlEmail;
    @BindView(R.id.rl_cancel)
    RelativeLayout rlCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.rl_telephone, R.id.rl_email, R.id.rl_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_telephone:
                callPhoneWapper();

                break;
            case R.id.rl_email:
                Email();
                break;
            case R.id.rl_cancel:
                onBackPressed();
                break;
        }
    }

    //申请callPhone权限包装方法
    private void callPhoneWapper() {
        int hasStoragePermission = ContextCompat.checkSelfPermission(this
                , Manifest.permission.CALL_PHONE);
        if (hasStoragePermission != PackageManager.PERMISSION_GRANTED) {        //未授权
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this
                    , android.Manifest.permission.CALL_PHONE)) {    //系统申请权限框不再弹出
                DialogUtils.showCustomDialog(this, "本应用需要获取拨打电话权限"
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(ContactActivity.this, new String[]{android
                                        .Manifest.permission.CALL_PHONE}, REQUEST_CALLPHONE_PERMISSION);
                                dialog.dismiss();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                return;
            }
            //系统框弹出时直接申请
            ActivityCompat.requestPermissions(ContactActivity.this, new String[]{android
                    .Manifest.permission.CALL_PHONE}, REQUEST_CALLPHONE_PERMISSION);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:025-85509799"));//025-85509799
        startActivity(intent);
    }

    @Override
    public boolean useSwipeBack() {
        //使用左滑返回
        return true;
    }

    public void Email() {
        Intent data = new Intent(Intent.ACTION_SENDTO);
        data.setData(Uri.parse("mailto:support@meishipintu.com"));
        data.putExtra(Intent.EXTRA_SUBJECT, "邮件来自米来C端：");
        data.putExtra(Intent.EXTRA_TEXT, "请填写您的账户：");
        startActivity(data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CALLPHONE_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //授权通过
                    callPhoneWapper();
                } else {
                    //拒绝授权
                    Toast.makeText(this, "无拨打电话权限，无法联系客服,请在系统设置中增加应用的相应授权", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }
}
