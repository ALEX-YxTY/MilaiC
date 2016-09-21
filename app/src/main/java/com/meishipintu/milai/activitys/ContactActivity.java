package com.meishipintu.milai.activitys;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.meishipintu.milai.R;
import com.meishipintu.milai.utils.Immersive;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContactActivity extends BaseActivity {

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
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:025-85509799"));//025-85509799
                startActivity(intent);
                break;
            case R.id.rl_email:
                Email();
                break;
            case R.id.rl_cancel:
                onBackPressed();
                break;
        }
    }

    public void Email() {
        Intent data = new Intent(Intent.ACTION_SENDTO);
        data.setData(Uri.parse("mailto:support@meishipintu.com"));
        data.putExtra(Intent.EXTRA_SUBJECT, "邮件来自米来C端：");
        data.putExtra(Intent.EXTRA_TEXT, "请填写您的账户：");
        startActivity(data);
    }
}
