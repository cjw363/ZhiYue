package com.cjw.zhiyue.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;

import com.cjw.zhiyue.domain.PlaySongInfo;

import android.os.Environment;

public abstract class LrcProtocol {
	private static final String ZHIYUE = "com.cjw.zhiyue";
	private static final String LRC = "songLrc";
	
	/**
	 * 获取歌词
	 */
	public void getLrcFromServer(PlaySongInfo playSongInfo){
		File file = new File(getFilePath()+playSongInfo.songinfo.title+".lrc");
		if(file.exists()){
			String parseLrc = parseLrc(file);
			
			parseData(parseLrc);
		}else{
			RequestParams params = new RequestParams(playSongInfo.songinfo.lrclink);// 设置下载地址
			params.setSaveFilePath(file.getPath());// 设置文件下载后的位置
			x.http().get(params,new CommonCallback<File>(){
				@Override
				public void onCancelled(CancelledException arg0) {}
				@Override
				public void onError(Throwable arg0, boolean arg1) {}
				@Override
				public void onFinished() {}
				@Override
				public void onSuccess(File file) {
					String parseLrc = parseLrc(file);
					
					parseData(parseLrc);
				}
				
			});
		}
		
	}
	
	public abstract void parseData(String parseLrc);// 解析数据

	
	/**
	 * @param file
	 * @return
	 */
	private String parseLrc(File file){
		BufferedReader reader = null;
		String line = "";
		String result = "";
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			reader = new BufferedReader(new InputStreamReader(fileInputStream));
			while ((line = reader.readLine()) != null) {
				if (line.trim().equals(""))
					continue;
				result += line + "\r\n";
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	/**
	 * 获取文件下载路径
	 */
	private String getFilePath(){
		StringBuffer sb = new StringBuffer();
		String path = Environment.getExternalStorageDirectory().getAbsolutePath();
		sb.append(path);
		sb.append(File.separator);
		sb.append(ZHIYUE);
		sb.append(File.separator);
		sb.append(LRC);
		
		if(createDir(sb.toString())){
			return sb.toString()+File.separator;
		}
		return null;
	}
	/**
	 * 创建下载的文件夹，并判断是否创建成功，没有会创建
	 */
	private  boolean createDir(String dir){
		File dirFile = new File(dir);//文件夹
		if(!dirFile.exists()||!dirFile.isDirectory())
			return dirFile.mkdirs();
		
		return true;
	}
}
