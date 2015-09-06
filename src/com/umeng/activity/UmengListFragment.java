package com.umeng.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.umeng.count.CountManager;

public class UmengListFragment extends ListFragment  implements IUmengTrack {
	private String pageName = "";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isUmengTrackEnabled()) {
			MobclickAgent.updateOnlineConfig(getActivity());
			MobclickAgent.openActivityDurationTrack(false);
			CountManager.instance(getActivity()).checkUmengConfig();
			pageName = CountManager.instance(getActivity()).getPageName();
			Log.i(getClass().getSimpleName(), "onCreate(): " + pageName);
		}
	}

	public void onResume() {
		super.onResume();
		if (isUmengTrackEnabled()) {
			MobclickAgent.onPageStart(pageName);
			MobclickAgent.onResume(getActivity(),
					CountManager.instance(getActivity()).getKey(), "");
		}
	}

	public void onPause() {
		super.onPause();
		if (isUmengTrackEnabled()) {
			MobclickAgent.onPageEnd(pageName);
			MobclickAgent.onPause(getActivity());
		}
	}

	@Override
	public boolean isUmengTrackEnabled() {
		return false;
	}
}
