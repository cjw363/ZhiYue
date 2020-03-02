package com.cjw.zhiyue.ui.fragment.musicFra;

import java.util.ArrayList;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import com.cjw.zhiyue.R;
import com.cjw.zhiyue.domain.DownloadInfo;
import com.cjw.zhiyue.http.manager.DownloadManager;
import com.cjw.zhiyue.ui.adapter.music.DownloadIngAdapter;
import com.cjw.zhiyue.ui.adapter.music.DownloadedAdapter;
import com.cjw.zhiyue.ui.fragment.BaseFragment;
import com.cjw.zhiyue.ui.view.NoScrollViewPager;
import com.cjw.zhiyue.ui.view.PagerState.ResultState;
import com.cjw.zhiyue.ui.view.libs.PagerTab;
import com.cjw.zhiyue.utils.SPUtils;
import com.cjw.zhiyue.utils.UIUtils;

import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class DownloadFragment extends BaseFragment {

	@ViewInject(R.id.pagertab)
	private PagerTab mPagerTab;
	@ViewInject(R.id.viewpager)
	private NoScrollViewPager mViewPager;

	@Override
	public View onCreateSuccessView() {
		View view = View.inflate(getActivity(), R.layout.layout_fra_download, null);
		x.view().inject(this, view);

		initPager();// 初始化pagertab和viewpager

		return view;
	}

	@Override
	public void onLoadHttp() {
		initDownloadList();// 初始化分类下载列表
		mPagerState.changCurrentState(ResultState.STATE_SUCCESS);
	}

	/**
	 * 初始化分类下载列表
	 */
	private void initDownloadList() {
		downloadInfoList = SPUtils.getDownloadInfoList();
		if(downloadingList==null)
		downloadingList = new ArrayList<DownloadInfo>();
		if(downloadedList==null)
		downloadedList = new ArrayList<DownloadInfo>();

		downloadingList.clear();//清除之前的数据
		downloadedList.clear();
		
		if (downloadInfoList != null)
			for (DownloadInfo d : downloadInfoList) {
				if (d.currentState == DownloadManager.STATE_DOWNLOADING || d.currentState == DownloadManager.STATE_FAIL
						|| d.currentState == DownloadManager.STATE_PAUSE
						|| d.currentState == DownloadManager.STATE_WAITTING) {

					downloadingList.add(d);
				} else if (d.currentState == DownloadManager.STATE_SUCCESS) {
					downloadedList.add(d);
				}
			}
	}

	/**
	 * 初始化pagertab和viewpager
	 */
	private void initPager() {
		mViewPager.setAdapter(new InnerPagerAdapter());

		mPagerTab.setViewPager(mViewPager);
	}

	private String[] tabNames = new String[] { "正在下载", "已下载" };
	private ArrayList<DownloadInfo> downloadInfoList;
	private ArrayList<DownloadInfo> downloadingList;
	private ArrayList<DownloadInfo> downloadedList;
	private DownloadIngAdapter mDownloadIngAdapter;
	private DownloadedAdapter mDownloadedAdapter;

	private class InnerPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return tabNames.length;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return tabNames[position];
		}

		@Override
		public RecyclerView instantiateItem(ViewGroup container, int position) {

			if (position == 0) {// 下载中
				RecyclerView downlingRecyclView = new RecyclerView(UIUtils.getContext());
				setRecyclview(downlingRecyclView);
				mDownloadIngAdapter = new DownloadIngAdapter(downloadingList);
				downlingRecyclView.setAdapter(mDownloadIngAdapter);
				
				container.addView(downlingRecyclView);
				return downlingRecyclView;
				
			} else if (position == 1) {
				RecyclerView downledRecyclView = new RecyclerView(UIUtils.getContext());
				setRecyclview(downledRecyclView);
				mDownloadedAdapter = new DownloadedAdapter(downloadedList);
				downledRecyclView.setAdapter(mDownloadedAdapter);
				
				container.addView(downledRecyclView);
				return downledRecyclView;
			}

			return null;
		}

		private void setRecyclview(RecyclerView recyclView) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			recyclView.setLayoutParams(params);

			LinearLayoutManager layoutManager = new LinearLayoutManager(UIUtils.getContext());
			layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
			recyclView.setLayoutManager(layoutManager);
		}
	}

}
