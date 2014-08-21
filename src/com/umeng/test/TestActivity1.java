package com.umeng.test;

import com.umeng.activity.UmengActivity;
import com.umeng.count.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class TestActivity1 extends UmengActivity implements OnClickListener {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setTitle(getClass().getSimpleName());
		this.setContentView(R.layout.activity_main);
		findViewById(R.id.hello_tv).setOnClickListener(this);
	}

	public void onClick(View v) {
		startActivity(new Intent(this, TestActivity2.class));
	}

}
