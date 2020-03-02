package com.cjw.zhiyue.ui.adapter.music;

import java.util.List;

import com.cjw.zhiyue.R;
import com.cjw.zhiyue.domain.SearchInfo.SearchSongInfo;
import com.cjw.zhiyue.ui.adapter.MyBaseAdapter;
import com.cjw.zhiyue.ui.holder.BaseHolder;
import com.cjw.zhiyue.ui.holder.music.SearchSuccessHolder;
import com.cjw.zhiyue.utils.UIUtils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SearchSuccessAdapter extends MyBaseAdapter<SearchSongInfo> {

	private RecyclerView recyclerview;

	public SearchSuccessAdapter(List<SearchSongInfo> data, RecyclerView recyclerview) {
		super(data);
		this.recyclerview=recyclerview;
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
		View itemView = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_search_success, parent, false);
		return itemView;
	}

	@Override
	public BaseHolder<SearchSongInfo> getHolder(View initHolderView,
			OnItemClickListener<SearchSongInfo> itemClickListener, int viewType) {
		
		return new SearchSuccessHolder(initHolderView,itemClickListener,recyclerview);
	}

}
