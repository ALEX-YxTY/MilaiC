package com.meishipintu.milai.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.client.android.CaptureActivity;
import com.meishipintu.milai.utils.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jpush.android.api.JPushInterface;

public class CaptureHanlderActivity extends CaptureActivity {

	private int mCheckCode;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		Intent in = getIntent();
		mCheckCode = in.getIntExtra("CHECK_CODE", 0);// 0.从主页进入
	}


	public boolean HandleDecoded(String str) {
		if (!StringUtils.isNullOrEmpty(str)) {
			if (mCheckCode == 0) {                    //从主页进入
				Intent data = new Intent();
				data.putExtra("dynamicId", str);
				data.putExtra("CHECK_CODE", mCheckCode);
				Log.i("test", "扫描完毕，mCheckCode=" + Integer.toString(mCheckCode));
				this.setResult(RESULT_OK, data);
				finish();
				return true;
			} else {
				Toast.makeText(this, "无效二维码", Toast.LENGTH_LONG).show();
				finish();
				return false;
			}
		}else{
			Toast.makeText(this, "无效二维码", Toast.LENGTH_LONG).show();
			finish();
			return false;
		}
	}

	public boolean isNumeric(String str){ 
		   Pattern pattern = Pattern.compile("[0-9]*"); 
		   Matcher isNum = pattern.matcher(str);
		   if( !isNum.matches() ){
		       return false; 
		   } 
		   return true; 
		}

	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(this);
	}
}