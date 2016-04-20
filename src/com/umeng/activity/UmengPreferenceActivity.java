package com.umeng.activity;

import com.umeng.analytics.MobclickAgent;
import com.umeng.count.CountManager;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

public class UmengPreferenceActivity extends PreferenceActivity  implements IUmengTrack  {
	private String pageName = "";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isUmengTrackEnabled()) {
			MobclickAgent.updateOnlineConfig(this);
			MobclickAgent.openActivityDurationTrack(isOpenActivityDurationTrack);
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
		return isUmengTrackEnabled;
	}
}
