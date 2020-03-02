package com.cjw.zhiyue.ui.fragment.tbFra;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import com.cjw.zhiyue.R;
import com.cjw.zhiyue.ui.fragment.BaseFragment;
import com.cjw.zhiyue.ui.fragment.musicFra.FragmentFactory;
import com.cjw.zhiyue.ui.view.NoScrollViewPager;
import com.cjw.zhiyue.ui.view.PagerState.ResultState;
import com.cjw.zhiyue.ui.view.libs.PagerTab;
import com.cjw.zhiyue.utils.UIUtils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;

public class TBMusicFragment extends BaseFragment {

	@ViewInject(R.id.pagertab)
	private PagerTab mPagerTab;
	@ViewInject(R.id.viewpager)
	private NoScrollViewPager mViewPager;

	@Override
	public View onCreateSuccessView() {
		// 因为pagertab构造方法需要传入的是activity对象,不能传入getapplicationcontext
		View view = View.inflate(getActivity(), R.layout.layout_fra_music, null);
		x.view().inject(this, view);

		// 只要有ViewPager 在界面初始化的时候就必须给ViewPager 设置adapter,不论你当前是否用到。并且一个ViewPager
		// 最好只声明一次，设置一次adapter，不然可能会有的时候界面显示不出来；
		initPager();// 初始化pagertab和viewpager

		return view;
	}

	@Override
	public void onLoadHttp() {
		mPagerState.changCurrentState(ResultState.STATE_SUCCESS);
	}

	/**
	 * 初始化pagertab和viewpager
	 */
	private void initPager() {
		// 嵌套fragment时，管理子fragment,用getChildFragmentManager
		mViewPager.setAdapter(new InnerFraPagerAdapter(getChildFragmentManager()));
		//mViewPager.setOffscreenPageLimit(0);// 缓存当前界面每一侧的界面数
		mPagerTab.setViewPager(mViewPager);
	}

	private class InnerFraPagerAdapter extends FragmentStatePagerAdapter {

		private String[] tabNames;

		public InnerFraPagerAdapter(FragmentManager fm) {
			super(fm);
			tabNames = UIUtils.getStringArray(R.array.tab_names);
		}

		@Override
		public Fragment getItem(int position) {
			return FragmentFactory.createFragment(position);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return tabNames[position];
		}

		@Override
		public int getCount() {
			return tabNames.length;
		}

	}

}
