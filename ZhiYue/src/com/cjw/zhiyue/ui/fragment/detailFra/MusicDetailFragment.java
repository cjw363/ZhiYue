package com.cjw.zhiyue.ui.fragment.detailFra;

import java.util.ArrayList;
import java.util.List;

import org.xutils.x;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import com.cjw.zhiyue.R;
import com.cjw.zhiyue.db.Dao.PlayListDao;
import com.cjw.zhiyue.domain.DownloadInfo;
import com.cjw.zhiyue.domain.DownloadSongInfo;
import com.cjw.zhiyue.domain.DownloadSongInfo.DownloadBitrate;
import com.cjw.zhiyue.domain.PlaySongInfo;
import com.cjw.zhiyue.global.GlobalConstant;
import com.cjw.zhiyue.http.LrcProtocol;
import com.cjw.zhiyue.http.VolleyHelper;
import com.cjw.zhiyue.http.manager.DownloadManager;
import com.cjw.zhiyue.http.manager.DownloadManager.DownloadObserver;
import com.cjw.zhiyue.http.protocol.DownloadProtocol;
import com.cjw.zhiyue.http.protocol.PlaySongProtocol;
import com.cjw.zhiyue.ui.adapter.MyBaseAdapter.OnItemClickListener;
import com.cjw.zhiyue.ui.adapter.popupw.DownloadPopupwAdapter;
import com.cjw.zhiyue.ui.service.MusicService;
import com.cjw.zhiyue.ui.service.MusicService.MusicBinder;
import com.cjw.zhiyue.ui.view.DownloadPopupw;
import com.cjw.zhiyue.ui.view.PalyListPopupw;
import com.cjw.zhiyue.ui.view.libs.CircleImageView;
import com.cjw.zhiyue.ui.view.libs.lrc.ILrcBuilder;
import com.cjw.zhiyue.ui.view.libs.lrc.impl.DefaultLrcBuilder;
import com.cjw.zhiyue.ui.view.libs.lrc.impl.LrcRow;
import com.cjw.zhiyue.ui.view.libs.lrc.impl.LrcView;
import com.cjw.zhiyue.utils.SPUtils;
import com.cjw.zhiyue.utils.StringUtils;
import com.cjw.zhiyue.utils.UIUtils;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MusicDetailFragment extends DialogFragment implements DownloadObserver {

	private static final int STATE_PALAYING = 0;
	private static final int STATE_PAUSE = 1;
	private static final int STATE_STOP = 2;

	protected static final int GET_SUCCESS = 0;
	protected static final int GET_FAIL = 1;

	@ViewInject(R.id.viewpager)
	private ViewPager mViewPager;
	@ViewInject(R.id.ib_dimiss_dialog)
	private ImageButton ib_dimiss_dialog;
	@ViewInject(R.id.point_1)
	private ImageView point_1;
	@ViewInject(R.id.point_2)
	private ImageView point_2;

	@ViewInject(R.id.tv_time_progress)
	private TextView tv_time_progress;
	@ViewInject(R.id.tv_time_total)
	private TextView tv_time_total;
	@ViewInject(R.id.seekbar)
	private SeekBar seekbar;
	@ViewInject(R.id.ib_play)
	private ImageButton ib_play;

	@ViewInject(R.id.ib_download)
	private ImageButton ib_download;

	private Window mWindow;

	private PlaySongInfo playSongInfo = null;// 歌曲信息

	private DownloadSongInfo downloadSongInfo = null;// 歌曲下载详细信息

	private RotateAnimation rotateAnimation;

	private MusicProgressReceiver progressReceiver;

	private Intent intentService;

	private DownloadManager mDM;

	protected int mCurrentState;

	private MusicBinder musicBinder = null;
	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			musicBinder = (MusicBinder) binder;
			setSeekBar();// 设置seekbar

			boolean isPlaying = musicBinder.getIsPlaying();// dialog起来时音乐状态
			isSelected = isPlaying;
			ib_play.setSelected(isSelected);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
	};

	public MusicDetailFragment() {
	}

	public MusicDetailFragment(PlaySongInfo playSongInfo) {
		this.playSongInfo = playSongInfo;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mWindow = getDialog().getWindow();
		mWindow.requestFeature(Window.FEATURE_NO_TITLE);// 必须放在setContextView之前调用

		View view = inflater.inflate(R.layout.layout_fra_music_detail,
				(ViewGroup) mWindow.findViewById(android.R.id.content), false);// 需要用android.R.id.content这个view
		x.view().inject(this, view);

		mDM = DownloadManager.getInstance();
		mDM.registObserver(this);// 注册下载观察者

		// 绑定音乐服务
		intentService = new Intent(getActivity(), MusicService.class);
		getActivity().bindService(intentService, connection, getActivity().BIND_AUTO_CREATE);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle arg0) {

		setWindow();// 设置窗体属性

		registerBroadcast();// 注册广播

		initAnimation();// 初始化动画

		setViewPager();// 设置viewpager
		
		setSeekBar();// 设置seekbar

		setBottomBar();// 设置音乐播放底栏按钮控件

		initDownload();// 首次判断歌曲当前是否下载过

		super.onActivityCreated(arg0);
	}

	/**
	 * 首次判断歌曲当前是否下载过，先获取下载列表：各种比特率
	 */
	private void initDownload() {
		if (playSongInfo != null) {
			// 先获取下载列表：各种比特率
			DownloadProtocol downloadProtocol = new DownloadProtocol() {
				@Override
				public DownloadSongInfo parseData(String result) {
					downloadSongInfo = super.parseData(result);

					if (22000 == downloadSongInfo.error_code) {
						getBitrate();// 获取各种品质的歌曲

//						firstRefreshUI();// 判断歌曲当前是否下载过
					}

					return downloadSongInfo;
				}

			};
			downloadProtocol.getDataFromCache(playSongInfo.songinfo.song_id);
		}
	}

	/**
	 * 更新下载按钮的状态
	 */
	protected void refreshUI(int mCurrentState) {
		this.mCurrentState = mCurrentState;// 修改当前的下载状态

		switch (mCurrentState) {
		case DownloadManager.STATE_UNDO:
			ib_download.setBackgroundResource(R.drawable.bt_playpage_button_download_normal_new);
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
			ib_download.setBackgroundResource(R.drawable.bt_playpage_button_download_activited_new);
			break;
		case DownloadManager.STATE_CANCEL:
			break;
		}
	}

	private DownloadInfo downloadInfo;

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

		refreshUI(mCurrentState);
	}

	/**
	 * 获取各种品质的歌曲
	 */
	protected void getBitrate() {
		ArrayList<DownloadBitrate> bitrate = downloadSongInfo.bitrate;
		bitrateList = new ArrayList<DownloadBitrate>();

		if(bitrate!=null)
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
	 * 设置音乐播放底栏按钮控件
	 */
	private void setBottomBar() {
		if (playSongInfo != null) {
			String file_duration = StringUtils.secToTime(playSongInfo.bitrate.file_duration);
			tv_time_total.setText(file_duration);// 设置歌曲时长
		}
	}

	/**
	 * 设置seekbar进度条
	 */
	private void setSeekBar() {
		if (playSongInfo != null && musicBinder != null) {
			seekbar.setMax(musicBinder.getDuration());
			seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					int progress = seekbar.getProgress();
					musicBinder.seekTo(progress);
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				}
			});
		}
	}

	/**
	 * 注册广播
	 */
	private void registerBroadcast() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(GlobalConstant.SERVICE_ACTION_FRAG_PROGRESS);
		intentFilter.addAction(GlobalConstant.SERVICE_ACTION_FRAG_STATE);
		intentFilter.addAction(GlobalConstant.SERVICE_ACTION_FRAG_PLAYSONGINFO);
		progressReceiver = new MusicProgressReceiver();
		getActivity().registerReceiver(progressReceiver, intentFilter);
	}

	/**
	 * 设置viewpager
	 */
	private void setViewPager() {
		mViewPager.setAdapter(new ViewPagerAdapger());

		// 设置指示器
		mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				if (arg0 == 0) {
					point_1.setSelected(true);
					point_2.setSelected(false);
				} else {
					point_2.setSelected(true);
					point_1.setSelected(false);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		point_1.setSelected(true);
	}

	/**
	 * 接收音乐进度的广播接收器
	 */
	private class MusicProgressReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action == GlobalConstant.SERVICE_ACTION_FRAG_PLAYSONGINFO) {

				PlaySongInfo playSongInfo = (PlaySongInfo) intent.getSerializableExtra("service_playsonginfo");
				MusicDetailFragment.this.playSongInfo = playSongInfo;

				// 更新MusicDetailFragment的歌曲信息
				updateMusicDetail();

			} else if (action == GlobalConstant.SERVICE_ACTION_FRAG_PROGRESS) {

				int progress = intent.getIntExtra("progress", -1);
				seekbar.setProgress(progress);// 更新进度条
				mLrcView.seekLrcToTime(progress);// 更新歌词进度
				
				int currentDuration = progress * (playSongInfo.bitrate.file_duration) / musicBinder.getDuration();
				tv_time_progress.setText(StringUtils.secToTime(currentDuration));// 更新音乐当前已播放的时间

			} else if (action == GlobalConstant.SERVICE_ACTION_FRAG_STATE) {// 从MusicService发过来的消息

				int service_state = intent.getIntExtra("service_state", -1);
				switch (service_state) {
				case STATE_PALAYING:
					ib_play.setSelected(true);
					isSelected = true;
					break;
				case STATE_PAUSE:
					ib_play.setSelected(false);
					isSelected = false;
					break;
				case STATE_STOP:
					ib_play.setSelected(false);
					isSelected = false;
					break;
				}

			}
		}

	}

	@Event(value = R.id.ib_dimiss_dialog)
	private void onDimissClick(View v) {
		dismiss();
	}

	/**
	 * 更新MusicDetailFragment的歌曲信息
	 */
	public void updateMusicDetail() {
		setSeekBar();// 设置seekbar

		setBottomBar();// 设置音乐播放底栏按钮控件

		updateSongInfo();// 更新歌曲封面，歌曲名，歌手名

		initSongLrc();// 初始化歌词
		
		initDownload();// 首次判断歌曲当前是否下载过
	}

	/**
	 * 更新歌曲封面，歌曲名，歌手名
	 */
	public void updateSongInfo() {
		VolleyHelper.imageLoader(playSongInfo.songinfo.pic_premium, civ_song_img);
		tv_song_name.setText(playSongInfo.songinfo.title);
		tv_songer.setText("—" + playSongInfo.songinfo.author + "—");
	}

	private CircleImageView civ_song_img = null;
	private TextView tv_song_name = null;
	private TextView tv_songer = null;
	private LrcView mLrcView;
	
	private class ViewPagerAdapger extends PagerAdapter {

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			if (position == 0) {
				View view = UIUtils.inflate(R.layout.pager_music_premiun);

				/* 设置封面，歌曲名，歌手 */
				civ_song_img = (CircleImageView) view.findViewById(R.id.civ_song_img);
				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) civ_song_img.getLayoutParams();
				params.width=UIUtils.getWidthPixels(0.75);//屏幕宽度的3/4
				params.height=UIUtils.getWidthPixels(0.75);
				civ_song_img.setLayoutParams(params);
				
				tv_song_name = (TextView) view.findViewById(R.id.tv_song_name);
				tv_songer = (TextView) view.findViewById(R.id.tv_songer);
				civ_song_img.setAnimation(rotateAnimation);
				if (playSongInfo != null)
					updateSongInfo();// 更新歌曲封面，歌曲名，歌手名

				container.addView(view);
				return view;
			} else {
				View view = UIUtils.inflate(R.layout.pager_music_lrc);
				mLrcView = (LrcView) view.findViewById(R.id.lrcview);
				
				initSongLrc();// 初始化歌词
				
				container.addView(view);
				return view;
			}
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

	/**
	 * 初始化动画
	 */
	private void initAnimation() {
		rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotateAnimation.setDuration(100000);
		rotateAnimation.setRepeatMode(Animation.RESTART);
		rotateAnimation.setRepeatCount(-1);
		rotateAnimation.setInterpolator(new LinearInterpolator());// 实现匀速动画
		rotateAnimation.setFillAfter(true);
	}

	/**
	 * 请求网络，拿到播放地址
	 */
	protected void requestSongProtocol(String song_id) {

		final Intent intent = new Intent(GlobalConstant.REQUEST_PLAY_SONG_ACTION);

		PlaySongProtocol playSongProtocol = new PlaySongProtocol() {
			@Override
			public PlaySongInfo parseData(String result) {
				// 重写解析方法，网络请求成功后，去开始服务播放音乐
				playSongInfo = super.parseData(result);

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

				// 发送广播
				intent.putExtra("result", GET_FAIL);
				UIUtils.getContext().sendBroadcast(intent);
			}

		};
		playSongProtocol.getDataFromCache("&songid=" + song_id);
	}

	/**
	 * 初始化歌词
	 */
	private void initSongLrc() {
		if(playSongInfo!=null&&mLrcView!=null){
			//获取歌词
			LrcProtocol lrcProtocol = new LrcProtocol() {
				
				@Override
				public void parseData(String songLrc) {
					//解析歌词构造器
			        ILrcBuilder builder = new DefaultLrcBuilder();
			        //解析歌词返回LrcRow集合
			        List<LrcRow> rows = builder.getLrcRows(songLrc);
			      //将得到的歌词集合传给mLrcView用来展示
			        mLrcView.setLrc(rows);
				}
			};
			lrcProtocol.getLrcFromServer(playSongInfo);
		}
	}
	
	/**
	 * 设置窗体属性
	 */
	private void setWindow() {
		// mWindow.setLayout(500, 500);//设置高度
		// setLayout必须 在 setContentView之后, 调用;并且 setBackgroundDrawable 必须设置

		// 透明状态栏
		mWindow.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		// 退出,进入动画
		mWindow.setWindowAnimations(R.style.animat_dialog);
		// 清理背景变暗
		mWindow.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		// 点击window外的区域 是否消失
		getDialog().setCanceledOnTouchOutside(true);
		// 是否可以取消,会影响上面那条属性
		setCancelable(true);
		// window外可以点击,不拦截窗口外的事件
		mWindow.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
		// 设置背景颜色,只有设置了这个属性,宽度才能全屏MATCH_PARENT
		mWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		WindowManager.LayoutParams mWindowAttributes = mWindow.getAttributes();
		mWindowAttributes.width = WindowManager.LayoutParams.MATCH_PARENT;// 这个属性需要配合透明背景颜色,才会真正的
																			// MATCH_PARENT
		mWindowAttributes.height = WindowManager.LayoutParams.MATCH_PARENT;
		// mWindowAttributes.flags=
		// WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN// 覆盖状态栏
		// WindowManager.LayoutParams.FLAG_FULLSCREEN;//全屏
		// gravity
		// mWindowAttributes.gravity = getGravity();
		mWindow.setAttributes(mWindowAttributes);
	}

	@Override
	public void onDetach() {
		super.onDetach();

		if (progressReceiver != null)// 取消注册
			getActivity().unregisterReceiver(progressReceiver);

		getActivity().unbindService(connection);// 解绑服务
	}

	private boolean isSelected = false;

	@Event(value = R.id.ib_play)
	private void onPlayClick(View v) {
		if (playSongInfo == null) {// 当前没有播放音乐，播放列表第一首
			String firstSongid = PlayListDao.getPlayListDao().queryFirstSong();
			if (firstSongid != null) {
				requestSongProtocol(firstSongid);
			} else
				Toast.makeText(UIUtils.getContext(), "没有更多歌曲了", Toast.LENGTH_SHORT).show();
		}

		if (!isSelected) {// 未播放
			musicBinder.playMusic();
		} else {// 播放中
			musicBinder.pauseMusic();
		}
	}

	@Event(value = R.id.ib_previous)
	private void onPreviousClick(View v) {
		if (playSongInfo != null) {
			// 查询歌曲列表中的下一曲
			String PreSong_id = PlayListDao.getPlayListDao().queryPreviouSong(playSongInfo.songinfo.song_id);
			if (PreSong_id != null) {
				// 有下一首id
				requestSongProtocol(PreSong_id);
			} else
				Toast.makeText(UIUtils.getContext(), "没有更多歌曲了", Toast.LENGTH_SHORT).show();

		} else {
			// 当前没有播放歌曲
			String PreSong_id = PlayListDao.getPlayListDao().queryPreviouSong(null);
			if (PreSong_id != null) {
				// 播放第一首id
				requestSongProtocol(PreSong_id);
			} else
				Toast.makeText(UIUtils.getContext(), "没有更多歌曲了", Toast.LENGTH_SHORT).show();
		}
	}

	@Event(value = R.id.ib_next)
	private void onNextClick(View v) {
		if (playSongInfo != null) {
			// 查询歌曲列表中的下一曲
			String nextSong_id = PlayListDao.getPlayListDao().queryNextSong(playSongInfo.songinfo.song_id);
			if (nextSong_id != null) {
				// 有下一首id
				requestSongProtocol(nextSong_id);
			} else
				Toast.makeText(UIUtils.getContext(), "没有更多歌曲了", Toast.LENGTH_SHORT).show();

		} else {
			// 当前没有播放歌曲
			String nextSong_id = PlayListDao.getPlayListDao().queryNextSong(null);
			if (nextSong_id != null) {
				// 播放第一首id
				requestSongProtocol(nextSong_id);
			} else
				Toast.makeText(UIUtils.getContext(), "没有更多歌曲了", Toast.LENGTH_SHORT).show();
		}
	}

	private PalyListPopupw playListPopupw = null;

	@Event(value = R.id.ib_menu)
	private void onShowMenuClic(View v) {
		if (playListPopupw == null)
			playListPopupw = new PalyListPopupw(FrameLayout.LayoutParams.MATCH_PARENT, UIUtils.getHeightPixels(0.6));
		playListPopupw.showAtLocation(this.getView(), Gravity.BOTTOM, 0, -UIUtils.getHeightPixels(0.6));
		playListPopupw.setAdaperData();// 重新获取列表数据
	}

	private DownloadPopupw downloadPopupw = null;
	private ArrayList<DownloadBitrate> bitrateList;
	private DownloadPopupwAdapter downlPopupwAdapter;

	@Event(value = R.id.ib_download)
	private void onDownloadClick(View v) {
		if (downloadSongInfo != null && downloadSongInfo.error_code == 22000) {
			if (downloadPopupw == null)
				downloadPopupw = new DownloadPopupw(FrameLayout.LayoutParams.MATCH_PARENT, UIUtils.dip2px(200));

			downlPopupwAdapter = new DownloadPopupwAdapter(bitrateList);
			downloadPopupw.setAdapter(downlPopupwAdapter);
			downloadPopupw.showAtLocation(this.getView(), Gravity.BOTTOM, 0, -UIUtils.dip2px(100));

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
						// mDM.pause(downloadSongInfo);
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
	 * 主线程更新按钮状态，和进度
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
		if(downloadSongInfo!=null)
		if ((downloadSongInfo.songinfo.song_id).equals(downloadInfo.song_id)) {// 判断下载对象是否是当前对象
			runOnMainRefreshUI(downloadInfo);
		}
	}

	@Override
	public void onDownloadProgressChanged(DownloadInfo downloadInfo) {
		if(downloadSongInfo!=null)
		if ((downloadSongInfo.songinfo.song_id).equals(downloadInfo.song_id)) {
			runOnMainRefreshUI(downloadInfo);
		}
	}
}
