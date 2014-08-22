package com.umeng.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.umeng.count.CountManager;

public abstract class UmengFragmentActivity extends FragmentActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			MobclickAgent.updateOnlineConfig(this);

			MobclickAgent.setDebugMode(true);
			MobclickAgent.openActivityDurationTrack(false);
			SharedPreferences sharedPreferences = getSharedPreferences(
					getPackageName(), 0);
			if (sharedPreferences.getBoolean("isclear", true)) {
				SharedPreferences umPreferences = getSharedPreferences(
						"umeng_general_config", 0);
				umPreferences.edit().clear().commit();
				sharedPreferences.edit().putBoolean("isclear", false).commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected String getTag() {
		return "untracked";
	}

	private String Appname;

	public void onResume() {
		super.onResume();
		try {
			if (Appname == null) {
				Appname = CountManager.getCountInstenc(this).getPageName();
			}
			MobclickAgent.onPageStart(Appname);
			MobclickAgent.onResume(this, IUmeng.APPKEY, "");
			Log.i("111", "onResume(): " + Appname + " = "
					+ getClass().getSimpleName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onPause() {
		super.onPause();
		try {
			if (Appname == null) {
				Appname = CountManager.getCountInstenc(this).getPageName();
			}
			MobclickAgent.onPageEnd(Appname);
			MobclickAgent.onPause(this);
			Log.i("111", "onPause(): " + Appname + " = "
					+ getClass().getSimpleName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
