package com.cjw.zhiyue.domain;

import java.io.File;
import java.io.Serializable;

import com.cjw.zhiyue.http.manager.DownloadManager;

import android.os.Environment;

public class DownloadInfo implements Serializable{

	private static final String ZHIYUE = "com.cjw.zhiyue";
	private static final String DOWNLOAD = "Download";
	
	
	public String song_id;// 歌曲id
	public String song_name;// 歌曲名
	public String songer;// 歌手名
	public String all_rate;//所有码率
	
	public String song_file_id;// 歌曲文件id
	public String file_extension;// 文件格式
	public Long file_size;// 文件大小
	public String file_bitrate;// 比特率 24, 64, 128, 192, 256, 320 ,flac//码率
	public String show_link;// 试听时的文件播放地址，可以下载
	
	public String downloadUrl;// 下载链接
	public String downloadPath;// 文件下载路径
	
	public String downloadName;//文件名

	public long currentPos;// 当前下载位置progress
	public int currentState;// 当前下载状态

	/**
	 * copy一个DownloadInfo对象
	 */
	public static DownloadInfo copyDownloadInfo(DownloadSongInfo info) {
		DownloadInfo downloadInfo = new DownloadInfo();

		downloadInfo.song_id = info.songinfo.song_id;
		downloadInfo.song_name = info.songinfo.title;
		downloadInfo.songer = info.songinfo.author;
		downloadInfo.all_rate = info.songinfo.all_rate;
		
		downloadInfo.song_file_id = info.song_file_id;
		downloadInfo.downloadUrl = info.file_link;
		downloadInfo.file_extension = info.file_extension;
		downloadInfo.file_size = info.file_size;
		downloadInfo.file_bitrate = info.file_bitrate;
		downloadInfo.show_link = info.show_link;

		downloadInfo.currentPos = 0;
		downloadInfo.currentState = DownloadManager.STATE_UNDO;
		
		downloadInfo.downloadName=info.songinfo.title+" - "+info.songinfo.author;
		downloadInfo.downloadPath = downloadInfo.getFilePath();

		
		return downloadInfo;
	}

	public float getProgress(){//获取下载进度0-1
		if(file_size==0){
			return 0;
		}
		
		return currentPos/(float)file_size; 
	}
	
	/**
	 * 获取文件下载路径
	 */
	public String getFilePath(){
		StringBuffer sb = new StringBuffer();
		String path = Environment.getExternalStorageDirectory().getAbsolutePath();
		sb.append(path);
		sb.append(File.separator);
		sb.append(ZHIYUE);
		sb.append(File.separator);
		sb.append(DOWNLOAD);
		
		if(createDir(sb.toString())){
			return sb.toString()+File.separator+downloadName+"."+file_extension;
		}
		return null;
	}
	
	/**
	 * 创建下载的文件夹，并判断是否创建成功，没有会创建
	 */
	private boolean createDir(String dir){
		File dirFile = new File(dir);//文件夹
		if(!dirFile.exists()||!dirFile.isDirectory())
			return dirFile.mkdirs();
		
		return true;
	}
}
