package com.cjw.zhiyue.http.protocol;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cjw.zhiyue.domain.SearchInfo;
import com.cjw.zhiyue.domain.SearchInfo.SearchSongInfo;
import com.cjw.zhiyue.global.GlobalConstant;

public class SearchProtocol extends BaseProtocol<SearchInfo> {

	@Override
	public String getKey() {
		return GlobalConstant.KEY_SEARCH;
	}

	@Override
	public String getParams() {
		return "&query=";
	}

	@Override
	public SearchInfo parseData(String result) {
		try {

			JSONObject jo = new JSONObject(result);
			SearchInfo searchInfo = new SearchInfo();
			searchInfo.error_code = jo.getInt("error_code");
			
			if (searchInfo.error_code == 22001) {
				return searchInfo;//当返回码为22001表示失败，可能是关键词有误或者服务器未响应
			}

			JSONArray ja = jo.getJSONArray("song");
			ArrayList<SearchSongInfo> searchList = new ArrayList<SearchSongInfo>();
			
			for (int i=0;i<ja.length();i++) {
				SearchSongInfo info = new SearchSongInfo();
				
				JSONObject jsonObject = ja.getJSONObject(i);
				info.artistname=jsonObject.getString("artistname");
				info.songname=jsonObject.getString("songname");
				info.songid=jsonObject.getString("songid");
				
				searchList.add(info);
			}
			searchInfo.song=searchList;
			
			return searchInfo;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// 对json数据进行解析
		// Gson gson = new Gson();
		// SearchInfo searchInfo = gson.fromJson(result, SearchInfo.class);

		return null;
	}

	@Override
	public void requestHttpFail() {
	}

}
