package com.cjw.zhiyue.ui.fragment.musicFra;

import java.util.concurrent.ConcurrentHashMap;

import com.cjw.zhiyue.ui.fragment.BaseFragment;



/**
 *生产fragment
 */
public class FragmentFactory {

	private static ConcurrentHashMap<Integer,BaseFragment> fraMap=new ConcurrentHashMap<Integer,BaseFragment>();
	public static BaseFragment createFragment(int position){
		BaseFragment fragment = fraMap.get(position);
		if(fragment==null){
			switch (position) {
			case 0:
				fragment=new RecommendFragment();
				break;
			case 1:
				fragment=new RankFragment();
				break;
			case 2:
				fragment=new HomeFragment();
				break;
			case 3:
				fragment=new SearchFragment();
				break;
			case 4:
				fragment=new DownloadFragment();
				break;
			}
			fraMap.put(position, fragment);
		}
		return fragment;
	}
}
