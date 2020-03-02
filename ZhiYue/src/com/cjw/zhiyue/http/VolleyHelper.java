package com.cjw.zhiyue.http;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.cjw.zhiyue.R;
import com.cjw.zhiyue.utils.UIUtils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

public class VolleyHelper {

	private static RequestQueue mRequestQueue;

	// RequestQueue是一个请求队列对象，它可以缓存所有的HTTP请求，然后按照一定的算法并发地发出这些请求
	public synchronized static RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(UIUtils.getContext());
		}
		
		return mRequestQueue;
	}

	/**
	 * 图片加载
	 */
	public static void imageLoader(String imageUrl, ImageView imageView) {
		// imageView是一个ImageView实例
		// ImageLoader.getImageListener的第二个参数是默认的图片resource id
		// 第三个参数是请求失败时候的资源id，可以指定为0
		ImageListener imageListener = ImageLoader.getImageListener(imageView, R.drawable.img_minibar_default, R.drawable.pcsync_failure);
		getImageLoader().get(imageUrl, imageListener);
	}

	private static int maxMomory = 10 * 1024 * 1024;// 最大内存超过10M，启动内存回收或者使用
													// Runtime.getRuntime().maxMemory()/4代替;
	private static LruCache<String, Bitmap> mLruCache = null;
	
	private static ImageLoader imageLoader;

	/**
	 * 获取ImageLoader对象
	 */
	private static ImageLoader getImageLoader() {
		if (mLruCache == null) {
			mLruCache = new LruCache<String, Bitmap>(maxMomory) {
				protected int sizeOf(String key, Bitmap value) {
					// 这里应该返回的是一个图片的大小
					return value.getRowBytes() * value.getHeight();// 或者value.getByteCount();
				};
			};
		}
		if(imageLoader==null){
			imageLoader = new ImageLoader(getRequestQueue(), new ImageCache() {
				@Override
				public void putBitmap(String url, Bitmap bitmap) {
					mLruCache.put(url, bitmap);
				}
				
				@Override
				public Bitmap getBitmap(String url) {
					return mLruCache.get(url);
				}
			});
		}
		return imageLoader;
	}

}
