package com.umeng.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.umeng.count.CountManager;

public abstract class UmengPreferenceActivity extends PreferenceActivity {
	private String pageName;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CountManager.instance(this).onCreate(this);
		pageName = CountManager.instance(this).getPageName();
	}

	public void onResume() {
		super.onResume();
		CountManager.instance(this).onResume(this, pageName);
	}

	public void onPause() {
		super.onPause();
		CountManager.instance(this).onPause(this, pageName);
	}
}
