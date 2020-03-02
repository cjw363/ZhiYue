package com.cjw.zhiyue.db.Dao;

import java.util.ArrayList;
import java.util.List;

import com.cjw.zhiyue.db.MusicSQLHelper;
import com.cjw.zhiyue.utils.UIUtils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class HistoryListDao {

	private static HistoryListDao historyListDao;

	private MusicSQLHelper mSQHelper;

	public HistoryListDao() {
		mSQHelper = new MusicSQLHelper(UIUtils.getContext(), "SongList.db", null, 1);
	}

	public static HistoryListDao getHistoryListDao() {
		if (historyListDao == null) {
			historyListDao = new HistoryListDao();
		}
		return historyListDao;
	}
	
	/**
	 * 存储歌曲信息
	 */
	public void insert(String searchKey) {
		SQLiteDatabase db = mSQHelper.getWritableDatabase();

		Cursor cursor = db.query("historylist", new String[]{"searchKey"}, "searchKey=?", new String[] { searchKey }, null,
				null, null);
		if (!cursor.moveToFirst()) {
			ContentValues values = new ContentValues();
			values.put("searchKey", searchKey);
			db.insert("historylist", null, values);
		}
		cursor.close();
		db.close();
	}
	
	/**
	 * 删除一条歌曲，通过song_id
	 */
	public void delete(String searchKey) {
		SQLiteDatabase db = mSQHelper.getWritableDatabase();
		db.delete("historylist", "searchKey=?", new String[] { searchKey });
		db.close();
	}
	
	/**
	 * @return 查询所有存储的歌曲信息
	 */
	public List<String> query() {
		SQLiteDatabase db = mSQHelper.getWritableDatabase();
		Cursor cursor = db.query("historylist", null, null, null, null, null, "id desc");
		if (cursor.getCount() > 0) {
			ArrayList<String> list = new ArrayList<String>();

			while (cursor.moveToNext()) {
				String searchKey = cursor.getString(cursor.getColumnIndex("searchKey"));
				list.add(searchKey);
			}
			db.close();
			cursor.close();
			return list;
		}
		db.close();
		cursor.close();
		return null;
	}

}
