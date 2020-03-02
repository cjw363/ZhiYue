package com.cjw.zhiyue.ui.fragment.musicFra;

import java.util.List;

import org.xutils.x;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import com.cjw.zhiyue.R;
import com.cjw.zhiyue.db.Dao.PlayListDao;
import com.cjw.zhiyue.domain.PlaySongInfo;
import com.cjw.zhiyue.domain.RankInfo;
import com.cjw.zhiyue.domain.RankInfo.SongInfo;
import com.cjw.zhiyue.global.GlobalConstant;
import com.cjw.zhiyue.http.protocol.PlaySongProtocol;
import com.cjw.zhiyue.http.protocol.RankProtocol;
import com.cjw.zhiyue.ui.adapter.MyBaseAdapter;
import com.cjw.zhiyue.ui.adapter.MyBaseAdapter.OnItemClickListener;
import com.cjw.zhiyue.ui.fragment.BaseFragment;
import com.cjw.zhiyue.ui.holder.BaseHolder;
import com.cjw.zhiyue.ui.holder.music.RecommendHolder;
import com.cjw.zhiyue.ui.view.PagerState.ResultState;
import com.cjw.zhiyue.utils.UIUtils;

import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class RecommendFragment extends BaseFragment {

	protected static final int GET_SUCCESS = 0;
	
	@ViewInject(R.id.recyclerview)
	private RecyclerView mRecyclerView;
	@ViewInject(R.id.tv_hot)
	private TextView tv_hot;
	@ViewInject(R.id.tv_new)
	private TextView tv_new;

	private static final int TYPE_NEW = 1;
	private static final int TYPE_HOT = 2;

	private RankInfo rankInfo = null;
	
	private RecomAdapter mRecomAdapter;

	@Override
	public View onCreateSuccessView() {
		View view = View.inflate(getActivity(), R.layout.layout_fra_recommend, null);
		x.view().inject(this, view);

		tv_hot.setSelected(true);//默认选中最热
		initRecyclerView();// 初始化,设置垂直布局，填充数据
		
		return view;
	}

	@Override
	public void onLoadHttp() {
		// 默认先加载最热歌曲的数据
		requestRankProtocol(TYPE_HOT);
	}

	@Event(R.id.tv_hot)
	private void onHotClick(View v){
		if(!tv_hot.isSelected()){
			tv_hot.setSelected(true);
			tv_new.setSelected(false);
			requestRankProtocol(TYPE_HOT);//加载最热歌曲的数据
		}
	}
	@Event(R.id.tv_new)
	private void onNewClick(View v){
		if(!tv_new.isSelected()){
			tv_new.setSelected(true);
			tv_hot.setSelected(false);
			requestRankProtocol(TYPE_NEW);//加载最新歌曲的数据
		}
	}
	
	/**
	 * 给mRecyclerView设置管理者，设置垂直布局
	 */
	private void initRecyclerView() {
		final GridLayoutManager layoutManager = new GridLayoutManager(UIUtils.getContext(), 2);// 分成两列
		layoutManager.setOrientation(GridLayoutManager.VERTICAL);
		mRecyclerView.setLayoutManager(layoutManager);
		mRecyclerView.setItemAnimator(new DefaultItemAnimator()); // 设置增加或删除条目的动画

//		mRecomAdapter = new RecomAdapter(rankInfo.song_list);
//		mRecyclerView.setAdapter(mRecomAdapter);
	}

	/**
	 * 请求网络歌单
	 */
	public void requestRankProtocol(final int type) {
		RankProtocol rankProtocol = new RankProtocol() {

			@Override
			public String getParams() {
				return "&type=" + type + "&size=10";
			}

			@Override
			public RankInfo parseData(String result) {
				rankInfo = super.parseData(result);
				// 集合
				mPagerState.changCurrentState(ResultState.STATE_SUCCESS);
				updateAdapter();// 随最热新的切换更换adapter的数据
				return rankInfo;
			}

			@Override
			public void requestHttpFail() {// 请求超时，或者失败
				mPagerState.changCurrentState(ResultState.STATE_FAIL);
			}

		};
		rankProtocol.getDataFromCache("&offset=0");// 从缓存中拿数据，拿不到就请求网络
	}

	/**
	 * 请求网络，拿到播放地址
	 */
	protected void requestSongProtocol(SongInfo songInfo) {
		
		final Intent intent = new Intent(GlobalConstant.REQUEST_PLAY_SONG_ACTION);
		
		PlaySongProtocol playSongProtocol = new PlaySongProtocol(){
			@Override
			public PlaySongInfo parseData(String result) {
				//重写解析方法，网络请求成功后，去开始服务播放音乐
				PlaySongInfo playSongInfo = super.parseData(result);
				
				//发送广播
				intent.putExtra("result", GET_SUCCESS);
				intent.putExtra("playSongInfo", playSongInfo);
				UIUtils.getContext().sendBroadcast(intent);
				
				return playSongInfo;
			}
			
			@Override
			public void requestHttpFail() {
				//请求播放地址失败
				Toast.makeText(UIUtils.getContext(), "请检查网络连接", Toast.LENGTH_SHORT).show();
			}
			
		};
		playSongProtocol.getDataFromCache("&songid="+songInfo.song_id);
	}
	
	/**
	 * 随最热新的切换更换adapter的数据
	 */
	protected void updateAdapter() {
		UIUtils.runOnUIThread(new Runnable() {
			@Override
			public void run() {
				if(mRecyclerView!=null&rankInfo!=null){
					mRecomAdapter = new RecomAdapter(rankInfo.song_list);
					mRecyclerView.setAdapter(mRecomAdapter);
					
					mRecomAdapter.setOnItemClickListener(new OnItemClickListener<SongInfo>() {
						@Override
						public void onItemClick(View view, int position, SongInfo songInfo) {
							//请求网络，拿到播放地址
							requestSongProtocol(songInfo);
							//存储歌曲，显示在歌曲播放列表
							PlayListDao.getPlayListDao().insert(songInfo.song_id, songInfo.title, songInfo.artist_name);
						}
					});
				}
			}
		});
	}

	private class RecomAdapter extends MyBaseAdapter<SongInfo> {

		public RecomAdapter(List<SongInfo> data) {
			super(data);
		}

		@Override
		public void onLoadMore() {
			RankProtocol rankProtocol = new RankProtocol() {
				@Override
				public String getParams() {
					return "&type=" + rankInfo.billboard.billboard_type + "&size=10";
				}

				@Override
				public RankInfo parseData(String result) {
					rankInfo = super.parseData(result);
					// 集合
					updateMoreData(rankInfo.song_list);//添加更多数据
					return rankInfo;
				}
			};
			rankProtocol.getDataFromCache("&offset="+data.size());// 从缓存中拿数据，拿不到就请求网络
		}

		@Override
		public View initHolderView(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_recom_grid, parent, false);
			return view;
		}

		@Override
		public BaseHolder<SongInfo> getHolder(View initHolderView, OnItemClickListener<SongInfo> itemClickListener,
				int viewType) {
			return new RecommendHolder(initHolderView,itemClickListener);
		}

	}
}
