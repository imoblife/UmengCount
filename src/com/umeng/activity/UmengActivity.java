package com.umeng.activity;

import com.umeng.analytics.MobclickAgent;
import com.umeng.count.CountManager;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class UmengActivity extends Activity {
	private String pageName = "";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobclickAgent.updateOnlineConfig(this);
		MobclickAgent.openActivityDurationTrack(false);
		CountManager.instence(this).checkUmengConfig();
		pageName = CountManager.instence(this).getPageName();
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(pageName);
		MobclickAgent.onResume(this, CountManager.instence(this).getKey(), "");
		
		Log.i(getClass().getSimpleName(), "onResume(): " + CountManager.instence(this).getKey());
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(pageName);
		MobclickAgent.onPause(this);
	}
}
