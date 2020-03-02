package com.cjw.zhiyue.ui.fragment.musicFra;

import java.util.ArrayList;
import java.util.List;

import org.xutils.x;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import com.cjw.zhiyue.R;
import com.cjw.zhiyue.db.Dao.PlayListDao;
import com.cjw.zhiyue.db.domain.SongInfo;
import com.cjw.zhiyue.domain.DownloadInfo;
import com.cjw.zhiyue.domain.PlaySongInfo;
import com.cjw.zhiyue.global.GlobalConstant;
import com.cjw.zhiyue.http.manager.DownloadManager;
import com.cjw.zhiyue.http.protocol.PlaySongProtocol;
import com.cjw.zhiyue.ui.adapter.MyBaseAdapter.OnItemClickListener;
import com.cjw.zhiyue.ui.adapter.music.DownloadedAdapter;
import com.cjw.zhiyue.ui.adapter.popupw.PlayListPopupwAdapter;
import com.cjw.zhiyue.ui.fragment.BaseFragment;
import com.cjw.zhiyue.ui.view.PagerState.ResultState;
import com.cjw.zhiyue.utils.SPUtils;
import com.cjw.zhiyue.utils.UIUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class HomeFragment extends BaseFragment {
	protected static final int GET_SUCCESS = 0;

	@ViewInject(R.id.recent_recyclerview)
	private RecyclerView recent_recyclerview;
	@ViewInject(R.id.downl_recyclerview)
	private RecyclerView downl_recyclerview;
	@ViewInject(R.id.like_recyclerview)
	private RecyclerView like_recyclerview;
	
	@ViewInject(R.id.iv_recent_arrow)
	private ImageView iv_recent_arrow;
	@ViewInject(R.id.iv_downl_arrow)
	private ImageView iv_downl_arrow;
	@ViewInject(R.id.iv_like_arrow)
	private ImageView iv_like_arrow;

	private ArrayList<DownloadInfo> downloadedList;
	private List<SongInfo> songPlayList;

	@Override
	public View onCreateSuccessView() {
		View view = View.inflate(getActivity(), R.layout.layout_fra_home, null);
		x.view().inject(this, view);

		initRecyclerView();// 获取设置RecyclerView的各种参数

		return view;
	}

	@Override
	public void onLoadHttp() {
		initDownloadList();// 初始化分类下载列表
		initRecentPlayList();//初始化最近播放列表
		
		mPagerState.changCurrentState(ResultState.STATE_SUCCESS);
	}

	/**
	 * 初始化分类下载列表
	 */
	private void initDownloadList() {
		ArrayList<DownloadInfo> downloadInfoList = SPUtils.getDownloadInfoList();
		downloadedList = new ArrayList<DownloadInfo>();

		downloadedList.clear();// 清除之前的数据
		if (downloadInfoList != null)
			for (DownloadInfo d : downloadInfoList) {
				if (d.currentState == DownloadManager.STATE_SUCCESS) {
					// 下载完成
					downloadedList.add(d);
				}
			}
	}


	/**
	 * 初始化最近播放列表
	 */
	private void initRecentPlayList() {
		songPlayList = PlayListDao.getPlayListDao().query();
	}

	/**
	 * 获取设置RecyclerView的各种参数
	 */
	private void initRecyclerView() {
		LinearLayoutManager recent_manager = new LinearLayoutManager(UIUtils.getContext());
		recent_manager.setOrientation(LinearLayoutManager.VERTICAL);
		recent_recyclerview.setLayoutManager(recent_manager);
		PlayListPopupwAdapter playListAdapter = new PlayListPopupwAdapter(songPlayList);
		recent_recyclerview.setAdapter(playListAdapter);
		playListAdapter.setOnItemClickListener(new OnItemClickListener<SongInfo>() {
			@Override
			public void onItemClick(View view, int position, SongInfo info) {
				requestSongProtocol(info);// 请求网络播放音乐
			}
		});
		
		
		LinearLayoutManager downl_manager = new LinearLayoutManager(UIUtils.getContext());
		downl_manager.setOrientation(LinearLayoutManager.VERTICAL);
		downl_recyclerview.setLayoutManager(downl_manager);
		downl_recyclerview.setAdapter(new DownloadedAdapter(downloadedList));

		LinearLayoutManager like_manager = new LinearLayoutManager(UIUtils.getContext());
		like_manager.setOrientation(LinearLayoutManager.VERTICAL);
		like_recyclerview.setLayoutManager(like_manager);
		//like_recyclerview.setAdapter(new DownloadedAdapter(downloadedList));
	}

	private boolean recent_isOpen;
	@Event(R.id.ll_recent_play)
	private void onSwitchRecentClick(View v) {
		ValueAnimator animator;
		if (recent_isOpen) {
			recent_isOpen = false;
			animator = ValueAnimator.ofInt(UIUtils.getHeightPixels(0.5), 0);
			iv_recent_arrow.setBackgroundResource(R.drawable.arrow_down);
		} else {
			recent_isOpen = true;
			animator = ValueAnimator.ofInt(0, UIUtils.getHeightPixels(0.5));
			iv_recent_arrow.setBackgroundResource(R.drawable.arrow_up);
		}

		animator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animator) {
				LinearLayout.LayoutParams recent_params = (LinearLayout.LayoutParams) recent_recyclerview
						.getLayoutParams();
				int height = (Integer) animator.getAnimatedValue();
				recent_params.height = height;
				recent_recyclerview.setLayoutParams(recent_params);
			}
		});
		animator.setDuration(500);
		animator.start();
	}
	
	private boolean downl_isOpen;
	@Event(R.id.ll_download)
	private void onSwitchDownlClick(View v) {
		ValueAnimator animator;
		if (downl_isOpen) {
			downl_isOpen = false;
			animator = ValueAnimator.ofInt(UIUtils.getHeightPixels(0.4), 0);
			iv_downl_arrow.setBackgroundResource(R.drawable.arrow_down);
		} else {
			downl_isOpen = true;
			animator = ValueAnimator.ofInt(0, UIUtils.getHeightPixels(0.4));
			iv_downl_arrow.setBackgroundResource(R.drawable.arrow_up);
		}

		animator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animator) {
				LinearLayout.LayoutParams downl_params = (LinearLayout.LayoutParams) downl_recyclerview
						.getLayoutParams();
				int height = (Integer) animator.getAnimatedValue();
				downl_params.height = height;
				downl_recyclerview.setLayoutParams(downl_params);
			}
		});
		animator.setDuration(500);
		animator.start();
	}
	
	private boolean like_isOpen=true;
	@Event(R.id.ll_like)
	private void onSwitchLikeClick(View v) {
		ValueAnimator animator;
		if (like_isOpen) {
			like_isOpen = false;
			animator = ValueAnimator.ofInt(UIUtils.getHeightPixels(0.6), 0);
			iv_like_arrow.setBackgroundResource(R.drawable.arrow_down);
		} else {
			like_isOpen = true;
			animator = ValueAnimator.ofInt(0, UIUtils.getHeightPixels(0.6));
			iv_like_arrow.setBackgroundResource(R.drawable.arrow_up);
		}

		animator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animator) {
				LinearLayout.LayoutParams recent_params = (LinearLayout.LayoutParams) like_recyclerview
						.getLayoutParams();
				int height = (Integer) animator.getAnimatedValue();
				recent_params.height = height;
				like_recyclerview.setLayoutParams(recent_params);
			}
		});
		animator.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator arg0) {}
			@Override
			public void onAnimationRepeat(Animator arg0) {}
			@Override
			public void onAnimationEnd(Animator animator) {
				if (like_isOpen) {
					iv_like_arrow.setBackgroundResource(R.drawable.arrow_down);
				} else {
					iv_like_arrow.setBackgroundResource(R.drawable.arrow_up);
				}
			}
			@Override
			public void onAnimationCancel(Animator arg0) {}
		});
		animator.setDuration(500);
		animator.start();
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
