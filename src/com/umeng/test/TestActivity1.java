package com.umeng.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.umeng.analytics.MobclickAgent;
import com.umeng.count.CountManager;
import com.umeng.count.R;

public class TestActivity1 extends Activity implements OnClickListener {

	public void onCreate(Bundle savedInstanceState) {
		MobclickAgent.setDebugMode(true);
		MobclickAgent.updateOnlineConfig(this);
		MobclickAgent.openActivityDurationTrack(false);
		CountManager.instance(this).checkUmengConfig();
		pageName = CountManager.instance(this).getPageName();
		
		super.onCreate(savedInstanceState);
		this.setTitle(getClass().getSimpleName());
		this.setContentView(R.layout.activity_main);
		findViewById(R.id.hello_tv).setOnClickListener(this);
	}

	private String pageName;

	public void onResume() {
		MobclickAgent.onPageStart(pageName);
		MobclickAgent.onResume(this, CountManager.instance(this).getKey(), "");
		super.onResume();
	}

	public void onPause() {
		MobclickAgent.onPageEnd(pageName);
		MobclickAgent.onPause(this);
		super.onPause();
	}

	public void onClick(View v) {
		startActivity(new Intent(this, TestActivity2.class));
	}

}
