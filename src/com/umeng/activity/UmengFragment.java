package com.umeng.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.umeng.count.CountManager;

public class UmengFragment extends Fragment  implements IUmengTrack {
	private String pageName = "";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isUmengTrackEnabled()) {
			MobclickAgent.updateOnlineConfig(getActivity());
			MobclickAgent.openActivityDurationTrack(isOpenActivityDurationTrack);
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
		return isUmengTrackEnabled;
	}
}
