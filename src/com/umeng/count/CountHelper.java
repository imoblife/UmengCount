package com.umeng.count;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.umeng.activity.IUmeng;
import com.umeng.analytics.MobclickAgent;

public class CountHelper {
	private static final String TAG = CountHelper.class.getSimpleName();

	public static void onCreate(Context context) {
		Log.i(TAG, "onCreate()");
		checkUmengCleared(context);
		MobclickAgent.setDebugMode(true);
		MobclickAgent.updateOnlineConfig(context);
		MobclickAgent.openActivityDurationTrack(false);
	}

	public static void onResume(Context context, String pageName) {
		Log.i(TAG, "onResume()");
		MobclickAgent.onPageStart(pageName);
		MobclickAgent.onResume(context, IUmeng.APPKEY, "");
	}

	public static void onPause(Context context, String pageName) {
		Log.i(TAG, "onPause()");
		MobclickAgent.onPageEnd(pageName);
		MobclickAgent.onPause(context);
	}

	public static void checkUmengCleared(Context context) {
		Log.i(TAG, "onCreate()");
		SharedPreferences sp = context.getSharedPreferences(
				context.getPackageName(), 0);
		if (!sp.getBoolean("is_umeng_cleared", false)) {
			sp.edit().putBoolean("is_umeng_cleared", true).commit();
			SharedPreferences umeng_sp = context.getSharedPreferences(
					"umeng_general_config", 0);
			umeng_sp.edit().clear().commit();
		}
	}

}
