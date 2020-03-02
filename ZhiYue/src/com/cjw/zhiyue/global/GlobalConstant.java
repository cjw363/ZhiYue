package com.cjw.zhiyue.global;

public class GlobalConstant {

	public static final String MUSIC_BASE_URL="http://tingapi.ting.baidu.com/v1/restserver/ting?format=json&calback=&from=webapp_music&";
	
	public static final String KEY_BILLLIST="method=baidu.ting.billboard.billList";
	
	public static final String KEY_PLAY="method=baidu.ting.song.play";
	
	public static final String KEY_SEARCH="method=baidu.ting.search.catalogSug";
	
	public static final String KEY_DOWNLOAD="method=baidu.ting.song.downWeb";
	
	public static final String REQUEST_PLAY_SONG_ACTION="com.receiver.action.from.holder";

	public static final String SERVICE_ACTION_ACTIVITY_STATE="com.receiver.action.form.service.to.activity";

	public static final String SERVICE_ACTION_FRAG_PROGRESS="progress.form.service.to.fragment";
	
	public static final String SERVICE_ACTION_FRAG_STATE="state.form.service.to.fragment";
	
	public static final String SERVICE_ACTION_FRAG_PLAYSONGINFO="playsonginfo.form.service.to.fragment";
	
	
}
