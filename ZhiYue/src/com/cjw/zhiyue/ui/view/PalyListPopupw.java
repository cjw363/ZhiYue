package com.cjw.zhiyue.ui.view;

import java.util.List;

import com.cjw.zhiyue.R;
import com.cjw.zhiyue.db.Dao.PlayListDao;
import com.cjw.zhiyue.db.domain.SongInfo;
import com.cjw.zhiyue.domain.PlaySongInfo;
import com.cjw.zhiyue.global.GlobalConstant;
import com.cjw.zhiyue.http.protocol.PlaySongProtocol;
import com.cjw.zhiyue.ui.adapter.MyBaseAdapter.OnItemClickListener;
import com.cjw.zhiyue.ui.adapter.popupw.PlayListPopupwAdapter;
import com.cjw.zhiyue.utils.UIUtils;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Toast;

public class PalyListPopupw extends PopupWindow {

	protected static final int GET_SUCCESS = 0;

	private View mRootView;
	private RecyclerView mRecyclerview;
	private PlayListPopupwAdapter mListAdapter = null;

	public PalyListPopupw() {
	}

	public PalyListPopupw(Context context) {
		super(context);
	}

	public PalyListPopupw(View contentView) {
		super(contentView);
	}

	public PalyListPopupw(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PalyListPopupw(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public PalyListPopupw(View contentView, int width, int height) {
		super(contentView, width, height);
	}

	public PalyListPopupw(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public PalyListPopupw(View contentView, int width, int height, boolean focusable) {
		super(contentView, width, height, focusable);
	}

	/**
	 * 通过此构造方法生成列表的popupwindow
	 */
	public PalyListPopupw(int width, int height) {
		super(width, height);
		mRootView = initRootView();
		setContentView(mRootView);

		setPopupWindow();// 设置各自参数
	}

	/**
	 * 生成根布局
	 */
	private View initRootView() {
		View view = UIUtils.inflate(R.layout.layout_popupw_play_list);
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
		final LinearLayoutManager layoutManager = new LinearLayoutManager(UIUtils.getContext());
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		mRecyclerview.setLayoutManager(layoutManager);
		mRecyclerview.setItemAnimator(new DefaultItemAnimator()); // 设置增加或删除条目的动画
		// recyclerview.addItemDecoration(new
		// SpaceItemDecoration(UIUtils.dip2px(10)));//设置间距

		List<SongInfo> songPlayList = PlayListDao.getPlayListDao().query();
		mListAdapter = new PlayListPopupwAdapter(songPlayList);
		mRecyclerview.setAdapter(mListAdapter);

		mListAdapter.setOnItemClickListener(new OnItemClickListener<SongInfo>() {
			@Override
			public void onItemClick(View view, int position, SongInfo info) {
				// 请求网络播放音乐
				requestSongProtocol(info);

				PalyListPopupw.this.dismiss();
			}
		});
	}

	/**
	 * 重新获取列表数据
	 */
	public void setAdaperData() {
		List<SongInfo> songPlayList = PlayListDao.getPlayListDao().query();
		mListAdapter.data = songPlayList;
		mListAdapter.notifyDataSetChanged();//更新播放列表内容

	}

	/**
	 * 请求网络，拿到播放地址
	 */
	protected void requestSongProtocol(SongInfo songInfo) {

		final Intent intent = new Intent(GlobalConstant.REQUEST_PLAY_SONG_ACTION);

		PlaySongProtocol playSongProtocol = new PlaySongProtocol() {
			@Override
			public PlaySongInfo parseData(String result) {
				// 重写解析方法，网络请求成功后，去开始服务播放音乐
				PlaySongInfo playSongInfo = super.parseData(result);

				// 发送广播
				intent.putExtra("result", GET_SUCCESS);
				intent.putExtra("playSongInfo", playSongInfo);
				UIUtils.getContext().sendBroadcast(intent);

				return playSongInfo;
			}

			@Override
			public void requestHttpFail() {
				// 请求播放地址失败
				Toast.makeText(UIUtils.getContext(), "请检查网络连接", Toast.LENGTH_SHORT).show();
			}

		};
		playSongProtocol.getDataFromCache("&songid=" + songInfo.song_id);
	}

}
