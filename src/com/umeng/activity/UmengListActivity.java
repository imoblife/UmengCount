package com.umeng.activity;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.umeng.count.CountManager;

public class UmengListActivity extends ListActivity  implements IUmengTrack {
	private String pageName = "";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isUmengTrackEnabled()) {
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
			MobclickAgent.onResume(this);
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
