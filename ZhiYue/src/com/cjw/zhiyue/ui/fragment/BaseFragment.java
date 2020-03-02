package com.cjw.zhiyue.ui.fragment;

import com.cjw.zhiyue.ui.view.PagerState;
import com.cjw.zhiyue.utils.UIUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {

	public PagerState mPagerState;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mPagerState = new PagerState(UIUtils.getContext()) {

			@Override
			public void onLoadHttp() {
				// 子类实现加载网络数据
				BaseFragment.this.onLoadHttp();
			}

			@Override
			public View onCreateSuccessView() {
				// 加载成功的布局由子类实现
				return BaseFragment.this.onCreateSuccessView();
			}

		};
		return mPagerState;
	}

	public abstract View onCreateSuccessView();// 加载成功的布局由子类实

	public abstract void onLoadHttp();// 子类实现加载网络数据

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {// 当fragment处于可见状态
			if (mPagerState != null) {
				// 调用PagerState的loadData加载数据,间接调用加载网络数据
				mPagerState.loadData();
				Log.d("loadData", getClass().getName() + "++可视化++>" + "loadData");
			}
		}
	}

	private boolean isFirstCreate=false;
	
	public boolean getIsFirstCreate(){
		return isFirstCreate;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if (getUserVisibleHint() && !getIsFirstCreate()) {
			mPagerState.loadData();
			isFirstCreate=true;
			Log.d("loadData", getClass().getName() + "++ActivityCreate++>" + "loadData");
		}
	}
}
