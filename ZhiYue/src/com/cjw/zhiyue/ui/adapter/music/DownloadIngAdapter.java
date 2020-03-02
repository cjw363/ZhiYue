package com.cjw.zhiyue.ui.adapter.music;

import java.util.ArrayList;
import java.util.List;

import com.cjw.zhiyue.R;
import com.cjw.zhiyue.domain.DownloadInfo;
import com.cjw.zhiyue.http.manager.DownloadManager;
import com.cjw.zhiyue.http.manager.DownloadManager.DownloadObserver;
import com.cjw.zhiyue.ui.adapter.MyBaseAdapter;
import com.cjw.zhiyue.ui.holder.BaseHolder;
import com.cjw.zhiyue.ui.holder.music.DownloadIngHolder;
import com.cjw.zhiyue.utils.SPUtils;
import com.cjw.zhiyue.utils.UIUtils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DownloadIngAdapter extends MyBaseAdapter<DownloadInfo> implements DownloadObserver {

	private ArrayList<DownloadInfo> downloadingList;
	private ArrayList<DownloadInfo> downloadInfoList;

	public DownloadIngAdapter(List<DownloadInfo> data) {
		super(data);

		final DownloadManager mDM = DownloadManager.getInstance();
		mDM.registObserver(this);// 注册下载观察者
		
		this.setOnItemClickListener(new OnItemClickListener<DownloadInfo>() {
			//点击事件
			@Override
			public void onItemClick(View view, int position, DownloadInfo info) {
				if (info.currentState == DownloadManager.STATE_UNDO || info.currentState == DownloadManager.STATE_PAUSE
						|| info.currentState == DownloadManager.STATE_FAIL) {
					mDM.download(null,info);// 开始下载
				} else if (info.currentState == DownloadManager.STATE_WAITTING
						|| info.currentState == DownloadManager.STATE_DOWNLOADING) {
					mDM.pause(null,info);
				}
			}
		});
		
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
		View view = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_downloading, parent, false);
		return view;
	}

	@Override
	public BaseHolder<DownloadInfo> getHolder(View initHolderView, OnItemClickListener<DownloadInfo> itemClickListener,
			int viewType) {
		return new DownloadIngHolder(initHolderView, itemClickListener);
	}

	/**
	 * 初始化分类下载列表
	 */
	private void initDownloadList() {
		downloadInfoList = SPUtils.getDownloadInfoList();
		downloadingList = new ArrayList<DownloadInfo>();

		downloadingList.clear();//清除之前的数据
		if (downloadInfoList != null)
			for (DownloadInfo d : downloadInfoList) {
				if (d.currentState == DownloadManager.STATE_DOWNLOADING || d.currentState == DownloadManager.STATE_FAIL
						|| d.currentState == DownloadManager.STATE_PAUSE
						|| d.currentState == DownloadManager.STATE_WAITTING) {
					// 未下载完成
					downloadingList.add(d);
				}
			}
		data=downloadingList;//修改下载列表的变化
		this.notifyDataSetChanged();//刷新列表
	}

	/**
	 * 更新状态，和进度
	 */
	protected void refreshUI(int currentState, DownloadInfo downloadInfo) {
		switch (currentState) {
		case DownloadManager.STATE_UNDO:
			break;
		case DownloadManager.STATE_WAITTING:
			break;
		case DownloadManager.STATE_DOWNLOADING:
			break;
		case DownloadManager.STATE_PAUSE:
			break;
		case DownloadManager.STATE_FAIL:
			break;
		case DownloadManager.STATE_SUCCESS:
			initDownloadList();//重新获取下载列表
			break;
		case DownloadManager.STATE_CANCEL:
			initDownloadList();//重新获取下载列表
			break;
		}
	}

	/**
	 * 主线程更新状态，和进度
	 */
	public void runOnMainRefreshUI(final DownloadInfo downloadInfo) {
		UIUtils.runOnUIThread(new Runnable() {// 这里必须传入DownloadInfo对象，因为final修饰就不能被修改，但里面的变量可以，这样可以保证里面的变量是最新的
			@Override
			public void run() {
				refreshUI(downloadInfo.currentState, downloadInfo);
			}
		});
	}

	@Override
	public void onDownloadStateChanged(DownloadInfo downloadInfo) {
		runOnMainRefreshUI(downloadInfo);
	}

	@Override
	public void onDownloadProgressChanged(DownloadInfo downloadInfo) {
		runOnMainRefreshUI(downloadInfo);
	}
}
