package com.cjw.zhiyue.http.protocol;

import com.cjw.zhiyue.domain.RankInfo;
import com.cjw.zhiyue.global.GlobalConstant;
import com.google.gson.Gson;

public class RankProtocol extends BaseProtocol<RankInfo> {

	@Override
	public String getKey() {
		return GlobalConstant.KEY_BILLLIST;
	}

	@Override
	public String getParams() {
		return "&type=1&size=10";//新歌榜，歌曲数目10
	}

	@Override
	public RankInfo parseData(String result) {
		//对json数据进行解析
		Gson gson = new Gson();
		RankInfo bankInfo = gson.fromJson(result, RankInfo.class);
		return bankInfo;
	}

	@Override
	public void requestHttpFail() {
	}
}
