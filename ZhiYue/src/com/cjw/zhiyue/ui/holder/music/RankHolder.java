package com.cjw.zhiyue.ui.holder.music;

import java.util.ArrayList;
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
import com.cjw.zhiyue.ui.adapter.MyBaseAdapter;
import com.cjw.zhiyue.ui.adapter.MyBaseAdapter.OnItemClickListener;
import com.cjw.zhiyue.ui.adapter.music.RankSongAdapter;
import com.cjw.zhiyue.ui.holder.BaseHolder;
import com.cjw.zhiyue.utils.UIUtils;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RankHolder extends BaseHolder<RankInfo> {

	protected static final int GET_SUCCESS = 0;
	
	@ViewInject(R.id.tv_rank_name)
	private TextView tv_rank_name;
	@ViewInject(R.id.ll_rank_more)
	private LinearLayout ll_rank_more;
	@ViewInject(R.id.recyclerview)
	private RecyclerView mRecyclerView;
	
	private OnItemClickListener<RankInfo> itemClickListener;
	private RankInfo rankIfo;
	
	public RankHolder(View itemView,OnItemClickListener<RankInfo> itemClickListener) {
		super(itemView);
		x.view().inject(this, itemView);
		
		this.itemClickListener=itemClickListener;
	}

	@Event(value=R.id.ll_rank_more)
	private void onMoreClick(View v){
		if(itemClickListener!=null)
			itemClickListener.onItemClick(v, getPosition(), rankIfo);
	}
	
	@Override
	public void refreshData(RankInfo rankIfo) {
		this.rankIfo=rankIfo;
		tv_rank_name.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
		tv_rank_name.setText(rankIfo.billboard.name);
		
		initRecyclerView(rankIfo.song_list);// 初始化,设置水平布局，填充数据
	}

	/**
	 * 初始化每一个榜单歌曲列表
	 */
	private void initRecyclerView(ArrayList<SongInfo> song_list) {
		LinearLayoutManager layoutManager = new LinearLayoutManager(UIUtils.getContext());
		layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		mRecyclerView.setLayoutManager(layoutManager);
		mRecyclerView.setItemAnimator(new DefaultItemAnimator()); // 设置增加或删除条目的动画
		//mRecyclerView.addItemDecoration(new SpaceItemDecoration(UIUtils.dip2px(10)));//设置间距

		RankSongAdapter songAdapter = new RankSongAdapter(song_list);// 填充数据
		mRecyclerView.setAdapter(songAdapter);
		
		songAdapter.setOnItemClickListener(new OnItemClickListener<SongInfo>() {
			@Override
			public void onItemClick(View view, int position,SongInfo songInfo) {
				//请求网络，拿到播放地址
				requestSongProtocol(songInfo);
				//存储歌曲，显示在歌曲播放列表
				PlayListDao.getPlayListDao().insert(songInfo.song_id, songInfo.title, songInfo.artist_name);
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

	
}
