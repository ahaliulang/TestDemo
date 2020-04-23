package com.adnonstop.communityplayer.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

/**
 * author:tdn
 * time:2018/12/25
 * description:
 */
public class LogUtil
{
	public static final boolean SHOW_LOG = true;

	private static Context mContext;

	private LogUtil()
	{
	}

	public static void init(Context context)
	{
		if(context != null)
		{
			mContext = context.getApplicationContext();
		}
	}


	private static boolean isApkInDebug()
	{
		if(mContext == null) return false;
		try
		{
			ApplicationInfo info = mContext.getApplicationInfo();
			return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	private static final String TAG = "TAN_LOG";

	public static void d(String message)
	{
		if(SHOW_LOG /*&& isApkInDebug()*/)
		{
			Log.d(TAG, message);
		}
	}

	public static void i(String message)
	{
		if(SHOW_LOG && isApkInDebug())
		{
			Log.i(TAG, message);
		}
	}

	public static void e(String message, Throwable throwable)
	{
		if(SHOW_LOG && isApkInDebug())
		{
			Log.e(TAG, message, throwable);
		}
	}
}
