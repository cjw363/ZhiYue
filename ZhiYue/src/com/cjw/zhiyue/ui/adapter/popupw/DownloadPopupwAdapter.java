package com.cjw.zhiyue.ui.adapter.popupw;

import java.util.List;

import com.cjw.zhiyue.R;
import com.cjw.zhiyue.domain.DownloadSongInfo.DownloadBitrate;
import com.cjw.zhiyue.ui.adapter.MyBaseAdapter;
import com.cjw.zhiyue.ui.holder.BaseHolder;
import com.cjw.zhiyue.ui.holder.popupw.DownloadPopupwHolder;
import com.cjw.zhiyue.utils.UIUtils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DownloadPopupwAdapter extends MyBaseAdapter<DownloadBitrate> {

	public DownloadPopupwAdapter(List<DownloadBitrate> data) {
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
		View view = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_popupw_download, parent, false);
		return view;
	}

	@Override
	public BaseHolder<DownloadBitrate> getHolder(View initHolderView, OnItemClickListener<DownloadBitrate> itemClickListener, int viewType) {
		return new DownloadPopupwHolder(initHolderView, itemClickListener);
	}


}
