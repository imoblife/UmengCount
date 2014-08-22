package com.umeng.activity;

import com.umeng.analytics.MobclickAgent;
import com.umeng.count.CountManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

public class UmengFragment extends Fragment {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			MobclickAgent.updateOnlineConfig(getActivity());

			MobclickAgent.setDebugMode(true);
			MobclickAgent.openActivityDurationTrack(false);
			SharedPreferences sharedPreferences = getActivity()
					.getSharedPreferences(getActivity().getPackageName(), 0);
			if (sharedPreferences.getBoolean("isclear", true)) {
				SharedPreferences umPreferences = getActivity()
						.getSharedPreferences("umeng_general_config", 0);
				umPreferences.edit().clear().commit();
				sharedPreferences.edit().putBoolean("isclear", false).commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String Appname;

	public void onResume() {
		super.onResume();
		try {
			if (Appname == null) {
				Appname = CountManager.getCountInstenc(getActivity())
						.getPageName();
			}
			MobclickAgent.onPageStart(Appname);
			MobclickAgent.onResume(getActivity(), IUmeng.APPKEY, "");
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
				Appname = CountManager.getCountInstenc(getActivity())
						.getPageName();
			}
			MobclickAgent.onPageEnd(Appname);
			MobclickAgent.onPause(getActivity());
			Log.i("111", "onPause(): " + Appname + " = "
					+ getClass().getSimpleName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
