package com.cjw.zhiyue.domain;

import java.util.ArrayList;

public class SearchInfo {

	public int error_code;//22000成功，22001失败
	
	//public String error_message=null;//失败的信息，"failed"
	
	public ArrayList<SearchSongInfo> song=null;
	
	public static class SearchSongInfo{
		public String artistname;
		public String songname;
		public String songid;
	}
	
}
