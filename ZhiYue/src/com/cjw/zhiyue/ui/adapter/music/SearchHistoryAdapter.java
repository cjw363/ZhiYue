package com.cjw.zhiyue.ui.adapter.music;

import java.util.List;

import com.cjw.zhiyue.R;
import com.cjw.zhiyue.db.Dao.HistoryListDao;
import com.cjw.zhiyue.ui.adapter.MyBaseAdapter;
import com.cjw.zhiyue.ui.holder.BaseHolder;
import com.cjw.zhiyue.ui.holder.music.SearchHistoryHolder;
import com.cjw.zhiyue.utils.UIUtils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class SearchHistoryAdapter extends MyBaseAdapter<String> {

	public SearchHistoryAdapter(List<String> data) {
		super(data);
	}

	@Override
	public boolean isHasMore() {
		return false;
	}

	@Override
	public void onLoadMore() {
	}

	@Override
	public View initHolderView(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_search_history, parent, false);
		return view;
	}
 
	@Override
	public BaseHolder<String> getHolder(View initHolderView, OnItemClickListener<String> itemClickListener,
			int viewType) {
		final SearchHistoryHolder searchHistoryHolder = new SearchHistoryHolder(initHolderView, itemClickListener);
		
		ImageButton ib_delete_song = (ImageButton) initHolderView.findViewById(R.id.ib_delete_song);
		ib_delete_song.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				HistoryListDao.getHistoryListDao().delete(data.get(searchHistoryHolder.getPosition()));//移除数据库的数据
				data.remove(searchHistoryHolder.getPosition());//移除data集合的数据
				SearchHistoryAdapter.this.notifyDataSetChanged();//更新历史内容
			}
		});
		
		return searchHistoryHolder;
	}

}
