package com.cjw.zhiyue.utils;

import org.xutils.image.ImageOptions;

import android.widget.ImageView;

public class BitmapHelper {
	private static ImageOptions imageOptions;

	// 获取xutils的
	public static ImageOptions getImageOptions() {
		if (imageOptions == null) {
			imageOptions = new ImageOptions.Builder()
					.setImageScaleType(ImageView.ScaleType.FIT_XY)
					.setFadeIn(true) // 淡入效果
					.build();
		}
		return imageOptions;
	}
}
