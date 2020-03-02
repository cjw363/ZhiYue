package com.cjw.zhiyue.ui.service;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.cjw.zhiyue.domain.DownloadInfo;
import com.cjw.zhiyue.domain.PlaySongInfo;
import com.cjw.zhiyue.global.GlobalConstant;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;

public class MusicService extends Service {
	protected static final int GET_SUCCESS = 0;
	protected static final int PLAY_LOCAL = 2;

	private static final int STATE_PALAYING = 0;
	private static final int STATE_PAUSE = 1;
	private static final int STATE_STOP = 2;

	private static int mCurrentState = STATE_STOP; // 默认未播放，即停止播放

	private MediaPlayer mediaPlayer;

	@Override
	public IBinder onBind(Intent intent) {
		return new MusicBinder();// 将MusicBinder传递给baseactivity
	}

	/*
	 * 第一次创建服务时
	 */
	@Override
	public void onCreate() {
		super.onCreate();

		if (mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();// 创建服务时,初始化MediaPlayer
		}
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mediaPlayer.seekTo(0);// 播完跳到起点

				mCurrentState = STATE_STOP;
				// 开始播放后更新按钮状态
				sendStateToActivity();
				sendStateToFragment();
				// 取消发送进度
				if (timer != null)
					timer.cancel();
			}
		});
		mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				mp.start();
			}
		});
	}

	/*
	 * startservice时，调用
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mediaPlayer.reset();// 将mediaPlayer对象重置到刚刚创建的状态

		int result = intent.getIntExtra("result", -1);

		PlaySongInfo playSongInfo = null;
		DownloadInfo downloadInfo = null;
		switch (result) {
		case GET_SUCCESS:
			playSongInfo = (PlaySongInfo) intent.getSerializableExtra("playSongInfo");

			if (playSongInfo != null) {// 播放本地音乐
				try {
					mediaPlayer.setDataSource(playSongInfo.bitrate.file_link);
					mediaPlayer.prepare();// 阻塞主线程
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			break;
		case PLAY_LOCAL:
			downloadInfo = (DownloadInfo) intent.getSerializableExtra("downloadInfo");

			if (downloadInfo != null) {
				try {
					mediaPlayer.setDataSource(downloadInfo.downloadPath);
					mediaPlayer.prepare();// 阻塞主线程
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			break;
		}

		mCurrentState = STATE_PALAYING;
		// 开始播放后更新按钮状态
		sendStateToActivity();
		sendStateToFragment();

		if (playSongInfo != null)// 将playSongInfo传给musicdetailfragment
			sendPSInfoToFragment(playSongInfo);
		// 将进度传给musicdetailfragment
		sendProgressToFragment();

		return super.onStartCommand(intent, flags, startId);
	}

	public class MusicBinder extends Binder {
		public void playMusic() {
			if (!mediaPlayer.isPlaying()) {
				mediaPlayer.start();// 开始播放

				if (mediaPlayer.isPlaying()) {// 防止没有音乐资源时
					mCurrentState = STATE_PALAYING;
					// 将进度传给musicdetailfragment
					sendProgressToFragment();
				}
				// 开始播放后更新按钮状态
				sendStateToActivity();
				sendStateToFragment();
			}
		}

		public void pauseMusic() {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();// 开始播放
				mCurrentState = STATE_PAUSE;

				// 开始播放后更新按钮状态
				sendStateToActivity();
				sendStateToFragment();
				// 取消发送进度
				timer.cancel();
			}
		}

		public int getDuration() {
			return mediaPlayer.getDuration();// 获取音乐总长
		}

		public void seekTo(int progress) {
			mediaPlayer.seekTo(progress);// 跳进度
		}

		public boolean getIsPlaying() {
			return mediaPlayer.isPlaying();// 获取是否正在播放
		}

	}

	private Intent intentFragment = null;
	private Timer timer;

	/**
	 * 发送进度
	 */
	private void sendProgressToFragment() {
		if (intentFragment == null)
			intentFragment = new Intent(GlobalConstant.SERVICE_ACTION_FRAG_PROGRESS);

		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				intentFragment.putExtra("progress", mediaPlayer.getCurrentPosition());
				sendBroadcast(intentFragment);
			}
		};
		timer = new Timer();

		timer.schedule(timerTask, 0, 1000);// 每隔一秒，执行timerTask
	}

	private Intent intentActivity = null;
	private Intent intentFraState = null;
	private Intent intentFraPSinfo = null;

	/**
	 * 发送当前音乐状态
	 */
	private void sendStateToActivity() {
		if (intentActivity == null)
			intentActivity = new Intent(GlobalConstant.SERVICE_ACTION_ACTIVITY_STATE);

		intentActivity.putExtra("service_state", mCurrentState);
		sendBroadcast(intentActivity);
	}

	private void sendStateToFragment() {
		if (intentFraState == null)
			intentFraState = new Intent(GlobalConstant.SERVICE_ACTION_FRAG_STATE);

		intentFraState.putExtra("service_state", mCurrentState);
		sendBroadcast(intentFraState);
	}

	/**
	 * //将playSongInfo传给musicdetailfragment
	 */
	private void sendPSInfoToFragment(PlaySongInfo playSongInfo) {
		if (intentFraPSinfo == null)
			intentFraPSinfo = new Intent(GlobalConstant.SERVICE_ACTION_FRAG_PLAYSONGINFO);

		intentFraPSinfo.putExtra("service_playsonginfo", playSongInfo);
		sendBroadcast(intentFraPSinfo);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();// 释放资源
		}
		if (timer != null) {
			timer.cancel();// 取消计时器
		}
	}
}
