package com.cjw.zhiyue.ui.activity;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import com.cjw.zhiyue.R;
import com.cjw.zhiyue.global.ZhiYueApplication;
import com.cjw.zhiyue.ui.fragment.tbFra.TBFragmentFactory;
import com.cjw.zhiyue.ui.view.NoScrollViewPager;
import com.cjw.zhiyue.utils.UIUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainActivity extends BaseActivity {

	@ViewInject(R.id.toolbar)
	private Toolbar mToolbar;
	@ViewInject(R.id.drawer)
	private DrawerLayout mDrawer;
	@ViewInject(R.id.radiogroup)
	private RadioGroup mRadiogroup;
	@ViewInject(R.id.rb_music)
	private RadioButton rbMusic;
	@ViewInject(R.id.viewpager)
	private NoScrollViewPager mViewpager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		x.view().inject(this);

		initToolBar();// 初始化toolbar
		initPager();// 初始化radiobutton点击事件和viewpager
		
		ZhiYueApplication.putActivity(this);//往栈添加当前activity
	}

	private void initToolBar() {
		mToolbar.inflateMenu(R.menu.main);// 设置右上角的填充菜单
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, 0, 0);// 绑定toolbar跟drawerlayout
		toggle.syncState();// .设置菜单切换同步,显示home键
		mDrawer.addDrawerListener(toggle);
	}

	private void initPager() {
		mViewpager.setAdapter(new InnerFraPagerAdapter(getSupportFragmentManager()));
		//mViewpager.setOffscreenPageLimit(0);//缓存当前界面每一侧的界面数
		
		//设置RadioGroup和viewpager同步
		mRadiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_music:
					mViewpager.setCurrentItem(0, true);
					break;
				case R.id.rb_zhihu:
					mViewpager.setCurrentItem(1, true);
					break;
				case R.id.rb_joy:
					mViewpager.setCurrentItem(2, true);
					break;
				}
			}
		});
		
		rbMusic.setChecked(true);//默认音乐选项被选中
	}

	private class InnerFraPagerAdapter extends FragmentStatePagerAdapter {

		private String[] tbNames;// 音乐，知乎，娱乐

		public InnerFraPagerAdapter(FragmentManager fm) {
			super(fm);
			tbNames = UIUtils.getStringArray(R.array.toolbar_names);
		}

		@Override
		public Fragment getItem(int position) {
			return TBFragmentFactory.createFragment(position);
		}

		@Override
		public int getCount() {
			return tbNames.length;
		}
		
//		@Override
//		public void destroyItem(ViewGroup container, int position, Object object) {
//			//super.destroyItem(container, position, object);//阻止fragment的销毁
//		}

	}
	
}
