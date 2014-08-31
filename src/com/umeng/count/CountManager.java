package com.umeng.count;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.RuleBasedCollator;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

public class CountManager {

	public static final String COUNT_ACTION_PAGE_START = "count_action_rotation_odleuser";

	public static final String COUNT_ACTION_PAGE_END = "count_action_end_odleuser";

	private static final String PREF_NAME = "CountManagerV3";

	private static CountManager instance;

	private Context mContext;

	public static class CountArg {

		public String mAppKey;
		public int mCount; // count times in 24h
		public int mSessionDuration;// page session duration

		public CountArg(String appKey, int countOfDay, int pageDuration) {
			mAppKey = appKey;
			mCount = countOfDay;
			mSessionDuration = pageDuration;
		}
	}

	private CountManager(Context context) {

		mContext = context;

		MobclickAgent.setDebugMode(getDebug());

		if (getKey() == null || getUrl() == null) {
			Log.e(getClass().getSimpleName(),
					"getKey() == null || getUrl() == null");
		}

	}

	public static CountManager instance(Context context) {

		if (instance == null) {
			instance = new CountManager(context);
		}

		instance.mContext = context;

		return instance;

	}

	public String getPageName() {
		int r1 = new Random().nextInt() >>> 1 % 5;
		int r2 = new Random().nextInt() >>> 1 % 5;
		return "pageName_" + r1 + "_" + r2;
	}

	private void startCountReceiver(String name, String action, String appId,
			int runT) {
		Log.d("count", "startCountReceiver");

		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.onPageStart(name); // 缁熻椤甸潰
		MobclickAgent.onResume(mContext, appId, null);
		setEndAlarmTime(mContext, action, runT, name, appId);

	}

	private void setEndAlarmTime(Context context, String action, int runT,
			String name, String appId) {
		Log.d("count", "setEndAlarmTime");
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(action);

		intent.putExtra("name", name);
		intent.putExtra("appId", appId);

		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + runT
				* 1000 * 60, sender);

	}

	public void startCount() {
		Log.d("count", "startCountOdleUser");

		SycSqlite db = CountProductData.getIntance(mContext).getSqlite();
		Cursor c = db.query(CountProductData.TB_NAME, new String[] {
				CountProductData.APP_KEY, CountProductData.COUNT_OF_DAY, CountProductData.PAGE_DURATION },
				null, null,
				null, null, null);

		List<CountArg> productId = new ArrayList<CountArg>();
		while (c.moveToNext()) {

			int size = c.getInt(1);
			String appKey = c.getString(0);
			int countOfDay = c.getInt(1);
			int pageDuration = c.getInt(2);

			for (int i = 0; i < size; i++) {
				productId.add(new CountArg(appKey, countOfDay, pageDuration));
			}

		}
		c.close();

		if (productId.size() < 1) {

			return;
		}

		int r = (new Random().nextInt() >>> 1) % productId.size();
		Log.d("count", "odle product list =" + productId.size() + "index=" + r);
		if (productId.size() > 0) {
			String appId = productId.get(r).mAppKey;
			String pageName = getPageName();
			startCountReceiver(pageName, COUNT_ACTION_PAGE_END, appId,
					productId.get(r).mSessionDuration);
		}

	}

