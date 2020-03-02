package com.cjw.zhiyue.ui.holder.music;


import org.xutils.x;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import com.cjw.zhiyue.R;
import com.cjw.zhiyue.domain.DownloadInfo;
import com.cjw.zhiyue.http.manager.DownloadManager;
import com.cjw.zhiyue.http.manager.DownloadManager.DownloadObserver;
import com.cjw.zhiyue.ui.adapter.MyBaseAdapter.OnItemClickListener;
import com.cjw.zhiyue.ui.holder.BaseHolder;
import com.cjw.zhiyue.utils.SPUtils;
import com.cjw.zhiyue.utils.UIUtils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DownloadIngHolder extends BaseHolder<DownloadInfo> implements OnClickListener, DownloadObserver {

	@ViewInject(R.id.tv_download_name)
	private TextView tv_download_name;
	@ViewInject(R.id.tv_click2pause)
	private TextView tv_click2pause;
	@ViewInject(R.id.progressbar)
	private ProgressBar progressbar;

	private OnItemClickListener<DownloadInfo> itemClickListener;
	private DownloadInfo info;
	private DownloadManager mDM;
	private AlertDialog mDialog;

	public DownloadIngHolder(View itemView, OnItemClickListener<DownloadInfo> itemClickListener) {
		super(itemView);
		x.view().inject(this, itemView);

		mDM = DownloadManager.getInstance();
		mDM.registObserver(this);// 注册下载观察者

		this.itemClickListener = itemClickListener;
		itemView.setOnClickListener(this);
	}

	@Override
	public void refreshData(DownloadInfo info) {
		this.info = info;
		tv_download_name.setText(info.downloadName);
		progressbar.setMax((int) (info.file_size / 1024));

		if(!mDM.isHasDownloadTask(info.song_id)){//没有下载进程
			info.currentState=DownloadManager.STATE_PAUSE;//手动修改
			SPUtils.saveDownloadInfo(info.song_id, info);// 本地存储downloadInfo的信息
		}
		// 首次更新控件状态
		refreshUI(info.currentState, info.currentPos);
	}

	@Override
	public void onClick(View v) {
		if (itemClickListener != null)
			itemClickListener.onItemClick(v, getPosition(), getData());
	}

	@Event(R.id.ib_cancle_download)
	private void onCancleDownlClick(View v) {
		showCancleDialog();// 弹出确定是否取消下载的dialog
	}

	/**
	 * 更新状态，和进度
	 */
	protected void refreshUI(int mCurrentState, long mProgress) {
		switch (mCurrentState) {
		case DownloadManager.STATE_UNDO:
			break;
		case DownloadManager.STATE_WAITTING:
			progressbar.setVisibility(View.GONE);
			tv_click2pause.setVisibility(View.VISIBLE);
			tv_click2pause.setText("等待下载中，请稍等");
			break;
		case DownloadManager.STATE_DOWNLOADING:
			info.currentState = DownloadManager.STATE_DOWNLOADING;

			progressbar.setVisibility(View.VISIBLE);
			tv_click2pause.setVisibility(View.GONE);
			progressbar.setProgress((int) (mProgress / 1024));
			break;
		case DownloadManager.STATE_PAUSE:
			info.currentState = DownloadManager.STATE_PAUSE;

			progressbar.setVisibility(View.GONE);
			tv_click2pause.setVisibility(View.VISIBLE);
			tv_click2pause.setText("点击继续下载");
			break;
		case DownloadManager.STATE_FAIL:
			progressbar.setVisibility(View.GONE);
			tv_click2pause.setVisibility(View.VISIBLE);
			tv_click2pause.setText("下载失败，点击重新下载");
			break;
		case DownloadManager.STATE_SUCCESS:
			break;
		}
	}

	/**
	 * 弹出确定是否取消下载的dialog
	 */
	private void showCancleDialog() {
		AlertDialog.Builder builder = new Builder(UIUtils.getActivity());
		mDialog = builder.create();

		Window window = mDialog.getWindow();
		//window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);// 类型
		mDialog.show();

		View view = UIUtils.inflate(R.layout.dialog_download_cancle);
		x.view().inject(this, view);

		window.setContentView(view);
		LayoutParams params = window.getAttributes();
		// params.format = PixelFormat.TRANSLUCENT;
		params.width = UIUtils.getWidthPixels(0.9); // 窗口的宽
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(params);
	}

	@Event(R.id.tv_sure)
	private void onSureClick(View v) {
		mDM.cancel(info);
		mDialog.dismiss();
	}
	@Event(R.id.tv_cancle)
	private void onCancleClick(View v) {
		mDialog.dismiss();
	}

	/**
	 * 主线程更新状态，和进度
	 */
	public void runOnMainRefreshUI(final DownloadInfo downloadInfo) {
		UIUtils.runOnUIThread(new Runnable() {// 这里必须传入DownloadInfo对象，因为final修饰就不能被修改，但里面的变量可以，这样可以保证里面的变量是最新的
			@Override
			public void run() {
				refreshUI(downloadInfo.currentState, downloadInfo.currentPos);
			}
		});
	}

	@Override
	public void onDownloadStateChanged(DownloadInfo downloadInfo) {
		if ((info.song_id).equals(downloadInfo.song_id)) {// 判断下载对象是否是当前对象
			runOnMainRefreshUI(downloadInfo);
		}
	}

	@Override
	public void onDownloadProgressChanged(DownloadInfo downloadInfo) {
		if ((info.song_id).equals(downloadInfo.song_id)) {
			runOnMainRefreshUI(downloadInfo);
		}
	}

}
