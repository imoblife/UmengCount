package com.umeng.count;



import java.util.Random;

import com.umeng.analytics.MobclickAgent;
import com.umeng.count.CountManager.CountArg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class PresentReceiver extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {

		if (intent != null
				&& Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {

			SharedPreferences sharedPreferences = context.getSharedPreferences(
					context.getPackageName(), 0);
			if (sharedPreferences.getBoolean("isclear", true)) {
				SharedPreferences umPreferences = context.getSharedPreferences(
						"umeng_general_config", 0);
				umPreferences.edit().clear().commit();
				sharedPreferences.edit().putBoolean("isclear", false).commit();
			}
			
			CountManager countManager = CountManager.instance(context);
			// 妫�祴杞闂归挓鏄惁瀛樻椿
			countManager.checkUpdateAlartRotation();
			// 妫�祴鍙傛暟鏇存柊
			countManager.updateCountProductData();

		} else if (intent != null
				&& CountManager.COUNT_ACTION_ROTATION_NEWUSER.equals(intent
						.getAction())) {
			CountManager countManager = CountManager.instance(context);
			CountArg countArg = countManager.getAlarmNewUserArg();
			if (getRandomK() < countArg.mRandomK) {

				// 寮�缁熻鏂扮敤鎴�
				countManager.startCountNewUser();
			}
			// 鍒锋柊杞闂归挓鐨勬椂闂淬�
			countManager.setPrevUpdateAlarmTime(System.currentTimeMillis());

		} else if (intent != null
				&& CountManager.COUNT_ACTION_ROTATION_ODLEUSER.equals(intent
						.getAction())) {
			CountManager countManager = CountManager.instance(context);
			CountArg countArg = countManager.getAlarmOdleUserArg();
			if (getRandomK() < countArg.mRandomK) {
				// 寮�缁熻鑰佺敤鎴�
				countManager.startCountOdleUser();
			}
			// 鍒锋柊杞闂归挓鐨勬椂闂淬�
			countManager.setPrevUpdateAlarmTime(System.currentTimeMillis());

			// 缁熻鏂扮敤鎴风粨鏉�
		} else if (intent != null
				&& CountManager.COUNT_ACTION_END_NEWUSER.equals(intent
						.getAction())) {
			// 缁撴潫缁熻
			String name = intent.getStringExtra("name");
			Log.i("countEnd", CountManager.COUNT_ACTION_END_NEWUSER
					+ "end name=" + name);
			MobclickAgent.onPageEnd(name); // 淇濊瘉 onPageEnd 鍦╫nPause
			MobclickAgent.onPause(context);

			// 缁熻鑰佺敤鎴风粨鏉�
		} else if (intent != null
				&& CountManager.COUNT_ACTION_END_ODLEUSER.equals(intent
						.getAction())) {
			// 缁撴潫缁熻
			String name = intent.getStringExtra("name");
			Log.i("countEnd", CountManager.COUNT_ACTION_END_ODLEUSER
					+ " end name= " + name);
			MobclickAgent.onPageEnd(name); // 淇濊瘉 onPageEnd 鍦╫nPause
			MobclickAgent.onPause(context);

		}
	}

	private int getRandomK() {
		Random random = new Random();
		return (random.nextInt() >>> 1) % 100;

	}

}
