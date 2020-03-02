package com.cjw.zhiyue.ui.view;

import com.cjw.zhiyue.R;
import com.cjw.zhiyue.ui.adapter.MyBaseAdapter;
import com.cjw.zhiyue.utils.UIUtils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.PopupWindow;

public class DownloadPopupw extends PopupWindow {

	private View mRootView;
	
	private RecyclerView mRecyclerview;
	
	public DownloadPopupw() {
		super();
	}

	public DownloadPopupw(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public DownloadPopupw(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public DownloadPopupw(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DownloadPopupw(Context context) {
		super(context);
	}

	public DownloadPopupw(View contentView, int width, int height, boolean focusable) {
		super(contentView, width, height, focusable);
	}

	public DownloadPopupw(View contentView, int width, int height) {
		super(contentView, width, height);
	}

	public DownloadPopupw(View contentView) {
		super(contentView);
	}

	/**
	 * 通过此构造方法生成列表的popupwindow
	 */
	public DownloadPopupw(int width, int height) {
		super(width, height);
		mRootView = initRootView();
		setContentView(mRootView);

		setPopupWindow();// 设置各自参数
	}

	/**
	 * 生成根布局
	 */
	private View initRootView() {
		View view = UIUtils.inflate(R.layout.layout_popupw_download_list);
		mRecyclerview = (RecyclerView) view.findViewById(R.id.recyclerview);

		return view;
	}

	/**
	 * 设置各自参数
	 */
	private void setPopupWindow() {
		setBackgroundDrawable(new ColorDrawable());// 设置透明背景,背景如果不设置的话，回退按钮不起作用
		setOutsideTouchable(true);// 外部可点击
		setFocusable(true);
		setAnimationStyle(R.style.animat_dialog);// 设置进程动画

		initRecyclerView();
	}

	/**
	 * 初始化RecyclerView
	 */
	private void initRecyclerView() {
		LinearLayoutManager layoutManager = new LinearLayoutManager(UIUtils.getContext());
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		mRecyclerview.setLayoutManager(layoutManager);
		mRecyclerview.setItemAnimator(new DefaultItemAnimator()); // 设置增加或删除条目的动画
		// recyclerview.addItemDecoration(new
		// SpaceItemDecoration(UIUtils.dip2px(10)));//设置间距
	}
	
	/**
	 * 设置adapter
	 */
	public void setAdapter(MyBaseAdapter adapter){
		mRecyclerview.setAdapter(adapter);
	}
}
