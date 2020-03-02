package com.cjw.zhiyue.global;

import java.util.Stack;

import org.xutils.x;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;

public class ZhiYueApplication extends Application {

	private static Context context;
	private static Handler handler;
	private static int mainThreadId;
	private static Stack<Activity> mStack = new Stack<Activity>();
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		x.Ext.init(this);//初始化xutils3
		
		context = getApplicationContext();
		handler = new Handler();
		mainThreadId = android.os.Process.myTid();
	}

	public static Context getContext() {
		return context;
	}

	public static Handler getHandler() {
		return handler;
	}

	public static int getMainThreadId() {
		return mainThreadId;
	}

	public static void putActivity(Activity activity){
		mStack.add(activity);
	}
	
	public static Activity getActivity(){
		return mStack.get(0);
	}
}
