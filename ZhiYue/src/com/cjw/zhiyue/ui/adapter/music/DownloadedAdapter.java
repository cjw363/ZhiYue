package com.cjw.zhiyue.ui.adapter.music;

import java.util.ArrayList;
import java.util.List;


import com.cjw.zhiyue.R;
import com.cjw.zhiyue.db.Dao.PlayListDao;
import com.cjw.zhiyue.domain.DownloadInfo;
import com.cjw.zhiyue.domain.PlaySongInfo;
import com.cjw.zhiyue.global.GlobalConstant;
import com.cjw.zhiyue.http.manager.DownloadManager;
import com.cjw.zhiyue.http.manager.DownloadManager.DownloadObserver;
import com.cjw.zhiyue.http.protocol.PlaySongProtocol;
import com.cjw.zhiyue.ui.adapter.MyBaseAdapter;
import com.cjw.zhiyue.ui.holder.BaseHolder;
import com.cjw.zhiyue.ui.holder.music.DownloadedHolder;
import com.cjw.zhiyue.utils.SPUtils;
import com.cjw.zhiyue.utils.UIUtils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;
import android.widget.TextView;

public class DownloadedAdapter extends MyBaseAdapter<DownloadInfo> implements DownloadObserver {

	protected static final int GET_SUCCESS = 0;
	protected static final int PLAY_LOCAL = 2;
	protected static final int UPDATE_INFO = 3;
	
	private ArrayList<DownloadInfo> downloadInfoList;
	private ArrayList<DownloadInfo> downloadedList;

	private AlertDialog mDialog;

	private DownloadManager mDM;
	
	public DownloadedAdapter(List<DownloadInfo> data) {
		super(data);
		
		mDM = DownloadManager.getInstance();
		mDM.registObserver(this);// 注册下载观察者
		
		this.setOnItemClickListener(new OnItemClickListener<DownloadInfo>() {
			//点击事件
			@Override
			public void onItemClick(View view, int position, DownloadInfo info) {
				//请求网络，拿到播放地址，播放本地音乐
				requestSongProtocol(info);
				//存储歌曲，显示在歌曲播放列表
				PlayListDao.getPlayListDao().insert(info.song_id, info.song_name, info.songer);
			}
		});
	}

	@Override
	public boolean isHasMore() {
		return false;
	}
	@Override
	public void onLoadMore() {}

	@Override
	public View initHolderView(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_downloaded, parent, false);
		return view;
	}

	@Override
	public BaseHolder<DownloadInfo> getHolder(View initHolderView, OnItemClickListener<DownloadInfo>  itemClickListener, int viewType) {
		final DownloadedHolder downloadedHolder = new DownloadedHolder(initHolderView,itemClickListener);
		
		ImageButton ib_delete_download = (ImageButton)initHolderView.findViewById(R.id.ib_delete_download);
		ib_delete_download.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showdeleteDialog(downloadedHolder.getData());// 弹出确定是否删除下载的dialog
			}
		});
		
		return downloadedHolder;
	}

	/**
	 * 弹出确定是否删除下载的dialog
	 */
	private void showdeleteDialog(final DownloadInfo downloadInfo) {
		AlertDialog.Builder builder = new Builder(UIUtils.getActivity());
		mDialog = builder.create();

		Window window = mDialog.getWindow();
		//window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);// 类型
		mDialog.show();
		
		View view = UIUtils.inflate(R.layout.dialog_download_delete);
		TextView tv_sure = (TextView) view.findViewById(R.id.tv_sure);
		TextView tv_cancle = (TextView) view.findViewById(R.id.tv_cancle);

		window.setContentView(view);
		LayoutParams params = window.getAttributes();
		// params.format = PixelFormat.TRANSLUCENT;
		params.width = UIUtils.getWidthPixels(0.9); // 窗口的宽
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(params);
		
		tv_sure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDM.delete(downloadInfo);
				
				initDownloadList();//重写获取下载完成列表
				mDialog.dismiss();
			}
		});
		tv_cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		
	}
	
	/**
	 * 请求网络，拿到播放地址
	 */
	protected void requestSongProtocol(DownloadInfo info) {
		
		final Intent intent = new Intent(GlobalConstant.REQUEST_PLAY_SONG_ACTION);
		
		PlaySongProtocol playSongProtocol = new PlaySongProtocol(){
			@Override
			public PlaySongInfo parseData(String result) {
				//重写解析方法，网络请求成功后，去开始服务播放音乐
				PlaySongInfo playSongInfo = super.parseData(result);
				
				//发送广播
				intent.putExtra("result", UPDATE_INFO);
				intent.putExtra("playSongInfo", playSongInfo);
				UIUtils.getContext().sendBroadcast(intent);
				
				return playSongInfo;
			}
			
			@Override
			public void requestHttpFail() {}
			
		};
		playSongProtocol.getDataFromCache("&songid="+info.song_id);
		
		//广播，播放本地
		intent.putExtra("result", PLAY_LOCAL);
		intent.putExtra("downloadInfo", info);
		UIUtils.getContext().sendBroadcast(intent);
	}
	
	/**
	 * 初始化分类下载列表
	 */
	private void initDownloadList() {
		downloadInfoList = SPUtils.getDownloadInfoList();
		downloadedList = new ArrayList<DownloadInfo>();

		downloadedList.clear();//清除之前的数据
		if (downloadInfoList != null)
			for (DownloadInfo d : downloadInfoList) {
				if (d.currentState == DownloadManager.STATE_SUCCESS) {
					// 下载完成
					downloadedList.add(d);
				}
			}
		data=downloadedList;//修改下载列表的变化
		this.notifyDataSetChanged();
	}

	/**
	 * 更新状态，和进度
	 */
	protected void refreshUI(int mCurrentState, DownloadInfo downloadInfo) {
		switch (mCurrentState) {
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
