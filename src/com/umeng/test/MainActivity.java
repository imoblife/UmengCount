package com.umeng.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.um.test.R;
import com.umeng.activity.UmengActivity;
import com.umeng.count.CountManager;

public class MainActivity extends UmengActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// 检测轮训闹钟是否存活
		CountManager.instence(this).checkUpdateAlartRotation();
		// 检测参数更新
		CountManager.instence(this).updateCountProductData();

		Button button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, Activity1.class));
			}
		});
	}

}
