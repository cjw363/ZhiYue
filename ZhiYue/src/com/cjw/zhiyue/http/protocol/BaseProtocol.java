package com.cjw.zhiyue.http.protocol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.cjw.zhiyue.global.GlobalConstant;
import com.cjw.zhiyue.http.VolleyHelper;
import com.cjw.zhiyue.utils.IOUtils;
import com.cjw.zhiyue.utils.StringUtils;
import com.cjw.zhiyue.utils.UIUtils;

public abstract class BaseProtocol<T> {

	/**
	 * 从缓存获取数据
	 * 
	 * @param offset偏移量
	 */
	public void getDataFromCache(String offset) {
		String result = getCache(offset);

		// 如果缓存为空，就去请求网络
		if (StringUtils.isEmpty(result)) {
			getDataFromServer(offset);
		}

		if (result != null) {
			parseData(result);
		}
	}

	public abstract String getKey();// 获取网络链接关键词，子类必须获取

	public abstract String getParams();// 获取网络链接参数，子类必须实现

	public abstract T parseData(String result);// 解析数据

	public abstract void requestHttpFail();// 请求超时，或者失败

	/**
	 * 从网络获取数据,并缓存
	 * 
	 * @param offset
	 */
	public void getDataFromServer(final String offset) {
		String url = GlobalConstant.MUSIC_BASE_URL + getKey() + getParams() + offset;

		StringRequest stringRequest = new StringRequest(url, new Listener<String>() {
			@Override
			public void onResponse(String resultResponse) {
				if (resultResponse != null) {
					String result = StringUtils.decode(resultResponse);// 解码,成功
					setCache(offset, result);// 存储缓存

					parseData(result);
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				requestHttpFail();// 请求超时，或者失败
				error.printStackTrace();
			}

		});

		stringRequest.setRetryPolicy(new DefaultRetryPolicy(4000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数 
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));//4秒超时
		
		// 将StringRequest对象添加进RequestQueue请求队列中
		VolleyHelper.getRequestQueue().add(stringRequest);
	}

	/**
	 * 从网络获取数据
	 * 
	 * @param offset
	 */
	public void getNewDataFromServer(final String offset) {
		String url = GlobalConstant.MUSIC_BASE_URL + getKey() + getParams() + offset;

		StringRequest stringRequest = new StringRequest(url, new Listener<String>() {
			@Override
			public void onResponse(String resultResponse) {
				if (resultResponse != null) {
					String result = StringUtils.URLdecode(resultResponse);// 解码,成功
					
					parseData(result);
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				requestHttpFail();// 请求超时，或者失败
				error.printStackTrace();
			}

		});
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(4000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数 
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		
		// 将StringRequest对象添加进RequestQueue请求队列中
		VolleyHelper.getRequestQueue().add(stringRequest);
	}

	/**
	 * 写缓存，url为文件名，json为内容
	 */
	private void setCache(String offset, String result) {
		File cacheDir = UIUtils.getContext().getCacheDir();// 缓存路径
		File cacheFile = new File(cacheDir, getKey() + getParams() + offset);

		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(cacheFile);
			long deadLine = System.currentTimeMillis() + 24 * 60 * 60 * 1000;// 缓存数据的有效期1小时
			fileWriter.write(deadLine + "\n");
			fileWriter.write(result);
			fileWriter.flush();// 刷新，将数据全部刷入
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.close(fileWriter);
		}
	}

	/**
	 * 读缓存
	 */
	public String getCache(String offset) {
		File cacheDir = UIUtils.getContext().getCacheDir();// 缓存路径
		File cacheFile = new File(cacheDir, getKey() + getParams() + offset);

		if (cacheFile.exists()) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(cacheFile));

				String deadLine = reader.readLine();
				if (Long.parseLong(deadLine) > System.currentTimeMillis()) {
					// 缓存有效
					String line;
					StringBuffer sb = new StringBuffer();
					while ((line = reader.readLine()) != null) {
						sb.append(line);
					}

					return sb.toString();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				IOUtils.close(reader);
			}
		}
		return null;
	}

}
