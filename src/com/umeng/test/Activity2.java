package com.umeng.test;

import android.os.Bundle;

import com.um.test.R;
import com.umeng.activity.UmengActivity;

public class Activity2 extends UmengActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		this.setTitle(getClass().getSimpleName());
	}
}
