package com.cjw.zhiyue.ui.fragment.tbFra;

import java.util.concurrent.ConcurrentHashMap;

import com.cjw.zhiyue.ui.fragment.BaseFragment;

/**
 *生产fragment
 */
public class TBFragmentFactory {

	private static ConcurrentHashMap<Integer,BaseFragment> tbFraMap=new ConcurrentHashMap<Integer,BaseFragment>();
	public static BaseFragment createFragment(int position){
		BaseFragment fragment = tbFraMap.get(position);
		if(fragment==null){
			switch (position) {
			case 0:
				fragment =new TBMusicFragment();
				break;
			case 1:
				fragment =new TBZhihuFragment();
				break;
			case 2:
				fragment =new TBJoyFragment();
				break;
			}
			tbFraMap.put(position, fragment);
		}
		return fragment;
	}
}
