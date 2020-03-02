package com.cjw.zhiyue.ui.adapter.popupw;

import java.util.List;

import com.cjw.zhiyue.R;
import com.cjw.zhiyue.db.Dao.PlayListDao;
import com.cjw.zhiyue.db.domain.SongInfo;
import com.cjw.zhiyue.ui.adapter.MyBaseAdapter;
import com.cjw.zhiyue.ui.holder.BaseHolder;
import com.cjw.zhiyue.ui.holder.popupw.PlayListPopupwHolder;
import com.cjw.zhiyue.utils.UIUtils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class PlayListPopupwAdapter extends MyBaseAdapter<SongInfo> {

	public PlayListPopupwAdapter(List<SongInfo> data) {
		super(data);
	}

	@Override
	public boolean isHasMore() {
		return false;// 没有更多数据
	}

	@Override
	public void onLoadMore() {
	}

	@Override
	public View initHolderView(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_popupw_play, parent, false);
		return view;
	}

	@Override
	public BaseHolder<SongInfo> getHolder(View initHolderView, OnItemClickListener<SongInfo> itemClickListener,
			int viewType) {
		final PlayListPopupwHolder listPopupwHolder = new PlayListPopupwHolder(initHolderView, itemClickListener);
		ImageButton ib_delete_song = (ImageButton) initHolderView.findViewById(R.id.ib_delete_song);
		ib_delete_song.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PlayListDao.getPlayListDao().delete(data.get(listPopupwHolder.getPosition()).song_id);// 移除数据库的数据
				data.remove(listPopupwHolder.getPosition());// 移除data集合的数据

				PlayListPopupwAdapter.this.notifyDataSetChanged();//更新播放列表内容
			}
		});

		return listPopupwHolder;
	}
}
