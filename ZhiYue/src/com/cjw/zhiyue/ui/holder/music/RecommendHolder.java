package com.cjw.zhiyue.ui.holder.music;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import com.cjw.zhiyue.R;
import com.cjw.zhiyue.domain.PlaySongInfo;
import com.cjw.zhiyue.domain.RankInfo.SongInfo;
import com.cjw.zhiyue.http.VolleyHelper;
import com.cjw.zhiyue.http.protocol.PlaySongProtocol;
import com.cjw.zhiyue.ui.adapter.MyBaseAdapter.OnItemClickListener;
import com.cjw.zhiyue.ui.holder.BaseHolder;
import com.cjw.zhiyue.utils.UIUtils;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RecommendHolder extends BaseHolder<SongInfo> implements OnClickListener{

	@ViewInject(R.id.iv_recom)
	private ImageView iv_recom;
	@ViewInject(R.id.tv_recom_songname)
	private TextView tv_recom_songname;
	@ViewInject(R.id.tv_recom_songer)
	private TextView tv_recom_songer;
	
	private OnItemClickListener<SongInfo> itemClickListener;
	private SongInfo info;
	
	public RecommendHolder(View itemView,OnItemClickListener<SongInfo> itemClickListener) {
		super(itemView);
		x.view().inject(this, itemView);
		
		this.itemClickListener=itemClickListener;
		itemView.setOnClickListener(this);
	}

	@Override
	public void refreshData(SongInfo info) {
		this.info=info;
		requestSongProtocol(info);
		
		tv_recom_songname.setText(info.title);
		tv_recom_songer.setText("by "+info.artist_name);
	}

	/**
	 * 请求网络，拿到播放地址
	 */
	protected void requestSongProtocol(SongInfo songInfo) {
		
		PlaySongProtocol playSongProtocol = new PlaySongProtocol(){
			@Override
			public PlaySongInfo parseData(String result) {
				PlaySongInfo playSongInfo = super.parseData(result);
				
				VolleyHelper.imageLoader(playSongInfo.songinfo.pic_premium, iv_recom);
				return playSongInfo;
				
			}
			@Override
			public void requestHttpFail() {
				//请求播放地址失败
				Toast.makeText(UIUtils.getContext(), "请检查网络连接", Toast.LENGTH_SHORT).show();
			}
			
		};
		playSongProtocol.getDataFromCache("&songid="+songInfo.song_id);
	}
	
	@Override
	public void onClick(View v) {
		if(itemClickListener!=null){
			itemClickListener.onItemClick(v, getPosition(), info);
		}
	}

}
