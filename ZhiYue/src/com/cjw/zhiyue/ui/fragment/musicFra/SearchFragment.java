package com.cjw.zhiyue.ui.fragment.musicFra;

import java.util.List;

import org.xutils.x;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import com.cjw.zhiyue.R;
import com.cjw.zhiyue.db.Dao.HistoryListDao;
import com.cjw.zhiyue.db.Dao.PlayListDao;
import com.cjw.zhiyue.domain.PlaySongInfo;
import com.cjw.zhiyue.domain.SearchInfo;
import com.cjw.zhiyue.domain.SearchInfo.SearchSongInfo;
import com.cjw.zhiyue.global.GlobalConstant;
import com.cjw.zhiyue.http.protocol.PlaySongProtocol;
import com.cjw.zhiyue.http.protocol.SearchProtocol;
import com.cjw.zhiyue.ui.adapter.MyBaseAdapter.OnItemClickListener;
import com.cjw.zhiyue.ui.adapter.music.SearchHistoryAdapter;
import com.cjw.zhiyue.ui.adapter.music.SearchSuccessAdapter;
import com.cjw.zhiyue.ui.fragment.BaseFragment;
import com.cjw.zhiyue.ui.view.PagerState;
import com.cjw.zhiyue.ui.view.PagerState.ResultState;
import com.cjw.zhiyue.ui.view.libs.FlowLayout;
import com.cjw.zhiyue.utils.StringUtils;
import com.cjw.zhiyue.utils.UIUtils;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SearchFragment extends BaseFragment {

	protected static final int GET_SUCCESS = 0;

	@ViewInject(R.id.ll_search_content)
	private LinearLayout ll_search_content;
	@ViewInject(R.id.et_search)
	private EditText et_search;
	@ViewInject(R.id.ib_search)
	private ImageButton ib_search;

	private ChildPagerState mChildPagerState;

	private SearchInfo searchInfo;

	@Override
	public View onCreateSuccessView() {
		View view = View.inflate(getActivity(), R.layout.layout_fra_search, null);
		x.view().inject(this, view);

		setEditListener();// 设置edit文本监听
		addChildPagerState();// 增加搜索下面子布局pagerstate

		return view;
	}

	@Override
	public void onLoadHttp() {
		mPagerState.changCurrentState(ResultState.STATE_SUCCESS);
	}

	/**
	 * 设置edit文本监听
	 */
	private void setEditListener() {
		TextWatcher textWatcher = new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (StringUtils.isEmpty(s.toString())) {
					mChildPagerState.changCurrentState(ResultState.STATE_UNDO);// 回到pager默认布局

					mChildPagerState.updateHistoryAdapter();// 更新历史搜索的内容
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		};
		et_search.addTextChangedListener(textWatcher);
	}

	/**
	 * 增加搜索下面子布局pagerstate
	 */
	private void addChildPagerState() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		mChildPagerState = new ChildPagerState(UIUtils.getContext());
		ll_search_content.addView(mChildPagerState, params);

		mChildPagerState.loadData();// 走子pager的onloadhttp方法
	}

	@Event(value = R.id.ib_search)
	private void onSearchClick(View v) {
		String searchKey = et_search.getText().toString();
		if (StringUtils.isEmpty(searchKey)) {
			return;
		}
		requestSearchProtocol(StringUtils.encode(searchKey));// 编码 根据关键词进行网络搜索

		HistoryListDao.getHistoryListDao().insert(searchKey);// 添加到历史搜索表
	}

	/**
	 * 根据关键词进行网络搜索
	 */
	private void requestSearchProtocol(String searchKey) {
		SearchProtocol searchProtocol = new SearchProtocol() {

			@Override
			public SearchInfo parseData(String result) {
				searchInfo = super.parseData(result);

				if (searchInfo != null)
					switch (searchInfo.error_code) {
					case 22001:// 表示失败，可能是关键词有误或者服务器未响应
						mChildPagerState.changCurrentState(ResultState.STATE_UNDO);// 回到pager默认布局

						mChildPagerState.updateHistoryAdapter();// 更新历史搜索的内容

						Toast.makeText(UIUtils.getContext(), "无结果，请重试", Toast.LENGTH_SHORT).show();
						break;
					case 22000:// 成功
						mChildPagerState.changCurrentState(ResultState.STATE_SUCCESS);// 更新子pager的布局

						mChildPagerState.updateSuccessAdapter();// 更新每一搜索的内容
						break;
					}

				return searchInfo;
			}

			@Override
			public void requestHttpFail() {
				mChildPagerState.changCurrentState(ResultState.STATE_FAIL);
			}
		};
		searchProtocol.getNewDataFromServer(searchKey);
		mChildPagerState.changCurrentState(ResultState.STATE_LOADING);// 更新子pager的布局正在加载
	}

	/**
	 * 请求网络，拿到播放地址
	 */
	protected void requestSongProtocol(SearchSongInfo songInfo) {

		final Intent intent = new Intent(GlobalConstant.REQUEST_PLAY_SONG_ACTION);

		PlaySongProtocol playSongProtocol = new PlaySongProtocol() {
			@Override
			public PlaySongInfo parseData(String result) {
				// 重写解析方法，网络请求成功后，去开始服务播放音乐
				PlaySongInfo playSongInfo = super.parseData(result);

				// 发送广播
				intent.putExtra("result", GET_SUCCESS);
				intent.putExtra("playSongInfo", playSongInfo);
				UIUtils.getContext().sendBroadcast(intent);

				return playSongInfo;
			}

			@Override
			public void requestHttpFail() {
				// 请求播放地址失败
				Toast.makeText(UIUtils.getContext(), "请检查网络连接", Toast.LENGTH_SHORT).show();
			}

		};
		playSongProtocol.getDataFromCache("&songid=" + songInfo.songid);
	}

	private class ChildPagerState extends PagerState {

		@ViewInject(R.id.flowlayout)
		private FlowLayout flowlayout;
		@ViewInject(R.id.history_recyclerview)
		private RecyclerView history_recyclerview;

		private RecyclerView success_recyclerview;

		private SearchSuccessAdapter mSuccessAdapter;

		public SearchHistoryAdapter mHistoryAdapter;

		public ChildPagerState(Context context) {
			super(context);
		}

		@Override
		public void onLoadHttp() {
			UIUtils.runOnUIThread(new Runnable() {
				@Override
				public void run() {
					View view = UIUtils.inflate(R.layout.pager_search_state_undo);
					x.view().inject(ChildPagerState.this, view);

					ChildPagerState.this.mPagerUndo = view;
					ChildPagerState.this.addView(ChildPagerState.this.mPagerUndo);// 主线程更新ui
					ChildPagerState.this.changCurrentState(ResultState.STATE_UNDO);

					initFlowLayout();// 设置自动排行FlowLayout的参数
					initHistoryRecycler();// 初始化,设置垂直布局，填充数据
				}
			});
		}

		@Override
		public View onCreateSuccessView() {
			View view = UIUtils.inflate(R.layout.pager_search_state_success);
			success_recyclerview = (RecyclerView) view.findViewById(R.id.success_recyclerview);

			initSuccessRecycler();// 初始化,设置垂直布局，填充数据

			return view;
		}

		private void initFlowLayout() {
			if (flowlayout != null) {
				int dp_10 = UIUtils.dip2px(10);
				flowlayout.setPadding(dp_10, 0, dp_10, dp_10);
				flowlayout.setHorizontalSpacing(dp_10);
				flowlayout.setVerticalSpacing(dp_10);

				String[] recomNames = UIUtils.getStringArray(R.array.search_recom_names);
				for (int i = 0; i < recomNames.length; i++) {
					final TextView textView = new TextView(UIUtils.getContext());
					textView.setText(recomNames[i]);
					textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					textView.setTextColor(0xaa000000);
					int dp_5 = UIUtils.dip2px(5);
					textView.setPadding(dp_5, dp_5, dp_5, dp_5);
					textView.setGravity(Gravity.CENTER);
					textView.setBackgroundResource(R.drawable.bg_flow_txt_selector);
					textView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							String searchKey = textView.getText().toString();
							et_search.setText(searchKey);// edit显示关键词
							requestSearchProtocol(StringUtils.encode(searchKey));// 根据关键词进行网络搜索

							HistoryListDao.getHistoryListDao().insert(searchKey);// 添加到历史搜索表
						}
					});

					flowlayout.addView(textView);
				}
			}
		}

		/**
		 * 更新每一搜索的内容
		 */
		private void updateSuccessAdapter() {
			UIUtils.runOnUIThread(new Runnable() {
				@Override
				public void run() {
					mSuccessAdapter.data = searchInfo.song;
					mSuccessAdapter.notifyDataSetChanged();
				}
				
			});
		}

		/**
		 * /更新历史搜索的内容
		 */
		private void updateHistoryAdapter() {
			UIUtils.runOnUIThread(new Runnable() {
				@Override
				public void run() {
					List<String> historyList = HistoryListDao.getHistoryListDao().query();

					mHistoryAdapter.data = historyList;
					mHistoryAdapter.notifyDataSetChanged();

				}
			});
		}

		/**
		 * 初始化SuccessRecycler
		 */
		private void initSuccessRecycler() {
			LinearLayoutManager layoutManager = new LinearLayoutManager(UIUtils.getContext());
			layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
			success_recyclerview.setLayoutManager(layoutManager);
			success_recyclerview.setItemAnimator(new DefaultItemAnimator()); // 设置增加或删除条目的动画

			mSuccessAdapter = new SearchSuccessAdapter(searchInfo.song,success_recyclerview);
			success_recyclerview.setAdapter(mSuccessAdapter);

			mSuccessAdapter.setOnItemClickListener(new OnItemClickListener<SearchInfo.SearchSongInfo>() {
				@Override
				public void onItemClick(View view, int position, SearchSongInfo info) {
					// 请求网络，拿到播放地址
					requestSongProtocol(info);
					// 存储歌曲，显示在歌曲播放列表
					PlayListDao.getPlayListDao().insert(info.songid, info.songname, info.artistname);
				}
			});
		}

		/**
		 * 初始化HistoryRecycler
		 */
		private void initHistoryRecycler() {
			LinearLayoutManager layoutManager = new LinearLayoutManager(UIUtils.getContext());
			layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
			history_recyclerview.setLayoutManager(layoutManager);
			history_recyclerview.setItemAnimator(new DefaultItemAnimator()); // 设置增加或删除条目的动画

			List<String> historyList = HistoryListDao.getHistoryListDao().query();// 获取历史搜索列表

			mHistoryAdapter = new SearchHistoryAdapter(historyList);
			history_recyclerview.setAdapter(mHistoryAdapter);

			mHistoryAdapter.setOnItemClickListener(new OnItemClickListener<String>() {
				@Override
				public void onItemClick(View view, int position, String searchKey) {
					et_search.setText(searchKey);// edit显示关键词
					requestSearchProtocol(StringUtils.encode(searchKey));// 根据关键词进行网络搜索
				}
			});
		}
	}

}
