package com.cjw.zhiyue.utils;

import java.util.ArrayList;

import com.cjw.zhiyue.domain.DownloadInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SPUtils {

	private static ArrayList<String> list = new ArrayList<String>();

	/**
	 * 通过sp存储DownloadInfo的progress, currentsate
	 */
	public static void saveDownloadInfo(String song_id, DownloadInfo downloadInfo) {
		Editor sp = UIUtils.getContext().getSharedPreferences("sp_download", Context.MODE_PRIVATE).edit();

		String serializeStr = SerializeUtils.serialize(downloadInfo);
		sp.putString(song_id, serializeStr);// 保存序列化后的对象

		saveSongId(song_id);//存储所有下载过的songid
		
		sp.commit();
	}

	/**
	 * @return 获取DownloadInfo的progress, currentsate
	 */
	public static DownloadInfo readDownloadInfo(String song_id) {
		SharedPreferences sp = UIUtils.getContext().getSharedPreferences("sp_download", Context.MODE_PRIVATE);

		String serializeStr = sp.getString(song_id, null);
		if (serializeStr != null) {
			return (DownloadInfo) SerializeUtils.deSerialize(serializeStr);
		}

		return null;

	}

	/**
	 *存储所有下载过的songid
	 */
	private static void saveSongId(String song_id) {
		Editor editorSp = UIUtils.getContext().getSharedPreferences("sp_download", Context.MODE_PRIVATE).edit();
		SharedPreferences sp = UIUtils.getContext().getSharedPreferences("sp_download", Context.MODE_PRIVATE);

		String serializeStr = sp.getString("song_id", null);
		if (serializeStr != null) {
			list = (ArrayList<String>) SerializeUtils.deSerialize(serializeStr);
		}
		if(!list.contains(song_id))
		list.add(song_id);

		editorSp.putString("song_id", SerializeUtils.serialize(list));// 保存序列化后的对象
		editorSp.commit();
	}

	/**
	 * @return 获取所有下载过的DownloadInfo对象集合
	 */
	public static ArrayList<DownloadInfo> getDownloadInfoList() {
		SharedPreferences sp = UIUtils.getContext().getSharedPreferences("sp_download", Context.MODE_PRIVATE);

		String serializeStr = sp.getString("song_id", null);
		if (serializeStr != null) {
			ArrayList<String> songidList = (ArrayList<String>) SerializeUtils.deSerialize(serializeStr);
			
			ArrayList<DownloadInfo> downloadInfoList = new ArrayList<DownloadInfo>();
			for(String id: songidList){
				String str = sp.getString(id, null);
				if (serializeStr != null) {
					downloadInfoList.add((DownloadInfo) SerializeUtils.deSerialize(str));
				}
			}
			return downloadInfoList;
		}
		return null;
	}
	
}
