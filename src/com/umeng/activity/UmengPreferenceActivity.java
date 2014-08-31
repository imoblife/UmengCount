package com.umeng.activity;

import com.umeng.analytics.MobclickAgent;
import com.umeng.count.CountManager;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

public abstract class UmengPreferenceActivity extends PreferenceActivity {
	private String pageName = "";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobclickAgent.updateOnlineConfig(this);
		MobclickAgent.openActivityDurationTrack(false);
		CountManager.instance(this).checkUmengConfigCleared();
		pageName = CountManager.instance(this).getPageName();

		Log.i(getClass().getSimpleName(), "onCreate(): " + pageName);
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(pageName);
		MobclickAgent.onResume(this, CountManager.instance(this).getKey(), "");
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(pageName);
		MobclickAgent.onPause(this);
	}
}
