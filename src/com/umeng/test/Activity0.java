package com.umeng.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.umeng.activity.UmengActivity;
import com.umeng.count.CountManager;
import com.umeng.count.R;

public class Activity0 extends UmengActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		this.setTitle(getClass().getSimpleName());

		Button button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(Activity0.this, Activity1.class));
			}
		});
	}

	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// �����ѵ�����Ƿ���
		CountManager.instance(this).checkAlarm();
		// ���������
		CountManager.instance(this).updateCountProductData();
	}
}
