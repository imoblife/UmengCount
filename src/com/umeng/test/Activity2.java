package com.umeng.test;

import android.os.Bundle;

import com.umeng.activity.UmengActivity;
import com.umeng.count.R;

public class Activity2 extends UmengActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		this.setTitle(getClass().getSimpleName());
	}

    @Override
    public String getTrackModule() {
        return getClass().getSimpleName();
    }
}
