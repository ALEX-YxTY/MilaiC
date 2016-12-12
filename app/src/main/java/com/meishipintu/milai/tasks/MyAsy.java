package com.meishipintu.milai.tasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;
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

    private Context context;
    private ProgressDialog mProgressDialog;

    public MyAsy(Context context) {
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
        AlertDialog.Builder builder=new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("下载完成").setMessage("新版本已经下载完成，是否安装？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        installApk(context,file);
                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();

        //MyApplication.getInstance().exit();

    }

    @Override
    protected File doInBackground(String... params) {
        final String fileName = "milai_c.apk";
        File tmpFile = new File("/sdcard/milai");
        if (!tmpFile.exists()) {
            tmpFile.mkdir();
        }
        final File file = new File("/sdcard/milai/" + fileName);

        try {
            URL url = new URL(params[0]);
            try {
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                InputStream is = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buf = new byte[1024];
                conn.connect();
                //计算文件长度
                final int lenghtOfFile = conn.getContentLength();
                Activity activity = (Activity) context;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.setMax(lenghtOfFile);
                    }
                });
                float all=lenghtOfFile/1024/1024.0f;

                double count = 0;
                int len1 = 0;
                int  total = 0;//改
                if (conn.getResponseCode() >= 400) {
                    Toast.makeText(context, "连接超时", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    while (count <= 100) {
                        if (is != null) {
                            int numRead = is.read(buf);
                            if (numRead <= 0) {
                                break;
                            } else {
                                len1 = numRead;
                                total += len1; //total = total + len1
//                                    publishProgress("" + total*100/lenghtOfFile);
                                mProgressDialog.setProgress(total);//改
                                float percent=total/1024/1024.0f;
                                mProgressDialog.setProgressNumberFormat(String.format("%.2fMb/%.2fMb",percent,all));
//                                    publishProgress("" + (int)((total*100)/lenghtOfFile));
                                fos.write(buf, 0, numRead);
                            }

                        } else {
                            break;
                        }

                    }
                }

                conn.disconnect();
                fos.close();
                is.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
        } catch (MalformedURLException e) {

            e.printStackTrace();
        }

        return file;
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
