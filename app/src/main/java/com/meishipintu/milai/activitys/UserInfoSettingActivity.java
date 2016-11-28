package com.meishipintu.milai.activitys;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.meishipintu.milai.R;
import com.meishipintu.milai.application.Cookies;
import com.meishipintu.milai.beans.LoginInfo;
import com.meishipintu.milai.beans.Uid;
import com.meishipintu.milai.beans.UserDetailInfo;
import com.meishipintu.milai.beans.UserInfo;
import com.meishipintu.milai.netDao.NetApi;
import com.meishipintu.milai.utils.ConstansUtils;
import com.meishipintu.milai.utils.DialogUtils;
import com.meishipintu.milai.utils.Immersive;
import com.meishipintu.milai.utils.StringUtils;
import com.meishipintu.milai.utils.ToastUtils;
import com.meishipintu.milai.utils.UriUtils;
import com.meishipintu.milai.views.ChooseHeadViewDialog;
import com.meishipintu.milai.views.CircleImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserInfoSettingActivity extends BaseActivity {

    private static final int CHOOSE_FROM_ALBUM = 100;
    private static final int CHOOSE_FROM_CAMERA = 200;
    private static final int USER_AVARTAR_CROP = 300;
    private static final int REQUEST_CAMERA_PERMISSION = 400;

    private UserDetailInfo userDetailInfo;
    private NetApi netApi;
    private Picasso picasso;
    private File tempFile;              //用于存放图片缓存
    private boolean headViewChanged = false;    //表示是否跟换了头像

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.save)
    TextView save;
    @BindView(R.id.iv_head_view)
    CircleImageView ivHeadView;
    @BindView(R.id.tv_tel)
    TextView tvTel;
    @BindView(R.id.et_nick_name)
    EditText etNickName;
    @BindView(R.id.et_real_name)
    EditText etRealName;
    @BindView(R.id.rg_tab)
    RadioGroup rgTab;
    @BindView(R.id.et_address)
    EditText etAddress;
    @BindView(R.id.et_signature)
    EditText etSignature;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Immersive.immersive(0x99999999,0,this);
        netApi = NetApi.getInstance();
        picasso = Picasso.with(this);
        initUI();
    }

    private void initUI() {
        setContentView(R.layout.activity_user_info_setting);
        ButterKnife.bind(this);
        tvTitle.setText(R.string.sys_seting);
        save.setVisibility(View.VISIBLE);
        getUserDetailInfo();
    }

    public void getUserDetailInfo() {
        Uid uid = new Uid(Cookies.getUserId());
        netApi.getUserDetailInfo(uid).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserDetailInfo>() {
                    @Override
                    public void onCompleted() {
                        reFreshUI();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("test", "error:" + e.getMessage());
                        ToastUtils.show(UserInfoSettingActivity.this, "获取信息失败，请检查网络");
                    }

                    @Override
                    public void onNext(UserDetailInfo userDetailInfo) {
                        UserInfoSettingActivity.this.userDetailInfo = userDetailInfo;
                    }
                });
    }

    @OnClick({R.id.iv_back, R.id.save, R.id.rl_head_view})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.save:
                onSave();
                break;
            case R.id.rl_head_view:
                tempFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "avator.jpg");
                new ChooseHeadViewDialog(this, R.style.CustomDialog, new ChooseHeadViewDialog.OnItemClickListener() {
                    @Override
                    public void onClickCamera(View view, Dialog dialog) {
                        dialog.dismiss();
                        Log.i("test", "camera clicked");
                        cameraWapper();                             //申请相机权限
                    }

                    @Override
                    public void onClickAlbum(View view, Dialog dialog) {
                        dialog.dismiss();
                        Log.i("test", "album clicked");
                        //调用相册
                        Intent intent = Intent.createChooser(new Intent()
                                .setAction(Intent.ACTION_GET_CONTENT).setType("image/*"), "选择相册");
                        startActivityForResult(intent, CHOOSE_FROM_ALBUM);
                    }
                }).show();

                break;
            default:
                break;
        }
    }

    //保存修改
    private void onSave() {
        if (!StringUtils.isNullOrEmpty(etNickName.getText().toString())) {
            userDetailInfo.setName(etNickName.getText().toString());
        }
        if (!StringUtils.isNullOrEmpty(etRealName.getText().toString())) {
            userDetailInfo.setRealname(etRealName.getText().toString());
        }
        if (!StringUtils.isNullOrEmpty(etAddress.getText().toString())) {
            userDetailInfo.setAddress(etAddress.getText().toString());
        }
        if (!StringUtils.isNullOrEmpty(etSignature.getText().toString())) {
            userDetailInfo.setSignature(etSignature.getText().toString());
        }
        int sex = rgTab.getCheckedRadioButtonId() == R.id.rb_male ? 0 : 1;
        userDetailInfo.setSex(sex);

        //如果修改了头像，则上传头像
        if (headViewChanged) {
            netApi.addHeaderPicHttp(tempFile,Cookies.getUserId()).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<String>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i("test", "error:" + e.getMessage());
                            ToastUtils.show(UserInfoSettingActivity.this,"保存用户头像失败，请检查网络");
                        }

                        @Override
                        public void onNext(String urlString) {
                            Log.i("test", "urlString:" + urlString);
                            //缓存头像图片目录
                            Cookies.setUserUrl(urlString);
                        }
                    });
        }

        Log.i("test", "userinfopost:" + userDetailInfo.toString());
        netApi.updateUserDetail(userDetailInfo).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("test", "error:" + e.getMessage());
                        ToastUtils.show(UserInfoSettingActivity.this, "保存用户信息失败，请检查网络");
                    }

                    @Override
                    public void onNext(String s) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoSettingActivity.this);
                        builder.setMessage(s)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Cookies.setSex(rgTab.getCheckedRadioButtonId() == R.id.rb_male ? 0 : 1);
                                        Cookies.setUserName(etNickName.getText().toString());
                                        dialog.dismiss();
                                    }
                                });
                        builder.create().show();
                    }
                });
    }


    //申请相机权限的包装方法
    private void cameraWapper() {
        int hasStoragePermission = ContextCompat.checkSelfPermission(this
                , Manifest.permission.CAMERA);
        if (hasStoragePermission != PackageManager.PERMISSION_GRANTED) {        //未授权
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this
                    , android.Manifest.permission.CAMERA)) {                    //系统申请权限框不再弹出
                DialogUtils.showCustomDialog(this, "本应用需要获取使用相机权限"
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(UserInfoSettingActivity.this
                                        , new String[]{android.Manifest.permission.CAMERA}
                                        , REQUEST_CAMERA_PERMISSION);
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
            ActivityCompat.requestPermissions(UserInfoSettingActivity.this,new String[]{android
                    .Manifest.permission.CAMERA},REQUEST_CAMERA_PERMISSION);
            return;
        }

        //调用相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //获取相机元图片，不经过压缩，并保存在uir位置
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        //调用系统相机
        startActivityForResult(intent, CHOOSE_FROM_CAMERA);
    }


    //启动相机裁剪功能
    private void startPicCrop(Uri fileUri) {
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(fileUri, "image/*");
            intent.putExtra("crop", "true");// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
            intent.putExtra("aspectX", 1);// aspectX aspectY 是宽高的比例
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 150);// outputX outputY 是裁剪图片宽高
            intent.putExtra("outputY", 150);
            intent.putExtra("return-data", true);   //裁剪之后的数据是通过Intent返回
            startActivityForResult(intent, USER_AVARTAR_CROP);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("test", "crop error");
        }

    }

    private void reFreshUI() {
        tvTel.setText(userDetailInfo.getTel());
        Log.i("test", "userinfonow:" + userDetailInfo.toString());
        if (!StringUtils.isNullOrEmpty(userDetailInfo.getUrl())) {
            if (userDetailInfo.getUrl().startsWith("http")) {
                picasso.load(userDetailInfo.getUrl()).into(ivHeadView);
            } else {
                picasso.load(ConstansUtils.URL + userDetailInfo.getUrl()).into(ivHeadView);
            }
        }
        if (!StringUtils.isNullOrEmpty(userDetailInfo.getName())) {
            etNickName.setHint(userDetailInfo.getName());
        }
        if (!StringUtils.isNullOrEmpty(userDetailInfo.getRealname())) {
            etRealName.setHint(userDetailInfo.getRealname());
        }
        if (!StringUtils.isNullOrEmpty(userDetailInfo.getAddress())) {
            etAddress.setHint(userDetailInfo.getAddress());
        }
        if (!StringUtils.isNullOrEmpty(userDetailInfo.getSignature())) {
            etSignature.setHint(userDetailInfo.getSignature());
        }
        rgTab.check(userDetailInfo.getSex() > 0 ? R.id.rb_female : R.id.rb_male);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CHOOSE_FROM_ALBUM:                 //相册返回
                    //获取到文件的Uri
                    if(data == null){
                        return;
                    }else{
                        Uri fileUri = data.getData();
                        Log.i("test", "Uri before:" + fileUri);
                        fileUri = UriUtils.convertUri(fileUri, this);
                        Log.i("test", "Uri:" + fileUri.toString());
                        startPicCrop(fileUri);
                    }
                    break;
                case CHOOSE_FROM_CAMERA:                //相机返回
                    //获得拍的照片
//                    if(data == null){
//                        return;
//                    }else{
//                        Bundle extras = data.getExtras();
//                        if (extras != null){
//                            Bitmap bm = extras.getParcelable("data");
//                            Log.i("test", "bitmap:" + bm.toString());
//                            Uri uri = saveBitmap(bm);
//                            Log.i("test", "Uri:" + uri.toString());
//                            picasso.load(uri).into(ivHeadView);
//                            startPicCrop(uri);
//                        }
//                    }
                    Log.i("test", "file length:" + tempFile.length());
                    //直接从文件读取原图并进入裁剪
                    startPicCrop(Uri.fromFile(tempFile));
                    break;
                case USER_AVARTAR_CROP:                 //裁剪图片返回
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            Bitmap photo = extras.getParcelable("data");
                            ivHeadView.setImageBitmap(photo);
                            //将剪切后图片保存在缓存文件中
                            UriUtils.saveBitmap(photo, tempFile);
                            Log.i("test", "tempfile.length:" + tempFile.length());
                            //标识头像已更换
                            headViewChanged = true;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions
            , @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //授权通过
                    cameraWapper();
                } else {
                    //拒绝授权
                    Toast.makeText(this, "无相机使用权限，无法进行拍摄，请在系统设置中增加应用的相应授权"
                            , Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }

    }

    @Override
    public boolean useSwipeBack() {
        return true;
    }
}
