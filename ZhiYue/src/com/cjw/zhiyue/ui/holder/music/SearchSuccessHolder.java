package com.cjw.zhiyue.ui.holder.music;

import java.util.ArrayList;

import org.xutils.x;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import com.cjw.zhiyue.R;
import com.cjw.zhiyue.domain.DownloadInfo;
import com.cjw.zhiyue.domain.DownloadSongInfo;
import com.cjw.zhiyue.domain.DownloadSongInfo.DownloadBitrate;
import com.cjw.zhiyue.domain.SearchInfo.SearchSongInfo;
import com.cjw.zhiyue.http.manager.DownloadManager;
import com.cjw.zhiyue.http.manager.DownloadManager.DownloadObserver;
import com.cjw.zhiyue.http.protocol.DownloadProtocol;
import com.cjw.zhiyue.ui.adapter.MyBaseAdapter.OnItemClickListener;
import com.cjw.zhiyue.ui.adapter.popupw.DownloadPopupwAdapter;
import com.cjw.zhiyue.ui.holder.BaseHolder;
import com.cjw.zhiyue.ui.view.DownloadPopupw;
import com.cjw.zhiyue.utils.UIUtils;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SearchSuccessHolder extends BaseHolder<SearchSongInfo> implements OnClickListener, DownloadObserver{

	@ViewInject(R.id.tv_search_name)
	private TextView tv_search_name;
	@ViewInject(R.id.tv_search_songer)
	private TextView tv_search_songer;
	
	private OnItemClickListener<SearchSongInfo> itemClickListener;
	
	private RecyclerView mRecyclerview;
	
	private SearchSongInfo searchSongInfo;
	protected DownloadSongInfo downloadSongInfo;
	private ArrayList<DownloadBitrate> bitrateList;
	private DownloadManager mDM;
	
	private int mCurrentState=DownloadManager.STATE_UNDO;
	
	private DownloadPopupw downloadPopupw;
	private DownloadPopupwAdapter downlPopupwAdapter;

	public SearchSuccessHolder(View itemView,OnItemClickListener<SearchSongInfo> itemClickListener, RecyclerView recyclerview) {
		super(itemView);
		x.view().inject(this, itemView);
		
		mDM = DownloadManager.getInstance();
		mDM.registObserver(this);// 注册下载观察者
		
		this.itemClickListener=itemClickListener;
		this.mRecyclerview=recyclerview;
		itemView.setOnClickListener(this);
	}

	@Override
	public void refreshData(SearchSongInfo info) {
		this.searchSongInfo=info;
		
		initDownload();//获取下载列表：各种比特率
		
		tv_search_name.setText(info.artistname);
		tv_search_songer.setText(info.songname);
	}

	@Override
	public void onClick(View v) {
		if(itemClickListener!=null)
			itemClickListener.onItemClick(v, getPosition(), searchSongInfo);
	}

	@Event(R.id.ib_search_download)
	private void onSearchDownlClick(View v){
		if (downloadSongInfo != null && downloadSongInfo.error_code == 22000) {
			if (downloadPopupw == null)
				downloadPopupw = new DownloadPopupw(FrameLayout.LayoutParams.MATCH_PARENT, UIUtils.dip2px(200));

			downlPopupwAdapter = new DownloadPopupwAdapter(bitrateList);
			downloadPopupw.setAdapter(downlPopupwAdapter);
			downloadPopupw.showAtLocation(mRecyclerview, Gravity.BOTTOM, 0, -UIUtils.dip2px(100));
			
			downlPopupwAdapter.setOnItemClickListener(new OnItemClickListener<DownloadSongInfo.DownloadBitrate>() {
				@Override
				public void onItemClick(View view, int position, DownloadBitrate info) {
					downloadSongInfo.file_bitrate = info.file_bitrate;
					downloadSongInfo.file_extension = info.file_extension;
					downloadSongInfo.file_link = info.file_link;
					downloadSongInfo.show_link = info.show_link;
					downloadSongInfo.song_file_id = info.song_file_id;
					downloadSongInfo.file_size = info.file_size;

					if (mCurrentState == DownloadManager.STATE_UNDO || mCurrentState == DownloadManager.STATE_PAUSE
							|| mCurrentState == DownloadManager.STATE_FAIL) {
						mDM.download(downloadSongInfo, null);// 开始下载
					} else if (mCurrentState == DownloadManager.STATE_WAITTING
							|| mCurrentState == DownloadManager.STATE_DOWNLOADING) {
						Toast.makeText(UIUtils.getContext(), "歌曲正在下载", Toast.LENGTH_SHORT).show();
					} else {
						mDM.download(downloadSongInfo, null);// 下载成功，也可以下载
					}

					downloadPopupw.dismiss();
				}
			});
			
		}else
			Toast.makeText(UIUtils.getContext(), "抱歉,暂无下载资源", Toast.LENGTH_SHORT).show();
		
	}
	
	/**
	 * 先获取下载列表：各种比特率
	 */
	private void initDownload() {
		if (searchSongInfo != null) {
			// 先获取下载列表：各种比特率
			DownloadProtocol downloadProtocol = new DownloadProtocol() {
				@Override
				public DownloadSongInfo parseData(String result) {
					downloadSongInfo = super.parseData(result);

					if (22000 == downloadSongInfo.error_code) 
						getBitrate();// 获取各种品质的歌曲

					return downloadSongInfo;
				}

			};
			downloadProtocol.getDataFromCache(searchSongInfo.songid);
		}
	}
	/**
	 * 获取各种品质的歌曲
	 */
	protected void getBitrate() {
		ArrayList<DownloadBitrate> bitrate = downloadSongInfo.bitrate;
		bitrateList = new ArrayList<DownloadBitrate>();

		for (DownloadBitrate b : bitrate) {
			if (b.file_bitrate.equals("320") && b.file_link != "")
				bitrateList.add(b);
			if (b.file_bitrate.equals("256") && b.file_link != "")
				bitrateList.add(b);
			if (b.file_bitrate.equals("192") && b.file_link != "")
				bitrateList.add(b);
			if (b.file_bitrate.equals("128") && b.file_link != "")
				bitrateList.add(b);
		}
	}
	
	/**
	 * 更新状态，和进度
	 */
	protected void refreshUI(int currentState) {
		mCurrentState=currentState;
	}
	
	/**
	 * 主线程更新状态，和进度
	 */
	public void runOnMainRefreshUI(final DownloadInfo downloadInfo) {
		UIUtils.runOnUIThread(new Runnable() {// 这里必须传入DownloadInfo对象，因为final修饰就不能被修改，但里面的变量可以，这样可以保证里面的变量是最新的
			@Override
			public void run() {
				refreshUI(downloadInfo.currentState);
			}
		});
	}

	@Override
	public void onDownloadStateChanged(DownloadInfo downloadInfo) {
		if ((searchSongInfo.songid).equals(downloadInfo.song_id)) {// 判断下载对象是否是当前对象
			runOnMainRefreshUI(downloadInfo);
		}
	}

	@Override
	public void onDownloadProgressChanged(DownloadInfo downloadInfo) {
		if ((searchSongInfo.songid).equals(downloadInfo.song_id)) {
			runOnMainRefreshUI(downloadInfo);
		}
	}
}
