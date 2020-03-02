package com.cjw.zhiyue.domain;

import java.util.ArrayList;

public class DownloadSongInfo {

	public ArrayList<DownloadBitrate> bitrate;

	public int error_code;
	
	public SongInfo songinfo;
	
	/* 选中要下载的比特率歌曲 */
	public String song_file_id;// 歌曲文件id
	public String file_link;// 文件下载链接
	public String file_extension;// 文件格式
	public Long file_size;// 文件大小
	public String file_bitrate;// 比特率 24, 64, 128, 192, 256, 320 ,flac//码率
	public String show_link;//试听时的文件播放地址，可以下载
	
	public class DownloadBitrate {
		public String song_file_id;// 歌曲文件id
		public String file_link;// 文件下载链接
		public String file_extension;// 文件格式
		public Long file_size;// 文件大小
		public String file_bitrate;// 比特率 24, 64, 128, 192, 256, 320 ,flac//码率
		public String show_link;//试听时的文件播放地址，可以下载
	}
	
	public class SongInfo{
		public String author;// 歌手名
		public String artist_id;// 歌手id
		public String song_id;// 歌id
		public String title;// 歌曲名
		public String all_rate;//所有码率
	}
}
