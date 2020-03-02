package com.cjw.zhiyue.ui.activity;

import org.xutils.x;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import com.cjw.zhiyue.R;
import com.cjw.zhiyue.db.Dao.PlayListDao;
import com.cjw.zhiyue.domain.DownloadInfo;
import com.cjw.zhiyue.domain.PlaySongInfo;
import com.cjw.zhiyue.global.GlobalConstant;
import com.cjw.zhiyue.http.VolleyHelper;
import com.cjw.zhiyue.http.protocol.PlaySongProtocol;
import com.cjw.zhiyue.ui.fragment.detailFra.MusicDetailFragment;
import com.cjw.zhiyue.ui.service.MusicService;
import com.cjw.zhiyue.ui.service.MusicService.MusicBinder;
import com.cjw.zhiyue.ui.view.PalyListPopupw;
import com.cjw.zhiyue.utils.UIUtils;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity {

	protected static final int GET_SUCCESS = 0;
	protected static final int GET_FAIL = 1;
	protected static final int PLAY_LOCAL = 2;
	protected static final int UPDATE_INFO = 3;

	private static final int STATE_PALAYING = 0;
	private static final int STATE_PAUSE = 1;
	private static final int STATE_STOP = 2;

	//private static int mCurrentState=STATE_STOP; //默认未播放，即停止播放

	private PlaySongInfo playSongInfo=null;
	
	@ViewInject(R.id.iv_bottom_img)
	private ImageView iv_bottom_img;
	@ViewInject(R.id.tv_bottom_songname)
	private TextView tv_bottom_songname;
	@ViewInject(R.id.tv_bottom_songer)
	private TextView tv_bottom_songer;
	@ViewInject(R.id.ib_play)
	private ImageButton ib_play;
	@ViewInject(R.id.ib_next)
	private ImageButton ib_next;
	@ViewInject(R.id.ib_menu)
	private ImageButton ib_menu;

	/**
	 * 根视野
	 */
	private FrameLayout mContentContainer;
	/**
	 * 浮动视野
	 */
	private View mFloatView;

	private SongInfoReceiver songInfoReceiver;

	private Intent intentService;

	private MusicBinder musicBinder;
	
	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			musicBinder = (MusicBinder)binder;
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
	};

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewGroup mDecorView = (ViewGroup) getWindow().getDecorView();
		mContentContainer = (FrameLayout) ((ViewGroup) mDecorView.getChildAt(0)).getChildAt(1);
		mFloatView = LayoutInflater.from(getBaseContext()).inflate(R.layout.layout_float_music, null);

		x.view().inject(this, mFloatView);

		// 注册接收器
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(GlobalConstant.REQUEST_PLAY_SONG_ACTION);
		intentFilter.addAction(GlobalConstant.SERVICE_ACTION_ACTIVITY_STATE);
		songInfoReceiver = new SongInfoReceiver();
		registerReceiver(songInfoReceiver, intentFilter);
	}

	// onCreate onStart onPost onCreate onResume onPostResume
	@Override
	protected void onPostCreate(@Nullable Bundle savedInstanceState) { // Activity彻底运行起来之后执行
		super.onPostCreate(savedInstanceState);
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.gravity = Gravity.BOTTOM;
		mContentContainer.addView(mFloatView, layoutParams);
		
		// 绑定音乐服务
		intentService = new Intent(BaseActivity.this, MusicService.class);
		bindService(intentService, connection, BIND_AUTO_CREATE);
	}

	/***
	 * 重点，设置这个可以实现前进Activity时候的无动画切换
	 * 
	 * @param intent
	 */
	@Override
	public void startActivity(Intent intent) {
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);// 设置切换没有动画，用来实现活动之间的无缝切换
		super.startActivity(intent);
	}

	/**
	 * 重点，在这里设置按下返回键，或者返回button的时候无动画
	 */
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(0, 0);// 设置返回没有动画
	}

	/**
	 *广播接收器
	 */
	private class SongInfoReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action==GlobalConstant.REQUEST_PLAY_SONG_ACTION){//从RankHolder发过来的消息
				
				int result = intent.getIntExtra("result", -1);
				switch (result) {
				case GET_SUCCESS:
					playSongInfo = (PlaySongInfo) intent.getSerializableExtra("playSongInfo");
					intentService.putExtra("result", GET_SUCCESS);
					intentService.putExtra("playSongInfo", playSongInfo);
					startService(intentService);// 将歌曲信息传递给服务
					
					updateBottomBar(playSongInfo,null);// 更新底部音乐播放栏控件显示
					
					musicDetailFragment = new MusicDetailFragment(playSongInfo);
					break;
				case GET_FAIL:
					break;
				case PLAY_LOCAL:
					DownloadInfo downloadInfo = (DownloadInfo) intent.getSerializableExtra("downloadInfo");
					intentService.putExtra("result", PLAY_LOCAL);
					intentService.putExtra("downloadInfo", downloadInfo);
					startService(intentService);// 将歌曲信息传递给服务
					
					updateBottomBar(null,downloadInfo);// 更新底部音乐播放栏控件显示
					break;
				case UPDATE_INFO:
					playSongInfo = (PlaySongInfo) intent.getSerializableExtra("playSongInfo");
					
					updateBottomBar(playSongInfo,null);// 更新底部音乐播放栏控件显示
					
					musicDetailFragment = new MusicDetailFragment(playSongInfo);
					break;
				}
				
			}else if(action==GlobalConstant.SERVICE_ACTION_ACTIVITY_STATE){//从MusicService发过来的消息
				
				int service_state = intent.getIntExtra("service_state", -1);
				switch (service_state) {
				case STATE_PALAYING:
					ib_play.setSelected(true);
					isSelected=true;
					break;
				case STATE_PAUSE:
					ib_play.setSelected(false);
					isSelected=false;
					break;
				case STATE_STOP:
					ib_play.setSelected(false);
					isSelected=false;
					break;
				}
				
			}
		}

	}

	/**
	 * 更新底部音乐播放栏控件显示
	 */
	public void updateBottomBar(PlaySongInfo playSongInfo,DownloadInfo downloadInfo) {
		if(downloadInfo!=null){
			iv_bottom_img.setBackgroundResource(R.drawable.pcsync_failure);//默认图片
			tv_bottom_songname.setText(downloadInfo.song_name);
			tv_bottom_songer.setText(downloadInfo.songer);
		}
		if(playSongInfo!=null){
			VolleyHelper.imageLoader(playSongInfo.songinfo.pic_big, iv_bottom_img);// 歌曲封面
			tv_bottom_songname.setText(playSongInfo.songinfo.title);
			tv_bottom_songer.setText(playSongInfo.songinfo.author);
		}
	}
	
	private boolean isSelected=false;
	
	@Event(value=R.id.ib_play)
	private void onPlayClick(View v){
		if(playSongInfo==null){//当前没有播放音乐，播放列表第一首
			String firstSongid = PlayListDao.getPlayListDao().queryFirstSong();
			if(firstSongid!=null){
				requestSongProtocol(firstSongid);
			}else
				Toast.makeText(UIUtils.getContext(), "没有更多歌曲了", Toast.LENGTH_SHORT).show();
		}
		
		if(!isSelected){//未播放
			musicBinder.playMusic();
		}else{//播放中
			musicBinder.pauseMusic();
		}
	}
	
	private MusicDetailFragment musicDetailFragment=null;
	/**
	 * 弹出音乐详情页
	 */
	@Event(value=R.id.rl_float_bar)
	private void onBottomMusicClick(View v){
		if(musicDetailFragment==null)
			musicDetailFragment = new MusicDetailFragment();
			
			musicDetailFragment.show(getSupportFragmentManager(), "MusicDetailFragment");
	}

	private PalyListPopupw popupWindow=null;
	@Event(value=R.id.ib_menu)
	private void onShowMenuClic(View v){
		if(popupWindow==null)
			popupWindow = new PalyListPopupw(FrameLayout.LayoutParams.MATCH_PARENT, UIUtils.getHeightPixels(0.6));
		popupWindow.showAsDropDown(mFloatView, 0, -(int)(UIUtils.getHeightPixels(0.6)+UIUtils.dip2px(52)));
		popupWindow.setAdaperData();//重新获取列表数据
	}
		
	@Event(value=R.id.ib_next)
	private void onNextClick(View v){
		if(playSongInfo!=null){
			//查询歌曲列表中的下一曲
			String nextSong_id = PlayListDao.getPlayListDao().queryNextSong(playSongInfo.songinfo.song_id);
			if(nextSong_id!=null){
				//有下一首id
				requestSongProtocol(nextSong_id);
			}else
				Toast.makeText(UIUtils.getContext(), "没有更多歌曲了", Toast.LENGTH_SHORT).show();
			
		}else{
			//当前没有播放歌曲
			String nextSong_id = PlayListDao.getPlayListDao().queryNextSong(null);
			if(nextSong_id!=null){
				//播放第一首id
				requestSongProtocol(nextSong_id);
			}else
				Toast.makeText(UIUtils.getContext(), "没有更多歌曲了", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 请求网络，拿到播放地址
	 */
	protected void requestSongProtocol(String song_id) {
		
		PlaySongProtocol playSongProtocol = new PlaySongProtocol(){
			@Override
			public PlaySongInfo parseData(String result) {
				//重写解析方法，网络请求成功后，去开始服务播放音乐
				playSongInfo = super.parseData(result);
				
				intentService.putExtra("playSongInfo", playSongInfo);
				startService(intentService);// 将歌曲信息传递给服务
				
				updateBottomBar(playSongInfo,null);// 更新底部音乐播放栏控件显示
				
				musicDetailFragment = new MusicDetailFragment(playSongInfo);
				
				return playSongInfo;
			}
			
			@Override
			public void requestHttpFail() {
				//请求播放地址失败
				Toast.makeText(UIUtils.getContext(), "请检查网络连接", Toast.LENGTH_SHORT).show();
			}
			
		};
		playSongProtocol.getDataFromCache("&songid="+song_id);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (songInfoReceiver != null) {// 取消注册
			unregisterReceiver(songInfoReceiver);
		}

		unbindService(connection);// 解绑服务
		stopService(intentService);
	}
}
