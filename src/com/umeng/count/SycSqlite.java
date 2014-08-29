package com.umeng.count;


import junit.framework.Assert;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.CancellationSignal;
import android.util.Log;

public class SycSqlite {

	SQLiteDatabase database;

	public SycSqlite(SQLiteDatabase database) {
		this.database = database;
	}

	public Cursor query(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy) {
		Log.d("count", "query");
		Assert.assertNotNull(database);
		synchronized (SycSqlite.class) {
			return database.query(table, columns, selection, selectionArgs,
					groupBy, having, orderBy);
		}

	}

	public Cursor query(boolean distinct, String table, String[] columns,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy, String limit) {
		Log.d("count", "query");
		Assert.assertNotNull(database);
		synchronized (SycSqlite.class) {
			return database.query(distinct, table, columns, selection,
					selectionArgs, groupBy, having, orderBy, limit);
		}

	}

	public long insert(String table, String nullColumnHack, ContentValues values) {
		Log.d("count", "insert");
		Assert.assertNotNull(database);
		synchronized (SycSqlite.class) {
			return database.insert(table, nullColumnHack, values);
		}
	}

	public int delete(String table, String whereClause, String[] whereArgs) {
		Log.d("count", "delete");
		Assert.assertNotNull(database);
		synchronized (SycSqlite.class) {
			return database.delete(table, whereClause, whereArgs);
		}
	}

	public int update(String table, ContentValues values, String whereClause,
			String[] whereArgs) {
		Log.d("count", "update");
		Assert.assertNotNull(database);
		synchronized (SycSqlite.class) {
			return database.update(table, values, whereClause, whereArgs);
		}

	}

	// public long replace(String table, String nullColumnHack,
	// ContentValues initialValues) {
	// Assert.assertNotNull(database);
	// synchronized (SycSqlite.class) {
	// return database.replace(table, nullColumnHack, initialValues);
	// }
	//
	// }
	//
	// public long replaceOrThrow(String table, String nullColumnHack,
	// ContentValues initialValues) {
	// Assert.assertNotNull(database);
	// synchronized (SycSqlite.class) {
	// return database
	// .replaceOrThrow(table, nullColumnHack, initialValues);
	// }
	// }

	public Cursor rawQuery(String sqlitCommand) {
		Log.d("count", "rawQuery");

		synchronized (SycSqlite.class) {
			return database.rawQuery(sqlitCommand, null);
		}

	}

	public void beginTransaction() {
		Log.d("count", "beginTransaction");
		synchronized (SycSqlite.class) {
			database.beginTransaction();
		}
	}

	public void setTransactionSuccessful() {
		Log.d("count", "setTransactionSuccessful");
		synchronized (SycSqlite.class) {
			database.setTransactionSuccessful();
		}
	}

	public void endTransaction() {
		Log.d("count", "endTransaction");
		synchronized (SycSqlite.class) {
			database.endTransaction();
		}

	}

}
