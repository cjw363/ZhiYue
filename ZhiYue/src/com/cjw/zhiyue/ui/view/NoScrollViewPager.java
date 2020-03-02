package com.cjw.zhiyue.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NoScrollViewPager extends ViewPager {

	public NoScrollViewPager(Context context) {
		super(context);
	}

	public NoScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		return false;//不拦截
	}
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return false;//禁用viewpager的滑动,什么都不做
	}
}
