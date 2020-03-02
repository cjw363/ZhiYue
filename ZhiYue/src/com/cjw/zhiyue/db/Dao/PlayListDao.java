package com.cjw.zhiyue.db.Dao;

import java.util.ArrayList;
import java.util.List;

import com.cjw.zhiyue.db.MusicSQLHelper;
import com.cjw.zhiyue.db.domain.SongInfo;
import com.cjw.zhiyue.utils.UIUtils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PlayListDao {

	private static PlayListDao playListDao;

	private MusicSQLHelper mSQHelper;

	public PlayListDao() {
		mSQHelper = new MusicSQLHelper(UIUtils.getContext(), "SongList.db", null, 1);
	}

	public static PlayListDao getPlayListDao() {
		if (playListDao == null) {
			playListDao = new PlayListDao();
		}
		return playListDao;
	}

	/**
	 * 存储歌曲信息
	 */
	public void insert(String song_id, String song_name, String songer) {
		SQLiteDatabase db = mSQHelper.getWritableDatabase();

		Cursor cursor = db.query("playlist", new String[] { "song_id" }, "song_id=?", new String[] { song_id }, null,
				null, null);
		if (!cursor.moveToFirst()) {
			ContentValues values = new ContentValues();
			values.put("song_id", song_id);
			values.put("song_name", song_name);
			values.put("songer", songer);
			db.insert("playlist", null, values);
		}
		cursor.close();
		db.close();
	}

	/**
	 * 删除一条歌曲，通过song_id
	 */
	public void delete(String song_id) {
		SQLiteDatabase db = mSQHelper.getWritableDatabase();
		db.delete("playlist", "song_id=?", new String[] { song_id });
		db.close();
	}

	/**
	 * @return 查询所有存储的歌曲信息
	 */
	public List<SongInfo> query() {
		SQLiteDatabase db = mSQHelper.getWritableDatabase();
		Cursor cursor = db.query("playlist", null, null, null, null, null, "id desc");
		if (cursor.getCount() > 0) {
			ArrayList<SongInfo> list = new ArrayList<SongInfo>();

			while (cursor.moveToNext()) {
				SongInfo songInfo = new SongInfo();

				songInfo.song_id = cursor.getString(cursor.getColumnIndex("song_id"));
				songInfo.song_name = cursor.getString(cursor.getColumnIndex("song_name"));
				songInfo.songer = cursor.getString(cursor.getColumnIndex("songer"));
				list.add(songInfo);
			}
			db.close();
			cursor.close();
			return list;
		}
		db.close();
		cursor.close();
		return null;
	}

	/**
	 * 查询下一条歌曲
	 */
	public String queryNextSong(String song_id){
		SQLiteDatabase db = mSQHelper.getWritableDatabase();
		if(song_id==null){//当前没有歌曲在播放，自动播放列表第一个(id最后一个)
			
			Cursor firstCursor = db.query("playlist", null, null, null, null, null, "id desc");
			if(firstCursor.moveToFirst()){
				String nextSong_id= firstCursor.getString(firstCursor.getColumnIndex("song_id"));
				db.close();
				return nextSong_id;
			}else{//数据库没歌曲
				db.close();
				return null;
			}
			
		}else{//当前有歌曲在播放
			
			Cursor cursorId = db.query("playlist", new String[]{"id"}, "song_id=?", new String[]{song_id}, null, null, null);
			if(cursorId.moveToFirst()){
				int id = cursorId.getInt(cursorId.getColumnIndex("id"));
				Cursor cursor = db.query("playlist", null, "id<?", new String[]{id+""}, null, null, null);
				if(cursor.moveToLast()){
					String nextSong_id= cursor.getString(cursor.getColumnIndex("song_id"));
					db.close();
					return nextSong_id;
				}else{
					//没有下一曲了
					db.close();
					return null;
				}
			}
			
			db.close();
			return null;
		}
	}
	
	/**
	 * 查询上一条歌曲
	 */
	public String queryPreviouSong(String song_id){
		SQLiteDatabase db = mSQHelper.getWritableDatabase();
		if(song_id==null){//当前没有歌曲在播放，自动播放列表最后一个(id第一个)
			
			Cursor firstCursor = db.query("playlist", null, null, null, null, null,null);
			if(firstCursor.moveToFirst()){
				String PreSong_id= firstCursor.getString(firstCursor.getColumnIndex("song_id"));
				db.close();
				return PreSong_id;
			}else{//数据库没歌曲
				db.close();
				return null;
			}
			
		}else{//当前有歌曲在播放
			
			Cursor cursorId = db.query("playlist", new String[]{"id"}, "song_id=?", new String[]{song_id}, null, null, null);
			if(cursorId.moveToFirst()){
				int id = cursorId.getInt(cursorId.getColumnIndex("id"));
				Cursor cursor = db.query("playlist", null, "id>?", new String[]{id+""}, null, null, null);
				if(cursor.moveToFirst()){
					String PreSong_id= cursor.getString(cursor.getColumnIndex("song_id"));
					db.close();
					return PreSong_id;
				}else{
					//没有上一曲了
					db.close();
					return null;
				}
			}
			
			db.close();
			return null;
		}
	}
	
	/**
	 * 查询列表第一首歌曲
	 */
	public String queryFirstSong(){
		SQLiteDatabase db = mSQHelper.getWritableDatabase();
		Cursor firstCursor = db.query("playlist", null, null, null, null, null, "id desc");
		if(firstCursor.moveToFirst()){
			String firstSong_id= firstCursor.getString(firstCursor.getColumnIndex("song_id"));
			db.close();
			return firstSong_id;
		}else{//数据库没歌曲
			db.close();
			return null;
		}
	}
}
