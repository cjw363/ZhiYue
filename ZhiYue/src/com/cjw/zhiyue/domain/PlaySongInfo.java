package com.cjw.zhiyue.domain;

import java.io.Serializable;

public class PlaySongInfo implements Serializable{
	
	public SongFile bitrate;
	
	public SongInfo songinfo;
	
	public class SongInfo implements Serializable{
		public String author;// 歌手名
		public String artist_id;// 歌手id
		public String song_id;// 歌id
		public String title;// 歌曲名
		public String lrclink;// 歌词，地址
		public String pic_premium;// 歌曲封面,很大
		public String pic_radio;// 歌曲封面
		public String pic_big;// 歌曲封面,实际很小
		public String pic_small;// 歌曲封面
	}
	
	public class SongFile implements Serializable{
		public int file_duration;// 文件时长
		public Long file_size;// 文件大小
		public String song_file_id;//文件id
		public String file_link;//播放地址
	}

}
