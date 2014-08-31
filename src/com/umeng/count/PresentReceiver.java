package com.umeng.count;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.umeng.analytics.MobclickAgent;

public class PresentReceiver extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
		if (intent == null)
			return;

		if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {

			CountManager countManager = CountManager.instance(context);
			countManager.checkAlarm();
			countManager.updateCountProductData();

		} else if (CountManager.COUNT_ACTION_PAGE_START.equals(intent
				.getAction())) {

			CountManager countManager = CountManager.instance(context);
			countManager.startCount();
			countManager.setPrevUpdateAlarmTime(System.currentTimeMillis());

		} else if (CountManager.COUNT_ACTION_PAGE_END
				.equals(intent.getAction())) {
			String name = intent.getStringExtra("name");
			MobclickAgent.onPageEnd(name);
			MobclickAgent.onPause(context);
		}
	}

}
