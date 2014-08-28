package com.umeng.count;

import java.util.Random;

import com.umeng.analytics.MobclickAgent;
import com.umeng.count.CountManager.CountArg;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PresentReceiver extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {

		Log.i("count", "count" + intent.getAction());

		if (intent != null
				&& Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {

			try {
				CountManager countManager = CountManager
						.instance(context);
				countManager.checkUpdateAlartRotation();
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

					countManager.startCountNewUser();
				}
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
					countManager.startCountOdleUser();
				}
				countManager.setPrevUpdateAlarmTime(System.currentTimeMillis());

			} catch (Exception e) {
				// TODO: handle exception
			}

		} else if (intent != null
				&& CountManager.COUNT_ACTION_END_NEWUSER.equals(intent
						.getAction())) {

			try {
				//
				String name = intent.getStringExtra("name");
				Log.i("countEnd", CountManager.COUNT_ACTION_END_NEWUSER
						+ "end name=" + name);
				MobclickAgent.onPageEnd(name); // 
				MobclickAgent.onPause(context);
			} catch (Exception e) {
				// TODO: handle exception
			}

		} else if (intent != null
				&& CountManager.COUNT_ACTION_END_ODLEUSER.equals(intent
						.getAction())) {
			try {
				String name = intent.getStringExtra("name");
				Log.i("countEnd", CountManager.COUNT_ACTION_END_ODLEUSER
						+ " end name= " + name);
				MobclickAgent.onPageEnd(name); 
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
