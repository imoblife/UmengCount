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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class CountManager {

	public static final String COUNT_ACTION_ROTATION_NEWUSER = "count_action_rotation_newuser";

	public static final String COUNT_ACTION_ROTATION_ODLEUSER = "count_action_rotation_odleuser";

	public static final String COUNT_ACTION_END_ODLEUSER = "count_action_end_odleuser";

	public static final String COUNT_ACTION_END_NEWUSER = "count_action_end_newuser";

	private final String CountName = "CountManagerV1";

	Context mContext;

	public static class CountArg {

		public int mCount; //
		public int mRandomK;//
		public int mRunT;//
		public boolean mRunMode;//

	}

	private static String[] pageNames = {

	"pageName_1_1",

	"pageName_1_2",

	"pageName_1_3",

	"pageName_1_4",

	"pageName_1_5",

	"pageName_2_1",

	"pageName_2_2",

	"pageName_2_3",

	"pageName_2_4",

	"pageName_2_5",

	"pageName_3_1",

	"pageName_3_2",

	"pageName_3_3",

	"pageName_3_4",

	"pageName_3_5",

	"pageName_4_1",

	"pageName_4_2",

	"pageName_4_3",

	"pageName_4_4",

	"pageName_4_5", "pageName_5_1",

	"pageName_5_2",

	"pageName_5_3",

	"pageName_5_4",

	"pageName_5_5"

	};

	/**
	 * 
	 * @param context
	 * @param url
	 */
	private CountManager(Context context) {

		mContext = context;

		MobclickAgent.setDebugMode(getDebug());

		if (getKey() == null || getUrl() == null) {
			throw new RuntimeException("getKey() == null || getUrl() == null");
		}

	}

	private static CountManager mCountManager;

	public static CountManager instence(Context context) {

		if (mCountManager == null) {
			mCountManager = new CountManager(context);
		}

		mCountManager.mContext = context;

		return mCountManager;

	}

	public String getPageName() {

		SharedPreferences preferences = mContext.getSharedPreferences(
				CountName, 0);

		int index = preferences.getInt("index", 0);

		if (++index >= pageNames.length) {

			Random random = new Random();

			index = (random.nextInt() >>> 1) % pageNames.length;

		}

		preferences.edit().putInt("index", index).commit();
		return pageNames[index];

	}

	/**
	 */
	public void startCountNewUser() {

		SycSqlite sqlite = CountProductData.getIntence(mContext).getSqlite();

		Cursor cursor = sqlite.query(CountProductData.TB_NAME, new String[] {
				CountProductData.PRODUCTID, CountProductData.RANDOMEK,

		}, CountProductData.USERTYPE + "= ? and " + CountProductData.STATE
				+ "=?", new String[] { "newUser",
				CountProductData.COUNTWAIT + "" }, null, null,
				CountProductData.RANDOMEK + " desc");

		if (cursor.moveToFirst()) {

			String mAppId = cursor.getString(0);
			String mName = getPageName();
			ContentValues contentValues = new ContentValues();
			contentValues.put(CountProductData.STATE,
					CountProductData.COUNTCOMPLITE);
			sqlite.update(CountProductData.TB_NAME, contentValues,
					CountProductData.PRODUCTID + "=?", new String[] { mAppId });
			CountArg countArg = getAlarmNewUserArg();
			if (countArg.mRunMode) {
				startCountActivity(mName, mAppId);
			} else {
				startCountReceiver(mName, COUNT_ACTION_END_NEWUSER, mAppId,
						countArg.mRunT);
			}
			cursor.close();

		} else {
			cursor.close();
		}

	}

	private void startCountActivity(String name, String appId) {
		MobclickAgent.openActivityDurationTrack(false);
		Intent intent = new Intent(mContext, CountActivity.class);
		intent.putExtra("name", name);
		intent.putExtra("appId", appId);
		mContext.startActivity(intent);
	}

	private void startCountReceiver(String name, String action, String appId,
			int runT) {

		Log.i("countStart", "start  action=" + action + " appId=" + appId
				+ " name=" + name + " runT=" + runT);

		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.onPageStart(name); //
		MobclickAgent.onResume(mContext, appId, null);
		setEndAlarmTime(mContext, action, runT, name, appId);

	}

	private void setEndAlarmTime(Context context, String action, int runT,
			String name, String appId) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(action);

		intent.putExtra("name", name);
		intent.putExtra("appId", appId);

		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 3 * 1000,
				sender);

	}

	/**
	 * 
	 */
	public void startCountOdleUser() {

		SycSqlite sqlite = CountProductData.getIntence(mContext).getSqlite();

		Cursor cursor = sqlite.query(CountProductData.TB_NAME, new String[] {
				CountProductData.PRODUCTID, CountProductData.RANDOMEK },
				CountProductData.USERTYPE + "= ?", new String[] { "oldUser" },
				null, null, null);

		List<String> productId = new ArrayList<String>();

		while (cursor.moveToNext()) {

			int size = cursor.getInt(1);
			String appName = cursor.getString(0);

			for (int i = 0; i < size; i++) {
				productId.add(appName);
			}

		}
		cursor.close();

		if (productId.size() < 1) {

			return;
		}

		Random k = new Random();

		int index = (k.nextInt() >>> 1) % productId.size();
		Log.i("count", "odle product list =" + productId.size() + "index="
				+ index);
		if (productId.size() > 0) {
			String appId = productId.get(index);

			String appName = getPageName();

			CountArg countArg = getAlarmOdleUserArg();
			if (countArg.mRunMode) {
				startCountActivity(appName, appId);
			} else {
				startCountReceiver(appName, COUNT_ACTION_END_ODLEUSER, appId,
						countArg.mRunT);
			}

		}

	}

	/**
	 * 
	 * @return
	 */
	public CountArg getAlarmNewUserArg() {
		CountArg countArg = new CountArg();
		SharedPreferences preferences = mContext.getSharedPreferences(
				"newUser", 0);
		countArg.mCount = preferences.getInt("runCount", 24);
		countArg.mRandomK = preferences.getInt("randomK", 0);
		countArg.mRunT = preferences.getInt("runT", 5);

		return countArg;

	}

	/**
	 * 
	 * @return
	 */
	public CountArg getAlarmOdleUserArg() {

		CountArg countArg = new CountArg();
		SharedPreferences preferences = mContext.getSharedPreferences(
				"oldUser", 0);
		countArg.mCount = preferences.getInt("runCount", 24);
		countArg.mRandomK = preferences.getInt("randomK", 0);
		countArg.mRunT = preferences.getInt("runT", 5);

		return countArg;

	}

	public void updateCountProductData() {

		Log.i("count", "updateCountProductData");

		if (isCheckUpdate()) {

			Log.i("count", "updateCountProductData isCheckUpdate true");

			new Thread() {

				public void run() {

					synchronized ("updateData") {
						try {
							int vc = getDataVcfromServer();
							Log.i("count", "count vc=" + vc);
							if (vc > getDataVCformLocal()) {
								updateDBfromServer();
								setDataVc(vc);
								completeUpdate();
								updateAlartRotation();
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

	private boolean isCheckUpdate() {

		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				CountName, 0);
		long t = System.currentTimeMillis()
				- sharedPreferences.getLong("prevT", 0);

		return t > (1000 * 60 * 60);

	}

	private void completeUpdate() {

		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				CountName, 0);

		sharedPreferences.edit().putLong("prevT", System.currentTimeMillis())
				.commit();

	}

	private int getDataVCformLocal() {

		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				CountName, 0);

		return sharedPreferences.getInt("vc", -1);

	}

	public long getPrevUpdateAlarmTime() {

		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				CountName, 0);

		return sharedPreferences.getLong("alarmT", System.currentTimeMillis());

	}

	public void setPrevUpdateAlarmTime(long time) {

		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				CountName, 0);
		sharedPreferences.edit().putLong("alarmT", time).commit();

	}

	private void setDataVc(int vc) {

		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				CountName, 0);
		sharedPreferences.edit().putInt("vc", vc).commit();
	}

	private void updateDBfromServer() throws MalformedURLException,
			IOException, JSONException {

		URL url = new URL(getUrl() + "countContent.json");

		HttpURLConnection mHttpURLConnection = (HttpURLConnection) url
				.openConnection();
		byte[] content = new byte[mHttpURLConnection.getContentLength()];

		InputStream inputStream = mHttpURLConnection.getInputStream();
		BufferedInputStream bufferedInputStream = new BufferedInputStream(
				inputStream);
		bufferedInputStream.read(content);
		bufferedInputStream.close();
		inputStream.close();
		mHttpURLConnection.disconnect();

		JSONObject jsonObject = new JSONObject(new String(content).trim());
		Log.i("countContent", jsonObject.toString());
		JSONObject newUser = jsonObject.getJSONObject("newUser");

		SharedPreferences preferences = mContext.getSharedPreferences(
				"newUser", 0);

		Editor editor = preferences.edit();

		editor.putInt("runCount", newUser.getInt("runCount"));

		editor.putInt("randomK", newUser.getInt("randomK"));

		editor.putBoolean("runMode", newUser.getBoolean("runMode"));

		editor.putInt("runT", newUser.getInt("runT"));

		editor.commit();
		SycSqlite sycSqlite = CountProductData.getIntence(mContext).getSqlite();
		sycSqlite.delete(CountProductData.TB_NAME, CountProductData.USERTYPE
				+ "=? and " + CountProductData.STATE + "=?", new String[] {
				"newUser", CountProductData.COUNTWAIT + "" });

		JSONArray applist = newUser.getJSONArray("applist");

		for (int i = 0; i < applist.length(); i++) {

			JSONObject product = applist.getJSONObject(i);

			Cursor cursor = sycSqlite.query(CountProductData.TB_NAME,
					new String[] { CountProductData.PRODUCTID },
					CountProductData.PRODUCTID + "=?",
					new String[] { product.getString("appId") }, null, null,
					null);

			if (!cursor.moveToFirst()) {

				ContentValues contentValues = new ContentValues();
				contentValues.put(CountProductData.USERTYPE, "newUser");
				contentValues.put(CountProductData.RANDOMEK,
						product.getInt("randomK"));
				contentValues.put(CountProductData.PRODUCTID,
						product.getString("appId"));
				contentValues.put(CountProductData.STATE,
						CountProductData.COUNTWAIT);
				sycSqlite.insert(CountProductData.TB_NAME, null, contentValues);
			}

			cursor.close();

		}

		//
		JSONObject odle = jsonObject.getJSONObject("oldUser");
		preferences = mContext.getSharedPreferences("oldUser", 0);
		editor = preferences.edit();
		editor.putInt("runCount", odle.getInt("runCount"));
		editor.putInt("randomK", odle.getInt("randomK"));
		editor.putBoolean("runMode", odle.getBoolean("runMode"));
		editor.putInt("runT", odle.getInt("runT"));
		editor.commit();

		sycSqlite.delete(CountProductData.TB_NAME, CountProductData.USERTYPE
				+ "=?", new String[] { "oldUser" });

		applist = odle.getJSONArray("applist");

		for (int i = 0; i < applist.length(); i++) {

			JSONObject product = applist.getJSONObject(i);

			ContentValues contentValues = new ContentValues();
			contentValues.put(CountProductData.USERTYPE, "oldUser");
			contentValues.put(CountProductData.RANDOMEK,
					product.getInt("randomK"));
			contentValues.put(CountProductData.PRODUCTID,
					product.getString("appId"));
			contentValues.put(CountProductData.STATE,
					CountProductData.COUNTWAIT);
			sycSqlite.insert(CountProductData.TB_NAME, null, contentValues);
		}

	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */

	private int getDataVcfromServer() throws IOException {
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
		Log.i("sv", "version" + version);
		return version;
	}

	/**
	 */
	public void checkUpdateAlartRotation() {

		if ((System.currentTimeMillis() - getPrevUpdateAlarmTime()) > 1000 * 60 * 60) {
			updateAlartRotation();
		}

	}

	/**
	 */
	private void updateAlartRotation() {

		int count = getAlarmNewUserArg().mCount;
		AlarmManager am = (AlarmManager) mContext
				.getSystemService(Context.ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + 500, 60 * 1000,
				getIntent(mContext, COUNT_ACTION_ROTATION_NEWUSER));

		count = getAlarmOdleUserArg().mCount;
		am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + 1000, 60 * 1000,
				getIntent(mContext, COUNT_ACTION_ROTATION_ODLEUSER));
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

	//Manifest:
	//<meta-data android:name="umeng_key" android:value="..." />
	public String getKey() {
		Bundle b = getMetaData(mContext);
		String s = b.getString("umeng_key");
		Log.i(getClass().getSimpleName(), "getKey(): " + s);
		return s;
	}

	//Manifest:
	//<meta-data android:name="umeng_url" android:value="..." />
	public String getUrl() {
		Bundle b = getMetaData(mContext);
		String s = b.getString("umeng_url");
		Log.i(getClass().getSimpleName(), "getUrl(): " + s);
		return s;
	}

	//Manifest:
	//<meta-data android:name="umeng_url" android:value="..." />
	public boolean getDebug() {
		Bundle b = getMetaData(mContext);
		String s = b.getString("umeng_debug");
		Log.i(getClass().getSimpleName(), "getDebug(): " + s);
		return "true".equals(s);
	}

	public void checkUmengConfig() {
		SharedPreferences sp = mContext.getSharedPreferences(
				mContext.getPackageName(), 0);
		if (sp.getBoolean("is_umeng_config_cleared", true)) {
			sp.edit().putBoolean("is_umeng_config_cleared", false).commit();
			SharedPreferences umeng_sp = mContext.getSharedPreferences(
					"umeng_general_config", 0);
			umeng_sp.edit().clear().commit();
		}
	}

	// no use
	//	public void onCreate(Context context) {
	//		Log.i(getClass().getSimpleName(), "onCreate()");
	//		checkUmengConfig();
	//		MobclickAgent.setDebugMode(true);
	//		MobclickAgent.updateOnlineConfig(context);
	//		MobclickAgent.openActivityDurationTrack(false);
	//	}
	//
	//	public void onResume(Context context, String pageName) {
	//		Log.i(getClass().getSimpleName(), "onResume(): " + pageName);
	//		MobclickAgent.onPageStart(pageName);
	//		MobclickAgent.onResume(context, "53fd49fdfd98c5c577028b2c", "");
	//	}
	//
	//	public void onPause(Context context, String pageName) {
	//		Log.i(getClass().getSimpleName(), "onPause(): " + pageName);
	//		MobclickAgent.onPageEnd(pageName);
	//		MobclickAgent.onPause(context);
	//	}
}
