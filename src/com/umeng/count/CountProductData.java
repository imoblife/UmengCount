package com.umeng.count;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CountProductData extends SQLiteOpenHelper {

	public static final String TB_NAME = "userCount";// 
	public static final String RANDOMEK = "randomk";
	public static final String USERTYPE = "usertype";
	public static final String PRODUCTID = "productId";
	public static final String STATE = "state";
	public static final String RUNT = "runT";

	public static final int COUNTWAIT = 1;

	public static final int COUNTCOMPLITE = 2;

	static CountProductData mCountProduct;

	SQLiteDatabase database;
	Context mContext;

	public static CountProductData getIntence(Context context) {

		if (mCountProduct == null) {
			mCountProduct = new CountProductData(context, "count.db", null, 2);
		}
		mCountProduct.mContext = context;

		return mCountProduct;

	}

	public SycSqlite getSqlite() {

		if (database == null || !database.isOpen()) {
			database = getWritableDatabase();
		}
		return new SycSqlite(database);

	}

	private CountProductData(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public void onCreate(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE IF NOT EXISTS " + TB_NAME + " ("
				+ "ID INTEGER PRIMARY KEY autoincrement not null," + RANDOMEK
				+ " INTEGER," + PRODUCTID + " text," + RUNT + " text," + STATE
				+ " INTEGER," + USERTYPE + " text)");

	}

	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE " + TB_NAME);

		onCreate(db);
	}

}
