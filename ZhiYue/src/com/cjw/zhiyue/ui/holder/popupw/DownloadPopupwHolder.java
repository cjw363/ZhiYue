package com.cjw.zhiyue.ui.holder.popupw;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import com.cjw.zhiyue.R;
import com.cjw.zhiyue.domain.DownloadSongInfo.DownloadBitrate;
import com.cjw.zhiyue.ui.adapter.MyBaseAdapter.OnItemClickListener;
import com.cjw.zhiyue.ui.holder.BaseHolder;
import com.cjw.zhiyue.utils.UIUtils;

import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class DownloadPopupwHolder extends BaseHolder<DownloadBitrate> implements OnClickListener {

	@ViewInject(R.id.tv_bitrate)
	private TextView tv_bitrate;
	@ViewInject(R.id.tv_file_size)
	private TextView tv_file_size;
	
	private OnItemClickListener<DownloadBitrate> itemClickListener;

	public DownloadPopupwHolder(View itemView, OnItemClickListener<DownloadBitrate> itemClickListener) {
		super(itemView);
		x.view().inject(this, itemView);
		this.itemClickListener = itemClickListener;
		itemView.setOnClickListener(this);
	}

	@Override
	public void refreshData(DownloadBitrate info) {

		if (info.file_bitrate.equals("320"))
			tv_bitrate.setText("超高品质");
		if (info.file_bitrate.equals("256"))
			tv_bitrate.setText("高品质");
		if (info.file_bitrate.equals("192"))
			tv_bitrate.setText("标准品质");
		if (info.file_bitrate.equals("128"))
			tv_bitrate.setText("普通品质");
		
		if(info.file_size!=null) {
			String fileSize = Formatter.formatFileSize(UIUtils.getContext(), info.file_size);
			tv_file_size.setText("("+fileSize+")");
		}
	}

	@Override
	public void onClick(View v) {
		if (itemClickListener != null)
			itemClickListener.onItemClick(v, getPosition(), getData());
	}

}
