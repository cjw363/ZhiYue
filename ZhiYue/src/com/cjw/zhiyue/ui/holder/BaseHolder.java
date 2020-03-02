package com.cjw.zhiyue.ui.holder;


import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @param <T>表示list集合里面单个item对象
 */
public abstract class BaseHolder<T> extends RecyclerView.ViewHolder{
	
	private T itemData;
	
	public BaseHolder(View itemView) {
		super(itemView);
	}
	
	/**
	 * 把数据传入，并刷新数据更新UI
	 */
	public void loadData(T itemData){
		this.itemData=itemData;
		refreshData(itemData);
	}
	public T getData(){
		return itemData;
	}
	
	public abstract void refreshData(T info);//交由子类由具体更新UI
		
}
