package com.umeng.count;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CountProductData extends SQLiteOpenHelper {

	public static final String TB_NAME = "userCount";// 表名字
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
			mCountProduct = new CountProductData(context, "count.db", null, 1);
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

		db.execSQL("CREATE TABLE IF NOT EXISTS " + TB_NAME + " (" + RANDOMEK
				+ " INTEGER," + PRODUCTID + " text PRIMARY KEY," + RUNT
				+ " text," + STATE + " INTEGER," + USERTYPE + " text)");

	}

	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

	}

}