//	public CountArg getAlarmArg() {
//		Log.d("count", "getAlarmOdleUserArg");
//
//		SharedPreferences preferences = mContext.getSharedPreferences(
//				"oldUser", 0);
//		countArg.mCount = preferences.getInt("runCount", 24);
//		countArg.mSessionDuration = preferences.getInt("runT", 5);
//
//		return new CountArg();
//
//	}

	// 浠庢湇鍔″櫒鏇存柊鏁版嵁搴�
	public void updateCountProductData() {

		Log.d("count", "updateCountProductData");

		if (isTimeUp()) {

			Log.d("count", "updateCountProductData isCheckUpdate true");

			new Thread() {

				public void run() {

					synchronized ("updateData") {
						try {
							int vc = loadDataVcfromServer();
							Log.d("count", "count vc=" + vc);
							if (vc > getDataVCfromLocal()) {
								updateDBfromServer();
								setDataVc(vc);
								completeUpdate();
								checkAlarm();
								setPrevUpdateAlarmTime(System
										.currentTimeMillis());
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

				}
			}.start();

		}

	}

	private boolean isTimeUp() {
		SharedPreferences sp = mContext.getSharedPreferences(PREF_NAME, 0);
		long t = System.currentTimeMillis() - sp.getLong("prevT", 0);
		return t > (1000 * 60 * 60 * 24);
	}

	private void completeUpdate() {

		Log.d("count", "completeUpdate");

		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				PREF_NAME, 0);

		sharedPreferences.edit().putLong("prevT", System.currentTimeMillis())
				.commit();

	}

	private int getDataVCfromLocal() {

		Log.d("count", "getDataVCformLocal");

		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				PREF_NAME, 0);

		return sharedPreferences.getInt("vc", -1);

	}

	public long getPrevUpdateAlarmTime() {
		Log.d("count", "getPrevUpdateAlarmTime");

		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				PREF_NAME, 0);

		return sharedPreferences.getLong("alarmT", System.currentTimeMillis());

	}

	public void setPrevUpdateAlarmTime(long time) {
		Log.d("count", "setPrevUpdateAlarmTime");

		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				PREF_NAME, 0);
		sharedPreferences.edit().putLong("alarmT", time).commit();

	}

	private void setDataVc(int vc) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				PREF_NAME, 0);
		sharedPreferences.edit().putInt("vc", vc).commit();
	}

	private void updateDBfromServer() throws MalformedURLException,
			IOException, JSONException {
		URL url = new URL(getUrl() + "countContent.json");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		byte[] content = new byte[conn.getContentLength()];

		InputStream is = conn.getInputStream();
		BufferedInputStream bis = new BufferedInputStream(is);
		bis.read(content);
		bis.close();
		is.close();
		conn.disconnect();

		JSONObject jo = new JSONObject(new String(content).trim());
		JSONObject odle = jo.getJSONObject("oldUser");
		SycSqlite sycSqlite = CountProductData.getIntance(mContext).getSqlite();
		JSONArray applist = odle.getJSONArray("applist");
		for (int i = 0; i < applist.length(); i++) {
			JSONObject joApp = applist.getJSONObject(i);
			ContentValues cv = new ContentValues();
			cv.put(CountProductData.APP_KEY, joApp.getString("key"));
			cv.put(CountProductData.COUNT_OF_DAY, joApp.getInt("countOfDay"));
			cv.put(CountProductData.PAGE_DURATION, joApp.getInt("pageDuration"));
			sycSqlite.insert(CountProductData.TB_NAME, null, cv);
		}

	}


	private int queryCount() {
		SycSqlite db = CountProductData.getIntance(mContext).getSqlite();
		Cursor c = db.query(CountProductData.TB_NAME, new String[] {
				"SUM("+ CountProductData.COUNT_OF_DAY+")"},
				null, null,
				null, null, null);
		if(c.moveToNext()) {
			return c.getInt(0);
		} else {
			return 1;
		}
		
	}
	
	private int loadDataVcfromServer() throws IOException {
		URL url = new URL(getUrl() + "countVc.txt");
		HttpURLConnection mHttpURLConnection = (HttpURLConnection) url
				.openConnection();
		byte[] versionDate = new byte[mHttpURLConnection.getContentLength()];
		InputStream inputStream = mHttpURLConnection.getInputStream();
		BufferedInputStream bufferedInputStream = new BufferedInputStream(
				inputStream);
		bufferedInputStream.read(versionDate);
		bufferedInputStream.close();
		inputStream.close();
		mHttpURLConnection.disconnect();
		int version = Integer.parseInt(new String(versionDate).trim());
		Log.d("sv", "version" + version);
		return version;
	}

	public void checkAlarm() {
		if ((System.currentTimeMillis() - getPrevUpdateAlarmTime()) > 24 * 1000 * 60 * 60) {
			startAlarm();
		}
	}

	private void startAlarm() {
		int count = queryCount();
		long interval = 24 * 1000 * 60 * 60 / count;
		AlarmManager am = (AlarmManager) mContext
				.getSystemService(Context.ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				interval, getIntent(mContext, COUNT_ACTION_PAGE_START));
	}

	private PendingIntent getIntent(Context context, String action) {
		Intent intent = new Intent(context, PresentReceiver.class);
		intent.setAction(action);
		return PendingIntent.getBroadcast(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
	}

	// http://stackoverflow.com/questions/19379349/android-get-manifest-meta-data-out-of-activity
	// http://yidongkaifa.iteye.com/blog/1780444
	public Bundle getMetaData(Context context) {
		Log.d("count", "getMetaData");
		Bundle result = null;
		try {
			result = context.getPackageManager().getApplicationInfo(
					context.getPackageName(), PackageManager.GET_META_DATA).metaData;
		} catch (Exception e) {
			throw new RuntimeException(
					"Could not read meta data in the manifest.", e);
		}
		return result;
	}

	// Manifest:
	// <meta-data android:name="umeng_key" android:value="..." />
	public String getKey() {
		Bundle b = getMetaData(mContext);
		String s = b.getString("umeng_key");
		Log.i(getClass().getSimpleName(), "getKey(): " + s);
		return s;
	}

	// Manifest:
	// <meta-data android:name="umeng_url" android:value="..." />
	public String getUrl() {
		Bundle b = getMetaData(mContext);
		String s = b.getString("umeng_url");
		Log.i(getClass().getSimpleName(), "getUrl(): " + s);
		return s;
	}

	// Manifest:
	// <meta-data android:name="umeng_debug" android:value="true" />
	public boolean getDebug() {
		Bundle b = getMetaData(mContext);
		boolean s = b.getBoolean("umeng_debug", false);
		Log.i(getClass().getSimpleName(), "getDebug(): " + s);
		return s;
	}

	public void checkUmengConfigCleared() {
		SharedPreferences sp = mContext.getSharedPreferences(
				mContext.getPackageName(), 0);
		if (sp.getBoolean("is_umeng_config_cleared", true)) {
			sp.edit().putBoolean("is_umeng_config_cleared", false).commit();
			SharedPreferences umeng_sp = mContext.getSharedPreferences(
					"umeng_general_config", 0);
			umeng_sp.edit().clear().commit();
		}
	}
}
