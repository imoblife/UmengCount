package com.umeng.count;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CountProductData extends SQLiteOpenHelper {

	public static final String TB_NAME = "userCount";// 琛ㄥ悕瀛�
	public static final String APP_KEY = "appKey";
	public static final String COUNT_OF_DAY = "countOfDay";
	public static final String PAGE_DURATION = "pageDuration";

	static CountProductData instance;

	private SQLiteDatabase database;

	public static CountProductData getIntance(Context context) {
		Log.d("count", "getIntence");

		if (instance == null) {
			instance = new CountProductData(context, "count.db", null, 3);
		}

		return instance;
	}

	public SycSqlite getSqlite() {
		Log.d("count", "getSqlite");

		if (database == null || !database.isOpen()) {
			database = getWritableDatabase();
		}
		return new SycSqlite(database);

	}

	private CountProductData(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		Log.d("count", "CountProductData");
		// TODO Auto-generated constructor stub
	}

	public void onCreate(SQLiteDatabase db) {
		Log.d("count", "onCreate");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + TB_NAME + " ("
				+ APP_KEY+ " TEXT PRIMARY KEY not null," + COUNT_OF_DAY + " INTEGER," + PAGE_DURATION + " INTEGER)");

	}

	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		Log.d("count", "onUpgrade");
		db.execSQL("DROP TABLE " + TB_NAME);

		onCreate(db);
	}

}
