package com.cjw.zhiyue.ui.holder.music;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import com.cjw.zhiyue.R;
import com.cjw.zhiyue.domain.RankInfo.SongInfo;
import com.cjw.zhiyue.http.VolleyHelper;
import com.cjw.zhiyue.ui.adapter.MyBaseAdapter.OnItemClickListener;
import com.cjw.zhiyue.ui.holder.BaseHolder;

import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class RankSongHolder extends BaseHolder<SongInfo> implements OnClickListener{

	@ViewInject(R.id.iv_song_img)
	private ImageView iv_song_img;
	@ViewInject(R.id.tv_song_name)
	private TextView tv_song_name;
	@ViewInject(R.id.tv_songer)
	private TextView tv_songer;
	@ViewInject(R.id.rb_star)
	private RatingBar rb_star;
	@ViewInject(R.id.tv_star)
	private TextView tv_star;
	
	private OnItemClickListener<SongInfo> itemClickListener;
	
	private SongInfo songInfo;
	
	public RankSongHolder(View itemView,OnItemClickListener<SongInfo> itemClickListener) {
		super(itemView);
		x.view().inject(this, itemView);
		
		this.itemClickListener=itemClickListener;
		itemView.setOnClickListener(this);//设置点击事件
	}

	@Override
	public void refreshData(SongInfo songInfo) {
		this.songInfo=songInfo;
		
		VolleyHelper.imageLoader(songInfo.pic_big, iv_song_img);
		
		tv_song_name.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
		tv_song_name.setText(songInfo.title);
		tv_songer.setText(songInfo.artist_name);
		
		double rating=songInfo.hot*5/500000;
		rb_star.setRating((float) rating);
		tv_star.setText(rating+"");
	}

	@Override
	public void onClick(View v) {
		if(itemClickListener!=null){
			itemClickListener.onItemClick(v, getPosition(),songInfo);
		}
	}

}
