package com.cjw.zhiyue.ui.fragment.detailFra;

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
import com.cjw.zhiyue.http.VolleyHelper;
import com.cjw.zhiyue.http.protocol.PlaySongProtocol;
import com.cjw.zhiyue.http.protocol.RankProtocol;
import com.cjw.zhiyue.ui.adapter.MyBaseAdapter;
import com.cjw.zhiyue.ui.adapter.MyBaseAdapter.OnItemClickListener;
import com.cjw.zhiyue.ui.fragment.BaseFragment;
import com.cjw.zhiyue.ui.holder.BaseHolder;
import com.cjw.zhiyue.ui.holder.music.RankDetailHolder;
import com.cjw.zhiyue.ui.view.PagerState.ResultState;
import com.cjw.zhiyue.utils.UIUtils;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RankDetailFragment extends BaseFragment {

	protected static final int GET_SUCCESS = 0;
	
	private static final int TYPE_HEADER_ITEM = 2;//头布局类型
	
	@ViewInject(R.id.recyclerview)
	private RecyclerView mRecyclerView;
	@ViewInject(R.id.swiperefresh)
	private SwipeRefreshLayout mSwipereFresh;
	
	private DrawerLayout mDrawerLayout;
	
	private RankInfo rankInfo;

	private RankDetailAdapter mRankDetailAdapter;
	
	public RankDetailFragment(){}
	public RankDetailFragment(DrawerLayout mDrawerLayout, RankInfo rankInfo) {
		this.mDrawerLayout=mDrawerLayout;
		this.rankInfo=rankInfo;
	}

	@Override
	public View onCreateSuccessView() {
		View view = View.inflate(getActivity(), R.layout.layout_fra_rank_detail, null);
		x.view().inject(this, view);
		
		if(rankInfo!=null){
			initRecyclerView();// 初始化,设置垂直布局，填充数据
			initSwipereFresh();// 设置下拉刷新事件
		}
		return view;
	}

	@Override
	public void onLoadHttp() {
		mPagerState.changCurrentState(ResultState.STATE_SUCCESS);
	}

	/**
	 *  初始化,设置垂直布局，填充数据
	 */
	private void initRecyclerView() {
		LinearLayoutManager layoutManager = new LinearLayoutManager(UIUtils.getContext());
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		mRecyclerView.setLayoutManager(layoutManager);
		mRankDetailAdapter = new RankDetailAdapter(rankInfo.song_list);
		mRecyclerView.setAdapter(mRankDetailAdapter);
		
		mRankDetailAdapter.setOnItemClickListener(new OnItemClickListener<RankInfo.SongInfo>() {
			@Override
			public void onItemClick(View view, int position, SongInfo songInfo) {
				//请求网络，拿到播放地址
				requestSongProtocol(songInfo);
				//存储歌曲，显示在歌曲播放列表
				PlayListDao.getPlayListDao().insert(songInfo.song_id, songInfo.title, songInfo.artist_name);
			}
		});
	}
	
	private class RankDetailAdapter extends MyBaseAdapter<SongInfo>{

		public RankDetailAdapter(List<SongInfo> data) {
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
					RankInfo bankInfo = super.parseData(result);// 走父类的解析方法,得到解析的数据，如集合
					
					updateMoreData(bankInfo.song_list);//集合添加更多数据
					return bankInfo;
				}
			};
			rankProtocol.getDataFromCache("&offset="+data.size());// 从缓存中拿数据，拿不到就请求网络
		}

		@Override
		public int getInnerType(int position) {
			if(position==0){
				return TYPE_HEADER_ITEM;//返回头的布局类型
			}else
			return super.getInnerType(position);
		}
		
		@Override
		public View initHolderView(ViewGroup parent,int viewType) {
			if(viewType==TYPE_HEADER_ITEM){
				View itemView = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_header_rank_detail, parent, false);
				return itemView; 
			}else{
				View itemView = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_detail_rank_song, parent, false);
				return itemView;
			}
		}

		@Override
		public BaseHolder<SongInfo> getHolder(View initHolderView,
				OnItemClickListener<SongInfo> itemClickListener,int viewType) {
			if(viewType==TYPE_HEADER_ITEM){
				return new InnerHeaderHolder(initHolderView); 
			}else
				return new RankDetailHolder(initHolderView,itemClickListener,mDrawerLayout);
		}
		
	}
	
	
	/**
	 * 设置下拉刷新事件
	 */
	private void initSwipereFresh() {
		mSwipereFresh.setColorSchemeColors(0xFF16A8FF);//设置加载内圈颜色
		mSwipereFresh.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mSwipereFresh.postDelayed(new Runnable() {
					@Override
					public void run() {

						mRankDetailAdapter.notifyDataSetChanged();// 通知更新
						mSwipereFresh.setRefreshing(false);// 关闭刷新progressbar
					}
				}, 500);// 2秒后刷新数据
			}
		});
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
	
	private class InnerHeaderHolder extends BaseHolder<SongInfo>{

		@ViewInject(R.id.iv_detail_img)
		private ImageView iv_detail_img;
		@ViewInject(R.id.ib_detail_back)
		private ImageButton ib_detail_back;
		@ViewInject(R.id.tv_detail_date)
		private TextView tv_detail_date;
		
		
		public InnerHeaderHolder(View itemView) {
			super(itemView);
			x.view().inject(this, itemView);
		}

		@Override
		public void refreshData(SongInfo info) {
			VolleyHelper.imageLoader(rankInfo.billboard.pic_s444, iv_detail_img);
			tv_detail_date.setText("更新日期: "+rankInfo.billboard.update_date);
		}
		
		@Event(value=R.id.ib_detail_back)
		private void onBackClick(View v){//点击按钮关闭mDrawerLayout
			FrameLayout fl_draw_content = (FrameLayout) mDrawerLayout.findViewById(R.id.fl_draw_content);
			mDrawerLayout.closeDrawer(fl_draw_content);
		}
	}
}
