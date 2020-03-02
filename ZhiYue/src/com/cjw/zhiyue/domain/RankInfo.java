package com.cjw.zhiyue.domain;

import java.util.ArrayList;

public class RankInfo {

	public BillBoard billboard;

	public ArrayList<SongInfo> song_list;

	public class BillBoard {
		public String name;// 榜单名
		public String billboard_type;//榜单type
		public String pic_s210;// 带百度，长方形，榜单图片
		public String pic_s260;// 无百度，正方形
		public String pic_s444;// 无百度，长方形
		public String pic_s640;// 带百度，正方形

		public String update_date;// 发布时间
		public String web_url;// web地址
	}

	public class SongInfo {
		public String artist_name;// 歌手名
		public String artist_id;// 歌手id
		public String title;// 歌曲名
		public String song_id;// 歌曲id,很重要
		public String country;
		public int file_duration;// 文件时长
		public Long hot;// 热度
		public String language;// 歌曲语言
		public String lrclink;// 歌词，地址
		public String pic_big;// 歌曲封面,实际很小
		public String pic_small;// 歌曲封面
		public String publishtime;// 歌曲发布时间
		public String style;// 歌曲风格
		public String rank;// 此歌曲在榜单中排行
	}
}
