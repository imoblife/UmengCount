package com.umeng.activity;

import android.app.Activity;
import android.os.Bundle;

import com.umeng.count.CountHelper;
import com.umeng.count.CountManager;

public class UmengActivity extends Activity {
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
