package com.umeng.count;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.umeng.analytics.MobclickAgent;

public class CountActivity extends Activity {

	Handler handler = new Handler();
	String mName;
	String mAppId;

	protected void onCreate(Bundle savedInstanceState) {
		MobclickAgent.openActivityDurationTrack(false);
		super.onCreate(savedInstanceState);
		mName = getIntent().getStringExtra("name");
		mAppId = getIntent().getStringExtra("appId");

	}

	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(mName); // 统计页面
		MobclickAgent.onResume(this, mAppId, null);
		handler.postDelayed(new Runnable() {

			public void run() {
				finish();
			}
		}, 300);
	}

	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(mName); // 保证 onPageEnd 在onPause
		MobclickAgent.onPause(this);

	}

}

