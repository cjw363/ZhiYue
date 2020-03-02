package com.cjw.zhiyue.ui.adapter.music;

import java.util.List;

import com.cjw.zhiyue.R;
import com.cjw.zhiyue.domain.RankInfo;
import com.cjw.zhiyue.ui.adapter.MyBaseAdapter;
import com.cjw.zhiyue.ui.holder.BaseHolder;
import com.cjw.zhiyue.ui.holder.music.RankHolder;
import com.cjw.zhiyue.utils.UIUtils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RankAdapter extends MyBaseAdapter<RankInfo> {

	public RankAdapter(List<RankInfo> data) {
		super(data);
	}

	@Override
	public boolean isHasMore() {
		return false;// 没有更多数据
	}

	@Override
	public void onLoadMore() {
		//SystemClock.sleep(2000);// 延迟两秒
	}

	@Override
	public BaseHolder<RankInfo> getHolder(View initHolderView,OnItemClickListener<RankInfo> itemClickListener,int viewType) {
		return new RankHolder(initHolderView,itemClickListener);
	}

	@Override
	public View initHolderView(ViewGroup parent,int viewType) {
		View itemView = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_rank, parent, false);
		return itemView;
	}
}


