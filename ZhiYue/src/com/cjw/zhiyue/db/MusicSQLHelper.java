package com.cjw.zhiyue.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MusicSQLHelper extends SQLiteOpenHelper {

	private static final String CREATE_PLAY_LIST="create table playlist("
			+ "id integer primary key autoincrement,"
			+ "song_id char(20),"
			+ "song_name text,"
			+ "songer text)";
	
	private static final String CREATE_HISTORY_LIST="create table historylist("
			+ "id integer primary key autoincrement,"
			+ "searchKey text)";
	
	public MusicSQLHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_PLAY_LIST);//音乐播放列表
		db.execSQL(CREATE_HISTORY_LIST);//搜索历史列表
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
