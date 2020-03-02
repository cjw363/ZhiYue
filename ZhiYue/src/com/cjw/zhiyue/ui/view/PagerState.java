package com.cjw.zhiyue.ui.view;

import com.cjw.zhiyue.R;
import com.cjw.zhiyue.utils.UIUtils;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public abstract class PagerState extends FrameLayout {

	public static final int PAGER_STATE_UNDO = 0;// 未加载
	public static final int PAGER_STATE_LOADING = 1;// 正在加载
	public static final int PAGER_STATE_FAIL = 2;// 加载失败
	public static final int PAGER_STATE_EMPTY = 3;// 加载为空
	public static final int PAGER_STATE_SUCCESS = 4;// 加载完成

	public int mCurrentState = PAGER_STATE_LOADING;

	public View mPagerUndo;
	private View mPagerEmpty;
	private View mPagerLoading;
	private View mPagerFail;
	private View mPagerSuccess;

	public PagerState(Context context) {
		this(context, null);
	}

	public PagerState(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PagerState(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView();// 初始化布局
	}

	// 加载网络数据
	public abstract void onLoadHttp();

	// 加载成功的布局
	public abstract View onCreateSuccessView();

	/**
	 * 初始化布局
	 */
	private void initView() {
		if (mPagerEmpty == null) {// 加载为空的布局
			mPagerEmpty = UIUtils.inflate(R.layout.pager_state_empty);
			addView(mPagerEmpty);
		}
		if (mPagerLoading == null) {// 正在加载的布局
			mPagerLoading = UIUtils.inflate(R.layout.pager_state_loading);
			addView(mPagerLoading);
		}
		if (mPagerFail == null) {// 加载失败的布局
			mPagerFail = UIUtils.inflate(R.layout.pager_state_fail);
			addView(mPagerFail);
		}
		showCurrentPager();
	}

	/**
	 * 根据当前状态显示不同布局
	 */
	private void showCurrentPager() {
		if (mPagerUndo != null)
			mPagerUndo.setVisibility(mCurrentState == PAGER_STATE_UNDO ? View.VISIBLE : View.GONE);

		mPagerLoading.setVisibility(mCurrentState == PAGER_STATE_LOADING ? View.VISIBLE : View.GONE);

		mPagerFail.setVisibility(mCurrentState == PAGER_STATE_FAIL ? View.VISIBLE : View.GONE);

		mPagerEmpty.setVisibility(mCurrentState == PAGER_STATE_EMPTY ? View.VISIBLE : View.GONE);

		if (mPagerSuccess == null && mCurrentState == PAGER_STATE_SUCCESS) {
			mPagerSuccess = onCreateSuccessView();
			if (mPagerSuccess != null)
				addView(mPagerSuccess);
		}
		if (mPagerSuccess != null)
			mPagerSuccess.setVisibility(mCurrentState == PAGER_STATE_SUCCESS ? View.VISIBLE : View.GONE);
	}

	/**
	 * 加载数据,由子类去调用
	 */
	public void loadData() {
		new Thread(new Runnable() {
			public void run() {
				SystemClock.sleep(100);//让当前线程睡眠，防止子线程过快
				onLoadHttp();// 加载网络数据
			}
		}).start();
	}

	/**
	 * 枚举 加载网络数据后返回结果状态
	 */
	public enum ResultState {
		STATE_UNDO(PAGER_STATE_UNDO), STATE_LOADING(PAGER_STATE_LOADING), STATE_SUCCESS(
				PAGER_STATE_SUCCESS), STATE_FAIL(PAGER_STATE_FAIL), STATE_EMPTY(PAGER_STATE_EMPTY);

		private int state;

		private ResultState(int state) {
			this.state = state;
		}

		public int getState() {
			return state;
		}
	}

	/**
	 * 由fragment子类调用，显示加载数据后的布局
	 */
	public void changCurrentState(final ResultState resultState) {
		// 更新加载网络后的布局，跑在主线程
		UIUtils.runOnUIThread(new Runnable() {
			@Override
			public void run() {
				if (resultState != null) {
					int state = resultState.getState();// 获取加载网络结果状态
					mCurrentState = state;
					showCurrentPager();// 根据最新状态刷新界面
				}
			}
		});
	}
}
