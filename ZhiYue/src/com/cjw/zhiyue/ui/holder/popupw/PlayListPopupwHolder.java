package com.cjw.zhiyue.ui.holder.popupw;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import com.cjw.zhiyue.R;
import com.cjw.zhiyue.db.domain.SongInfo;
import com.cjw.zhiyue.ui.adapter.MyBaseAdapter.OnItemClickListener;
import com.cjw.zhiyue.ui.holder.BaseHolder;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class PlayListPopupwHolder extends BaseHolder<SongInfo> implements OnClickListener {

	@ViewInject(R.id.tv_pw_songname)
	private TextView tv_pw_songname;
	@ViewInject(R.id.tv_pw_songer)
	private TextView tv_pw_songer;

	private OnItemClickListener<SongInfo> itemClickListener;

	private SongInfo songInfo;

	public PlayListPopupwHolder(View itemView, OnItemClickListener<SongInfo> itemClickListener) {
		super(itemView);
		x.view().inject(this, itemView);

		this.itemClickListener = itemClickListener;
		itemView.setOnClickListener(this);// 设置点击事件
	}

	@Override
	public void refreshData(SongInfo songInfo) {
		this.songInfo = songInfo;
		tv_pw_songname.setText(songInfo.song_name);
		tv_pw_songer.setText(songInfo.songer);
	}

	@Override
	public void onClick(View v) {
		if (itemClickListener != null)
			itemClickListener.onItemClick(v, getPosition(), songInfo);
	}

}