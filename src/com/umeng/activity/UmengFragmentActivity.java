package com.umeng.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.umeng.count.CountManager;

public class UmengFragmentActivity extends FragmentActivity  implements IUmengTrack {
	private String pageName = "";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isUmengTrackEnabled()) {
			MobclickAgent.updateOnlineConfig(this);
			MobclickAgent.openActivityDurationTrack(false);
			CountManager.instance(this).checkUmengConfig();
			pageName = CountManager.instance(this).getPageName();
			Log.i(getClass().getSimpleName(), "onCreate(): " + pageName);
		}
	}

	public void onResume() {
		super.onResume();
		if (isUmengTrackEnabled()) {
			MobclickAgent.onPageStart(pageName);
			MobclickAgent.onResume(this, CountManager.instance(this).getKey(), "");
		}
	}

	public void onPause() {
		super.onPause();
		if (isUmengTrackEnabled()) {
			MobclickAgent.onPageEnd(pageName);
			MobclickAgent.onPause(this);
		}
	}

	@Override
	public boolean isUmengTrackEnabled() {
		return false;
	}
}
