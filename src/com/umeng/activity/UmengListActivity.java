package com.umeng.activity;

import android.app.ListActivity;
import android.os.Bundle;

import com.umeng.count.CountManager;

public abstract class UmengListActivity extends ListActivity {
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
