package com.pxpd.App;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * 配置文件快速访问类
 * @author liuzeren
 *
 */
public final class Config {




	public static String ClerkID;
	public static String ClerkStationID;

	public static int Mode=0;//当前用户选择的模式
	public static String ServerIP="";//服务器地址
	public static String ServerPort="";
	public static String Serverhttp="";
	public static int RunMode=0;// 0 在线 1 离线
	private static final String url="/CSharpBackground/WebServer.asmx/";

	public static void setServerInfo()
	{
		Serverhttp = String.format("http://%1$s:%2$s/%3$s",ServerIP,ServerPort,url);
	}

	public static String getSrvUrl(String Method)
	{
		return Serverhttp+Method;
	}

	/**
	 * 获取本地用户信息
	 * @param context
	 * @param key
     * @return
     */
	public static String getKeyShareVarForString(Context context, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_APPEND);
		return sharedPreferences.getString(key, "null");
	}


	public static Boolean getKeyShareVarForBoolean(Context context,String key)
	{
		SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(key, false);
	}

	public static int getKeyShareVarForint(Context context,String key)
	{
		SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
		return sharedPreferences.getInt(key, -1);
	}

	public static long getKeyShareVarForLong(Context context,String key)
	{
		SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
		return sharedPreferences.getLong(key, -1);
	}
	/**
	 * 设置信息
	 * @param context
	 * @param key
	 * @param value
     */
	public static void setKeyShareVar(Context context,String key,String value)
	{
		SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_APPEND);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key,value);
		editor.commit();
	}

	public static void setKeyShareVar(Context context,String key,int value)
	{
		SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_APPEND);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(key,value);
		editor.commit();
	}

	public static void setKeyShareVar(Context context,String key,boolean value)
	{
		SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_APPEND);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(key,value);
		editor.commit();
	}
	/**
	 * 删除信息
	 * @param context
	 * @param key
     */
	public static void delKeyShareVar(Context context,String key)
	{
		SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_APPEND);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.remove(key);
		editor.commit();
	}


}
