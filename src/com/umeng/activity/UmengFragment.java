package com.umeng.activity;

import com.umeng.count.CountHelper;
import com.umeng.count.CountManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class UmengFragment extends Fragment {
	private String pageName;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CountHelper.onCreate(getActivity());
		pageName = CountManager.instance(getActivity()).getPageName();
	}

	public void onResume() {
		super.onResume();
		CountHelper.onResume(getActivity(), pageName);
	}

	public void onPause() {
		super.onPause();
		CountHelper.onPause(getActivity(), pageName);
	}
}
