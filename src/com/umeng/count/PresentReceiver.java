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

		Log.d("count", "count" + intent.getAction());

		if (intent != null
				&& Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {

			try {
				CountManager countManager = CountManager
						.instance(context);
				// 妫€娴嬭疆璁椆閽熸槸鍚﹀瓨娲?
				countManager.checkUpdateAlartRotation();
				// 妫€娴嬪弬鏁版洿鏂?
				countManager.updateCountProductData();
			} catch (Exception e) {
				// TODO: handle exception
			}

		} else if (intent != null
				&& CountManager.COUNT_ACTION_ROTATION_NEWUSER.equals(intent
						.getAction())) {

			try {
				CountManager countManager = CountManager
						.instance(context);
				CountArg countArg = countManager.getAlarmNewUserArg();
				if (getRandomK() < countArg.mRandomK) {

					// 寮€濮嬬粺璁℃柊鐢ㄦ埛
					countManager.startCountNewUser();
				}
				// 鍒锋柊杞闂归挓鐨勬椂闂淬€?
				countManager.setPrevUpdateAlarmTime(System.currentTimeMillis());
			} catch (Exception e) {
				// TODO: handle exception
			}

		} else if (intent != null
				&& CountManager.COUNT_ACTION_ROTATION_ODLEUSER.equals(intent
						.getAction())) {

			try {
				CountManager countManager = CountManager
						.instance(context);
				CountArg countArg = countManager.getAlarmOdleUserArg();
				if (getRandomK() < countArg.mRandomK) {
					// 寮€濮嬬粺璁¤€佺敤鎴?
					countManager.startCountOdleUser();
				}
				// 鍒锋柊杞闂归挓鐨勬椂闂淬€?
				countManager.setPrevUpdateAlarmTime(System.currentTimeMillis());

				// 缁熻鏂扮敤鎴风粨鏉?
			} catch (Exception e) {
				// TODO: handle exception
			}

		} else if (intent != null
				&& CountManager.COUNT_ACTION_END_NEWUSER.equals(intent
						.getAction())) {

			try {
				// 缁撴潫缁熻
				String name = intent.getStringExtra("name");
				Log.d("countEnd", CountManager.COUNT_ACTION_END_NEWUSER
						+ "end name=" + name);
				MobclickAgent.onPageEnd(name); // 淇濊瘉 onPageEnd 鍦╫nPause
				MobclickAgent.onPause(context);
				// 缁熻鑰佺敤鎴风粨鏉?
			} catch (Exception e) {
				// TODO: handle exception
			}

		} else if (intent != null
				&& CountManager.COUNT_ACTION_END_ODLEUSER.equals(intent
						.getAction())) {
			try {
				// 缁撴潫缁熻
				String name = intent.getStringExtra("name");
				Log.d("countEnd", CountManager.COUNT_ACTION_END_ODLEUSER
						+ " end name= " + name);
				MobclickAgent.onPageEnd(name); // 淇濊瘉 onPageEnd 鍦╫nPause
				MobclickAgent.onPause(context);
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

	private int getRandomK() {
		Random random = new Random();
		return (random.nextInt() >>> 1) % 100;

	}

}

