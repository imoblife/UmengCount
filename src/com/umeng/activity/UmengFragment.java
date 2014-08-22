package com.umeng.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umeng.count.CountManager;

public class UmengFragment extends Fragment {
	private String pageName;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CountManager.instance(getActivity()).onCreate(getActivity());
		pageName = CountManager.instance(getActivity()).getPageName();
	}

	public void onResume() {
		super.onResume();
		CountManager.instance(getActivity()).onResume(getActivity(), pageName);
	}

	public void onPause() {
		super.onPause();
		CountManager.instance(getActivity()).onPause(getActivity(), pageName);
	}
}
