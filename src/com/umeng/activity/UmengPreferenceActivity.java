package com.umeng.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.umeng.count.CountHelper;
import com.umeng.count.CountManager;

public abstract class UmengPreferenceActivity extends PreferenceActivity {
	private String pageName;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CountHelper.onCreate(getApplicationContext());
		pageName = CountManager.instance(getApplicationContext()).getPageName();
	}

	public void onResume() {
		super.onResume();
		CountHelper.onResume(getApplicationContext(), pageName);
	}

	public void onPause() {
		super.onPause();
		CountHelper.onPause(getApplicationContext(), pageName);
	}
}
