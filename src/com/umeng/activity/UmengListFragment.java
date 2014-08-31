package com.umeng.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.umeng.count.CountManager;

public class UmengListFragment extends ListFragment {
	private String pageName = "";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobclickAgent.updateOnlineConfig(getActivity());
		MobclickAgent.openActivityDurationTrack(false);
		CountManager.instance(getActivity()).checkUmengConfigCleared();
		pageName = CountManager.instance(getActivity()).getPageName();
		
		Log.i(getClass().getSimpleName(), "onCreate(): " + pageName);
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(pageName);
		MobclickAgent.onResume(getActivity(),
				CountManager.instance(getActivity()).getKey(), "");
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(pageName);
		MobclickAgent.onPause(getActivity());
	}
}
