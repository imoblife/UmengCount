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

		Log.i("11", "1111");
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
			// 检测轮训闹钟是否存活
			countManager.checkUpdateAlartRotation();
			// 检测参数更新
			countManager.updateCountProductData();

		} else if (intent != null
				&& CountManager.COUNT_ACTION_ROTATION_NEWUSER.equals(intent
						.getAction())) {
			CountManager countManager = CountManager.instance(context);
			CountArg countArg = countManager.getAlarmNewUserArg();
			if (getRandomK() < countArg.mRandomK) {

				// 开始统计新用户
				countManager.startCountNewUser();
			}
			// 刷新轮训闹钟的时间。
			countManager.setPrevUpdateAlarmTime(System.currentTimeMillis());

		} else if (intent != null
				&& CountManager.COUNT_ACTION_ROTATION_ODLEUSER.equals(intent
						.getAction())) {
			CountManager countManager = CountManager.instance(context);
			CountArg countArg = countManager.getAlarmOdleUserArg();
			if (getRandomK() < countArg.mRandomK) {
				// 开始统计老用户
				countManager.startCountOdleUser();
			}
			// 刷新轮训闹钟的时间。
			countManager.setPrevUpdateAlarmTime(System.currentTimeMillis());

			// 统计新用户结束
		} else if (intent != null
				&& CountManager.COUNT_ACTION_END_NEWUSER.equals(intent
						.getAction())) {
			// 结束统计
			String name = intent.getStringExtra("name");
			MobclickAgent.onPageEnd(name); // 保证 onPageEnd 在onPause
			MobclickAgent.onPause(context);

			// 统计老用户结束
		} else if (intent != null
				&& CountManager.COUNT_ACTION_END_ODLEUSER.equals(intent
						.getAction())) {
			// 结束统计
			String name = intent.getStringExtra("name");
			MobclickAgent.onPageEnd(name); // 保证 onPageEnd 在onPause
			MobclickAgent.onPause(context);

		}
	}

	private int getRandomK() {
		Random random = new Random();
		return (random.nextInt() >>> 1) % 100;

	}

}
