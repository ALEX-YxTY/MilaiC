package com.meishipintu.milai.tasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.meishipintu.milai.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2016/8/29.
 */
public class MyAsy extends AsyncTask<String,String,File> {

    private String versionCode;
    private Context context;
    private ProgressDialog mProgressDialog;

    public MyAsy(Context context,String versionCode) {
        this.versionCode = versionCode;
        this.context = context;
        this.mProgressDialog = new ProgressDialog(context);
    }

    //点击调用界面
    @Override
    protected void onPreExecute() {
        mProgressDialog.setMessage("下载文件...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }
    //点击调用后界面
    @Override
    protected void onPostExecute(final File file) {
        mProgressDialog.dismiss();
        if (file != null) {
            AlertDialog.Builder builder=new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("下载完成").setMessage("新版本已经下载完成，是否安装？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            installApk(context,file);
                            dialog.dismiss();
                        }
                    }).setNegativeButton("取消", null);
            builder.show();
        }else {
            Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    protected File doInBackground(String... params) {
        final String fileName = "milai_c.apk";
        File tmpFile = new File("/sdcard/milai/"+versionCode);
        if (!tmpFile.exists()) {
            tmpFile.mkdir();
        }
        final File file = new File("/sdcard/milai/"+versionCode+"/" + fileName);
        URL url;
        try {
            url = new URL(params[0]);
            HttpURLConnection conn = (HttpURLConnection) url
                    .openConnection();
            conn.connect();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.i("test", "网络错误，service return：" + conn.getResponseCode()
                        + ";" + conn.getResponseMessage());
            } else {
                InputStream is = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(file);
                try {
                    is = conn.getInputStream();
                    fos = new FileOutputStream(file);
                    byte[] buf = new byte[1024];
                    //计算文件长度
                    final int lenghtOfFile = conn.getContentLength();
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.setMax(lenghtOfFile);
                        }
                    });
                    float all = lenghtOfFile / 1024 / 1024.0f;
                    int numRead;
                    int total = 0;
                    while ((numRead = is.read(buf)) != -1) {
                        total += numRead;
                        mProgressDialog.setProgress(total);
                        float percent = total / 1024 / 1024.0f;
                        mProgressDialog.setProgressNumberFormat(String.format("%.2fMb/%.2fMb", percent, all));
                        fos.write(buf, 0, numRead);
                    }

                    return file;

                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    fos.flush();
                    fos.close();
                    is.close();
                }
            }
            conn.disconnect();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onProgressUpdate(String... progress) {
        mProgressDialog.setProgress(Integer.parseInt(progress[0]));
    }

    /* 安装apk */
    public static void installApk(Context context, File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

}
