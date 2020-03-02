package com.cjw.zhiyue.ui.fragment.musicFra;

import java.util.ArrayList;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import com.cjw.zhiyue.R;
import com.cjw.zhiyue.domain.RankInfo;
import com.cjw.zhiyue.http.protocol.RankProtocol;
import com.cjw.zhiyue.ui.adapter.MyBaseAdapter.OnItemClickListener;
import com.cjw.zhiyue.ui.adapter.SpaceItemDecoration;
import com.cjw.zhiyue.ui.adapter.music.RankAdapter;
import com.cjw.zhiyue.ui.fragment.BaseFragment;
import com.cjw.zhiyue.ui.fragment.detailFra.RankDetailFragment;
import com.cjw.zhiyue.ui.view.PagerState.ResultState;
import com.cjw.zhiyue.utils.UIUtils;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

public class RankFragment extends BaseFragment {

	@ViewInject(R.id.recyclerview)
	private RecyclerView mRecyclerView;
	@ViewInject(R.id.drawerlayout)
	private DrawerLayout mDrawerLayout;
	@ViewInject(R.id.fl_draw_content)
	private FrameLayout fl_draw_content;
	
	private ArrayList<RankInfo> rankList;// 榜单集合

	@Override
	public View onCreateSuccessView() {
		View view = View.inflate(getActivity(), R.layout.layout_fra_rank, null);
		x.view().inject(this, view);

		initRecyclerView();// 初始化,设置垂直布局，填充数据
		initDrawerLayout();//设置mRecyclerView
		
		return view;
	}

	/**
	 * 给mRecyclerView设置管理者，设置垂直布局
	 */
	private void initRecyclerView() {
		LinearLayoutManager layoutManager = new LinearLayoutManager(UIUtils.getContext());
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		mRecyclerView.setLayoutManager(layoutManager);
		mRecyclerView.setItemAnimator(new DefaultItemAnimator()); // 设置增加或删除条目的动画
		mRecyclerView.addItemDecoration(new SpaceItemDecoration(UIUtils.dip2px(10)));//设置间距

		RankAdapter rankAdapter = new RankAdapter(rankList);
		rankAdapter.setOnItemClickListener(new OnItemClickListener<RankInfo>() {
			@Override
			public void onItemClick(View view, int position, RankInfo info) {
				FragmentManager fm = getChildFragmentManager();
				FragmentTransaction transaction = fm.beginTransaction();//开启事务
				
				if(!mDrawerLayout.isDrawerOpen(fl_draw_content)){
					mDrawerLayout.openDrawer(fl_draw_content);//打开DrawerLayout
					transaction.replace(R.id.fl_draw_content, new RankDetailFragment(mDrawerLayout,info),"RankDetailFragment");
					transaction.commit();
				}
			}
		});
		
		mRecyclerView.setAdapter(rankAdapter);// 填充数据
	}

	/**
	 * 设置mRecyclerView
	 */
	private void initDrawerLayout() {
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);//关闭侧滑滑动
		mDrawerLayout.addDrawerListener(new DrawerListener() {
			@Override
			public void onDrawerStateChanged(int arg0) {}
			@Override
			public void onDrawerSlide(View arg0, float arg1) {}
			@Override
			public void onDrawerOpened(View arg0) {
				//mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);//开启侧滑滑动
			}
			@Override
			public void onDrawerClosed(View arg0) {
				//mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);//关闭侧滑滑动
				
				FragmentManager fm = getChildFragmentManager();
				FragmentTransaction transaction = fm.beginTransaction();//开启事务
				mDrawerLayout.closeDrawer(fl_draw_content);
				transaction.remove(fm.findFragmentByTag("RankDetailFragment"));
				transaction.commit();
			}
		});	
	}

	
	private int[] mRankInts = new int[] { 1, 2, 11, 20, 21, 23, 24, 25 };//榜单的type
	//1-新歌榜，2-热歌榜，6，KTV热歌榜，8-Billboard,11-摇滚榜,12-爵士,16-流行
	//,22-经典老歌榜,23-情歌对唱榜,24-影视金曲榜,20-华语金曲榜，21-欧美金曲榜，25-网络歌曲榜
	/*
	 * 加载网络数据
	 */
	@Override
	public void onLoadHttp() {
		rankList = new ArrayList<RankInfo>();// 榜单集合

		for (final int i : mRankInts) {
			RankProtocol rankProtocol = new RankProtocol() {
				
				@Override
				public String getParams() {
					return "&type=" + i + "&size=10";
				}
				@Override
				public RankInfo parseData(String result) {
					RankInfo rankInfo = super.parseData(result);// 走父类的解析方法,得到解析的数据，如
																// 集合
					rankList.add(rankInfo);
					if (rankList.size()==mRankInts.length) {
						mPagerState.changCurrentState(ResultState.STATE_SUCCESS);
					}
					return rankInfo;
				}
				@Override
				public void requestHttpFail() {//请求超时，或者失败
					mPagerState.changCurrentState(ResultState.STATE_FAIL);
				}
				
			};
			rankProtocol.getDataFromCache("&offset=0");// 从缓存中拿数据，拿不到就请求网络
		}

	}
	
}
