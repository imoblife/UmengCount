package com.umeng.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.umeng.count.CountManager;

public abstract class UmengFragmentActivity extends FragmentActivity {
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
