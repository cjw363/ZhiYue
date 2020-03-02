package com.cjw.zhiyue.ui.holder.music;

import java.util.ArrayList;

import org.xutils.x;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import com.cjw.zhiyue.R;
import com.cjw.zhiyue.domain.DownloadInfo;
import com.cjw.zhiyue.domain.DownloadSongInfo;
import com.cjw.zhiyue.domain.DownloadSongInfo.DownloadBitrate;
import com.cjw.zhiyue.domain.RankInfo.SongInfo;
import com.cjw.zhiyue.http.VolleyHelper;
import com.cjw.zhiyue.http.manager.DownloadManager;
import com.cjw.zhiyue.http.manager.DownloadManager.DownloadObserver;
import com.cjw.zhiyue.http.protocol.DownloadProtocol;
import com.cjw.zhiyue.ui.adapter.MyBaseAdapter.OnItemClickListener;
import com.cjw.zhiyue.ui.adapter.popupw.DownloadPopupwAdapter;
import com.cjw.zhiyue.ui.holder.BaseHolder;
import com.cjw.zhiyue.ui.view.DownloadPopupw;
import com.cjw.zhiyue.utils.SPUtils;
import com.cjw.zhiyue.utils.UIUtils;

import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RankDetailHolder extends BaseHolder<SongInfo> implements OnClickListener,DownloadObserver{

	@ViewInject(R.id.iv_detail_img)
	private ImageView iv_detail_img;
	@ViewInject(R.id.tv_detail_name)
	private TextView tv_detail_name;
	@ViewInject(R.id.tv_detail_songer)
	private TextView tv_detail_songer;
	@ViewInject(R.id.ib_detail_download)
	private ImageButton ib_detail_download;
	
	private OnItemClickListener<SongInfo> itemClickListener;
	private DrawerLayout mDrawerLayout;
	private SongInfo songInfo;
	
	private DownloadManager mDM;
	
	protected DownloadSongInfo downloadSongInfo;
	
	private ArrayList<DownloadBitrate> bitrateList;
	
	private DownloadPopupw downloadPopupw = null;
	private DownloadPopupwAdapter downlPopupwAdapter;
	
	public RankDetailHolder(View itemView,OnItemClickListener<SongInfo> itemClickListener,DrawerLayout mDrawerLayout) {
		super(itemView);
		x.view().inject(this, itemView);
		
		mDM = DownloadManager.getInstance();
		mDM.registObserver(this);// 注册下载观察者
		
		this.itemClickListener=itemClickListener;
		this.mDrawerLayout=mDrawerLayout;
		itemView.setOnClickListener(this);
	}

	@Override
	public void refreshData(SongInfo songInfo) {
		this.songInfo=songInfo;
		
		initDownload();//首次判断歌曲当前是否下载过，先获取下载列表：各种比特率
		
		VolleyHelper.imageLoader(songInfo.pic_big, iv_detail_img);
		tv_detail_name.setText(songInfo.title);
		tv_detail_songer.setText(songInfo.artist_name);
	}

	@Override
	public void onClick(View v) {
		if(itemClickListener!=null){
			itemClickListener.onItemClick(v, getPosition(), songInfo);
		}
	}

	@Event(R.id.ib_detail_download)
	private void onDetailDownlClick(View v){
		if (downloadSongInfo != null && downloadSongInfo.error_code == 22000) {
			if (downloadPopupw == null)
				downloadPopupw = new DownloadPopupw(FrameLayout.LayoutParams.MATCH_PARENT, UIUtils.dip2px(200));

			downlPopupwAdapter = new DownloadPopupwAdapter(bitrateList);
			downloadPopupw.setAdapter(downlPopupwAdapter);
			downloadPopupw.showAtLocation(mDrawerLayout, Gravity.BOTTOM, 0, -UIUtils.dip2px(100));

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
	 * 首次判断歌曲当前是否下载过，先获取下载列表：各种比特率
	 */
	private void initDownload() {
		if (songInfo != null) {
			// 先获取下载列表：各种比特率
			DownloadProtocol downloadProtocol = new DownloadProtocol() {
				@Override
				public DownloadSongInfo parseData(String result) {
					downloadSongInfo = super.parseData(result);

					if (22000 == downloadSongInfo.error_code) {
						getBitrate();// 获取各种品质的歌曲

						firstRefreshUI();// 判断歌曲当前是否下载过
					}

					return downloadSongInfo;
				}

			};
			downloadProtocol.getDataFromCache(songInfo.song_id);
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
	
	private DownloadInfo downloadInfo;
	private int mCurrentState;
	/**
	 * 判断歌曲当前是否下载过
	 */
	private void firstRefreshUI() {
		downloadInfo = mDM.getDownloadInfo(downloadSongInfo.songinfo.song_id);
		if (downloadInfo != null) {
			mCurrentState = downloadInfo.currentState;
		} else {

			downloadInfo = SPUtils.readDownloadInfo(downloadSongInfo.songinfo.song_id);
			if (downloadInfo != null) {
				if (downloadInfo.currentState == DownloadManager.STATE_DOWNLOADING)
					mCurrentState = downloadInfo.currentState = DownloadManager.STATE_PAUSE;// 将上次没有暂停下载的状态，downloading->pause
				else
					mCurrentState = downloadInfo.currentState;
			} else {
				mCurrentState = DownloadManager.STATE_UNDO;// 未下载
			}

		}
		
		refreshUI(mCurrentState,downloadInfo);
	}
	
	/**
	 * 更新状态，和进度
	 */
	protected void refreshUI(int currentState, DownloadInfo downloadInfo) {
		mCurrentState=currentState;
		switch (currentState) {
		case DownloadManager.STATE_UNDO:
			break;
		case DownloadManager.STATE_WAITTING:
			ib_detail_download.setBackgroundResource(R.drawable.ic_downloadmanage_wait);
			break;
		case DownloadManager.STATE_DOWNLOADING:
			ib_detail_download.setBackgroundResource(R.drawable.ic_download);
			break;
		case DownloadManager.STATE_PAUSE:
			ib_detail_download.setBackgroundResource(R.drawable.ic_resume);
			break;
		case DownloadManager.STATE_FAIL:
			ib_detail_download.setBackgroundResource(R.drawable.ic_downloadmanage_fail);
			break;
		case DownloadManager.STATE_SUCCESS:
			ib_detail_download.setBackgroundResource(R.drawable.ic_checkbox_blue_normal);
			break;
		case DownloadManager.STATE_CANCEL:
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
		if ((songInfo.song_id).equals(downloadInfo.song_id)) {// 判断下载对象是否是当前对象
			runOnMainRefreshUI(downloadInfo);
		}
	}

	@Override
	public void onDownloadProgressChanged(DownloadInfo downloadInfo) {
		if ((songInfo.song_id).equals(downloadInfo.song_id)) {
			runOnMainRefreshUI(downloadInfo);
		}
	}
	
}
