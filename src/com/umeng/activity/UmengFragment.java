package com.umeng.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.umeng.count.CountManager;

public class UmengFragment extends Fragment {
	private String pageName = "";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobclickAgent.updateOnlineConfig(getActivity());
		MobclickAgent.openActivityDurationTrack(false);
		CountManager.instence(getActivity()).checkUmengConfig();
		pageName = CountManager.instence(getActivity()).getPageName();
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(pageName);
		MobclickAgent.onResume(getActivity(),
				CountManager.instence(getActivity()).getKey(), "");
		
		Log.i(getClass().getSimpleName(), "onResume(): " + CountManager.instence(getActivity()).getKey());
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(pageName);
		MobclickAgent.onPause(getActivity());
	}
}
