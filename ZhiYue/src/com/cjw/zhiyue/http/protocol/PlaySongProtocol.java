package com.cjw.zhiyue.http.protocol;

import com.cjw.zhiyue.domain.PlaySongInfo;
import com.cjw.zhiyue.global.GlobalConstant;
import com.google.gson.Gson;

public class PlaySongProtocol extends BaseProtocol<PlaySongInfo> {

	@Override
	public String getKey() {

		return GlobalConstant.KEY_PLAY;
	}

	@Override
	public String getParams() {
		return "";
	}

	@Override
	public PlaySongInfo parseData(String result) {
		// 对json数据进行解析
		Gson gson = new Gson();
		PlaySongInfo playSongInfo = gson.fromJson(result, PlaySongInfo.class);
		return playSongInfo;
	}

	@Override
	public void requestHttpFail() {
	}

}
