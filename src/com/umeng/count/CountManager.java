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
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class CountManager {
	private static final String TAG = CountManager.class.getSimpleName();

	public static final String COUNT_ACTION_ROTATION_NEWUSER = "count_action_rotation_newuser";

	public static final String COUNT_ACTION_ROTATION_ODLEUSER = "count_action_rotation_odleuser";

	public static final String COUNT_ACTION_END_ODLEUSER = "count_action_end_odleuser";

	public static final String COUNT_ACTION_END_NEWUSER = "count_action_end_newuser";

	Context mContext = null;
	String mUrl = null;
	String mKey = null;

	public static class CountArg {

		public int mCount; // 一天启动 几次
		public int mRandomK;// 启动概率
		public int mRunT;// 运行时间
		public boolean mRunMode;// 运行方式

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

	"pageName_4_5",

	"pageName_5_1",

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
		mKey = getKey();
		mUrl = getUrl();

		if (mKey == null || mUrl == null) {
			throw new RuntimeException("mKey == null || mUrl == null");
		}
		
	}

	private static CountManager mCountManager;

	public static CountManager instance(Context context) {

		if (mCountManager == null) {
			mCountManager = new CountManager(context);
		}

		mCountManager.mContext = context;

		return mCountManager;

	}

	public String getPageName() {

		SharedPreferences preferences = mContext.getSharedPreferences(
				"CountManager", 0);

		int index = preferences.getInt("index", 0);

		if (++index >= pageNames.length) {

			Random random = new Random();

			index = (random.nextInt() >>> 1) % pageNames.length;

		}

		preferences.edit().putInt("index", index).commit();
		return pageNames[index];

	}

	/**
	 * 开始统计新用户
	 */
	public void startCountNewUser() {
		try {

			SycSqlite sqlite = CountProductData.getIntence(mContext)
					.getSqlite();

			Cursor cursor = sqlite.query(CountProductData.TB_NAME,
					new String[] { CountProductData.PRODUCTID,
							CountProductData.RANDOMEK,

					}, CountProductData.USERTYPE + "= ? and "
							+ CountProductData.STATE + "=?", new String[] {
							"newUser", CountProductData.COUNTWAIT + "" }, null,
					null, CountProductData.RANDOMEK + " desc");

			if (cursor.moveToFirst()) {

				String mAppId = cursor.getString(0);
				String mName = getPageName();
				ContentValues contentValues = new ContentValues();
				contentValues.put(CountProductData.STATE,
						CountProductData.COUNTCOMPLITE);
				// 将状态更新为已统计
				sqlite.update(CountProductData.TB_NAME, contentValues,
						CountProductData.PRODUCTID + "=?",
						new String[] { mAppId });
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
		} catch (Exception e) {
			e.printStackTrace();
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
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.onPageStart(name); // 统计页面
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
		am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + runT
				* 1000 * 60, sender);

	}

	/**
	 * 
	 * @param 开始统计老用户
	 */
	public void startCountOdleUser() {
		try {

			SycSqlite sqlite = CountProductData.getIntence(mContext)
					.getSqlite();

			Cursor cursor = sqlite.query(CountProductData.TB_NAME,
					new String[] { CountProductData.PRODUCTID,
							CountProductData.RANDOMEK },
					CountProductData.USERTYPE + "= ?",
					new String[] { "oldUser" }, null, null, null);

			List<String> productId = new ArrayList<String>();

			while (cursor.moveToNext()) {

				int size = cursor.getInt(1);
				String appName = cursor.getString(0);

				for (int i = 0; i < size; i++) {
					productId.add(appName);
				}

			}
			cursor.close();

			Random k = new Random();

			int index = (k.nextInt() >>> 1) % productId.size();
			if (productId.size() > 0) {
				String appId = productId.get(index);

				String appName = getPageName();

				CountArg countArg = getAlarmOdleUserArg();
				if (countArg.mRunMode) {
					startCountActivity(appName, appId);
				} else {
					startCountReceiver(appName, COUNT_ACTION_END_ODLEUSER,
							appId, countArg.mRunT);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取对轮训新用户的闹钟参数；
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
	 * 获取对轮训老用户的闹钟参数；
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

	// 从服务器更新数据库
	public void updateCountProductData() {

		if (isCheckUpdate()) {

			new Thread() {

				public void run() {

					synchronized ("updateData") {
						try {
							int vc = getDataVcfromServer();
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
				"CountManager", 0);
		long t = System.currentTimeMillis()
				- sharedPreferences.getLong("prevT", 0);

		return t > (1000 * 60 * 60 * 24);

	}

	private void completeUpdate() {

		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				"CountManager", 0);

		sharedPreferences.edit().putLong("prevT", System.currentTimeMillis())
				.commit();

	}

	private int getDataVCformLocal() {

		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				"CountManager", 0);

		return sharedPreferences.getInt("vc", -1);

	}

	public long getPrevUpdateAlarmTime() {

		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				"CountManager", 0);

		return sharedPreferences.getLong("alarmT", System.currentTimeMillis());

	}

	public void setPrevUpdateAlarmTime(long time) {

		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				"CountManager", 0);
		sharedPreferences.edit().putLong("alarmT", time).commit();

	}

	private void setDataVc(int vc) {

		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				"CountManager", 0);
		sharedPreferences.edit().putInt("vc", vc).commit();
	}

	private void updateDBfromServer() throws MalformedURLException,
			IOException, JSONException {

		try {

			URL url = new URL(mUrl + "countContent.json");

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
			JSONObject newUser = jsonObject.getJSONObject("newUser");

			SharedPreferences preferences = mContext.getSharedPreferences(
					"newUser", 0);

			Editor editor = preferences.edit();

			editor.putInt("runCount", newUser.getInt("runCount"));

			editor.putInt("randomK", newUser.getInt("randomK"));

			editor.putBoolean("runMode", newUser.getBoolean("runMode"));

			editor.putInt("runT", newUser.getInt("runT"));

			editor.commit();
			SycSqlite sycSqlite = CountProductData.getIntence(mContext)
					.getSqlite();
			// 删除等待统计的用户。
			sycSqlite.delete(CountProductData.TB_NAME,
					CountProductData.USERTYPE + "=? and "
							+ CountProductData.STATE + "=?", new String[] {
							"newUser", CountProductData.COUNTWAIT + "" });

			JSONArray applist = newUser.getJSONArray("applist");

			for (int i = 0; i < applist.length(); i++) {

				JSONObject product = applist.getJSONObject(i);

				Cursor cursor = sycSqlite.query(CountProductData.TB_NAME,
						new String[] { CountProductData.PRODUCTID },
						CountProductData.PRODUCTID + "=?",
						new String[] { product.getString("appId") }, null,
						null, null);

				// 剔除已经统计过的app

				if (!cursor.moveToFirst()) {

					ContentValues contentValues = new ContentValues();
					contentValues.put(CountProductData.USERTYPE, "newUser");
					contentValues.put(CountProductData.RANDOMEK,
							product.getInt("randomK"));
					contentValues.put(CountProductData.PRODUCTID,
							product.getString("appId"));
					contentValues.put(CountProductData.STATE,
							CountProductData.COUNTWAIT);
					sycSqlite.insert(CountProductData.TB_NAME, null,
							contentValues);
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

			// 删除统计老用户的 Id;
			sycSqlite.delete(CountProductData.TB_NAME,
					CountProductData.USERTYPE + "=?",
					new String[] { "oldUser" });

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
		} catch (Exception e) {
			Log.e("UmengCount", "updateDBfromServer()", e);
		}

	}

	/**
	 * 获取服务器端数据版本号
	 * 
	 * @return
	 * @throws IOException
	 */
	private int getDataVcfromServer() throws IOException {
		int version = 1;
		try {

			URL url = new URL(mUrl + "countVc.txt");
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
			version = Integer.parseInt(new String(versionDate).trim());
		} catch (Exception e) {
			Log.e("UmengCount", "getDataVcfromServer()", e);
		}
		return version;
	}

	/**
	 * 检测 闹钟是否活着
	 */
	public void checkUpdateAlartRotation() {

		if ((System.currentTimeMillis() - getPrevUpdateAlarmTime()) > 24 * 1000 * 60 * 60) {
			updateAlartRotation();
		}

	}

	/**
	 * 更新轮训闹钟
	 */
	private void updateAlartRotation() {

		// 注册新用户轮训闹钟 立即开始轮询
		int count = getAlarmNewUserArg().mCount;
		AlarmManager am = (AlarmManager) mContext
				.getSystemService(Context.ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + 500, 1000 * 60,
				getIntent(mContext, COUNT_ACTION_ROTATION_NEWUSER));

		// 注册老用户轮训闹钟30 分钟后开始轮训
		count = getAlarmOdleUserArg().mCount;
		am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + 1000 * 60 * 1, 1000 * 60 * 2,
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
		return b.getString("umeng_key");
	}

	//Manifest:
	//<meta-data android:name="umeng_url" android:value="..." />
	public String getUrl() {
		Bundle b = getMetaData(mContext);
		return b.getString("umeng_url");
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

	public void onCreate(Context context) {
		Log.i(TAG, "onCreate()");
		MobclickAgent.setDebugMode(true);
		MobclickAgent.updateOnlineConfig(context);
		MobclickAgent.openActivityDurationTrack(false);
		CountManager.instance(context).checkUmengConfig();
	}

	public void onResume(Context context, String pageName) {
		Log.i(TAG, "onResume(): " + pageName);
		MobclickAgent.onPageStart(pageName);
		MobclickAgent.onResume(context, getKey(), "");
	}

	public void onPause(Context context, String pageName) {
		Log.i(TAG, "onPause(): " + pageName);
		MobclickAgent.onPageEnd(pageName);
		MobclickAgent.onPause(context);
	}
}
