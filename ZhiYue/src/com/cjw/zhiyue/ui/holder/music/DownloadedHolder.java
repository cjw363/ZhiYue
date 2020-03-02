package com.cjw.zhiyue.ui.holder.music;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import com.cjw.zhiyue.R;
import com.cjw.zhiyue.domain.DownloadInfo;
import com.cjw.zhiyue.ui.adapter.MyBaseAdapter.OnItemClickListener;
import com.cjw.zhiyue.ui.holder.BaseHolder;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class DownloadedHolder extends BaseHolder<DownloadInfo> implements OnClickListener{

	@ViewInject(R.id.tv_download_songname)
	private TextView tv_download_songname;
	@ViewInject(R.id.tv_download_songer)
	private TextView tv_download_songer;
	@ViewInject(R.id.ib_delete_download)
	private ImageButton ib_delete_download;
	
	private OnItemClickListener<DownloadInfo> itemClickListener;

	public DownloadedHolder(View itemView,OnItemClickListener<DownloadInfo>  itemClickListener) {
		super(itemView);
		x.view().inject(this, itemView);
		
		this.itemClickListener = itemClickListener;
		itemView.setOnClickListener(this);
	}

	@Override
	public void refreshData(DownloadInfo info) {
		tv_download_songname.setText(info.song_name);
		tv_download_songer.setText(info.songer);
	}

	@Override
	public void onClick(View v) {
		if (itemClickListener != null)
			itemClickListener.onItemClick(v, getPosition(), getData());
	}

}
