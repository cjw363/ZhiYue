package com.cjw.zhiyue.ui.holder.music;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import com.cjw.zhiyue.R;
import com.cjw.zhiyue.ui.adapter.MyBaseAdapter.OnItemClickListener;
import com.cjw.zhiyue.ui.holder.BaseHolder;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SearchHistoryHolder extends BaseHolder<String> implements OnClickListener{

	@ViewInject(R.id.tv_search_key)
	private TextView tv_search_key;
	
	private OnItemClickListener<String> itemClickListener;

	private String info;

	public SearchHistoryHolder(View itemView,OnItemClickListener<String> itemClickListener) {
		super(itemView);
		x.view().inject(this, itemView);
		
		this.itemClickListener=itemClickListener;
		itemView.setOnClickListener(this);
	}

	@Override
	public void refreshData(String info) {
		this.info=info;
		tv_search_key.setText(info);
	}

	@Override
	public void onClick(View v) {
		if(itemClickListener!=null)
			itemClickListener.onItemClick(v, getPosition(), info);
	}

}
