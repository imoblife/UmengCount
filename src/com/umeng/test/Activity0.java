package com.umeng.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.um.test.R;
import com.umeng.activity.UmengActivity;
import com.umeng.count.CountManager;

public class Activity0 extends UmengActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		this.setTitle(getClass().getSimpleName());

		// 检测轮训闹钟是否存活
		CountManager.instance(this).checkUpdateAlartRotation();
		// 检测参数更新
		CountManager.instance(this).updateCountProductData();

		Button button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(Activity0.this, Activity1.class));
			}
		});
	}

}
