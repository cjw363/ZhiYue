package com.cjw.zhiyue.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.UnsupportedEncodingException;

import android.util.Base64;


public class SerializeUtils {

	/**
	 * 序列化对象
	 */
	public static String serialize(Object object) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream;
		try {
			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(object);
			
			String serStr = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
			
			objectOutputStream.close();
			byteArrayOutputStream.close();
			
			return serStr;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}

	/**
	 * 反序列化对象
	 */
	public static Object deSerialize(String str){
		String redStr=null;
		try {
			 byte[] buffer = Base64.decode(str, Base64.DEFAULT);
			
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
			
			Object object =  objectInputStream.readObject();
			
			objectInputStream.close();
			byteArrayInputStream.close();
			
			return object;
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (OptionalDataException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return redStr;
	}
}
