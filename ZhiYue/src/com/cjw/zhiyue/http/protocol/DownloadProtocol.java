package com.cjw.zhiyue.http.protocol;

import com.cjw.zhiyue.domain.DownloadSongInfo;
import com.cjw.zhiyue.global.GlobalConstant;
import com.google.gson.Gson;

public class DownloadProtocol extends BaseProtocol<DownloadSongInfo> {

	@Override
	public String getKey() {
		return GlobalConstant.KEY_DOWNLOAD;
	}

	@Override
	public String getParams() {
		return "&songid=";
	}

	@Override
	public DownloadSongInfo parseData(String result) {
		// 对json数据进行解析
		Gson gson = new Gson();
		DownloadSongInfo downloadSongInfo = gson.fromJson(result, DownloadSongInfo.class);
		return downloadSongInfo;
	}

	@Override
	public void requestHttpFail() {
	}

}
