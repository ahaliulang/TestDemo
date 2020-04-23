package com.adnonstop.communityplayer.util;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * author:tdn
 * time:2019/8/5
 * description: 判断是否有网络、网络类型、GPS定位是否打开
 */
public class NetUtils
{

	public static final int TYPE_NONE = 1;
	public static final int TYPE_MOBILE = 2;
	public static final int TYPE_WIFI = 3;


	/**
	 * 判断是否有网络连接
	 *
	 * @param context Context
	 * @return boolean
	 */
	public static boolean isNetworkConnected(Context context)
	{
		if(context != null && context.getApplicationContext() != null)
		{
			ConnectivityManager manager = (ConnectivityManager)(context.getApplicationContext()).getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = manager.getActiveNetworkInfo();
			if(networkInfo != null)
			{
				return networkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 判断WIFI网络是否可用
	 *
	 * @param context Context
	 * @return boolean
	 */
	public static boolean isWifiConnected(Context context)
	{
		if(context != null)
		{
			// 获取手机所有连接管理对象(包括对wi-fi,net等连接的管理)
			ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			// 获取NetworkInfo对象
			NetworkInfo networkInfo = manager.getActiveNetworkInfo();
			//判断NetworkInfo对象是否为空 并且类型是否为WIFI
			if(networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
			{
				return networkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 判断MOBILE网络是否可用
	 *
	 * @param context Context
	 * @return boolean
	 */
	public static boolean isMobileConnected(Context context)
	{
		if(context != null)
		{
			//获取手机所有连接管理对象(包括对wi-fi,net等连接的管理)
			ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			//获取NetworkInfo对象
			NetworkInfo networkInfo = manager.getActiveNetworkInfo();
			//判断NetworkInfo对象是否为空 并且类型是否为MOBILE
			if(networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
			{
				return networkInfo.isAvailable();
			}
		}
		return false;
	}

	public static int getConnectedType(Context context)
	{
		if(context != null)
		{
			//获取手机所有连接管理对象
			ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			//获取NetworkInfo对象
			NetworkInfo networkInfo = manager.getActiveNetworkInfo();
			if(networkInfo != null && networkInfo.isAvailable())
			{
				//返回NetworkInfo的类型
				return networkInfo.getType();
			}
		}
		return -1;
	}

	/**
	 * 获取当前的网络状态:{@link NetUtils#TYPE_NONE 没有网络}，{@link NetUtils#TYPE_MOBILE 手机网络},{@link NetUtils#TYPE_WIFI wifi网络}
	 * 自定义
	 *
	 * @param context Context
	 * @return int
	 */
	public static int getNetType(Context context)
	{
		int netType = TYPE_NONE;
		ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		//NetworkInfo对象为空 则代表没有网络
		if(networkInfo == null)
		{
			return netType;
		}
		int nType = networkInfo.getType();
		if(nType == ConnectivityManager.TYPE_WIFI)
		{
			//WIFI
			netType = TYPE_WIFI;
		}
		else if(nType == ConnectivityManager.TYPE_MOBILE)
		{
			/*int nSubType = networkInfo.getSubtype();
			TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			//3G   联通的3G为UMTS或HSDPA 电信的3G为EVDO
			if(nSubType == TelephonyManager.NETWORK_TYPE_LTE && !telephonyManager.isNetworkRoaming())
			{
				netType = 4;
			}
			else if(nSubType == TelephonyManager.NETWORK_TYPE_UMTS || nSubType == TelephonyManager.NETWORK_TYPE_HSDPA || nSubType == TelephonyManager.NETWORK_TYPE_EVDO_0 && !telephonyManager.isNetworkRoaming())
			{
				netType = 3;
				//2G 移动和联通的2G为GPRS或EGDE，电信的2G为CDMA
			}
			else if(nSubType == TelephonyManager.NETWORK_TYPE_GPRS || nSubType == TelephonyManager.NETWORK_TYPE_EDGE || nSubType == TelephonyManager.NETWORK_TYPE_CDMA && !telephonyManager.isNetworkRoaming())
			{
				netType = 2;
			}
			else
			{
				netType = 2;
			}*/
			netType = TYPE_MOBILE;
		}
		return netType;
	}

	/**
	 * 判断GPS是否打开
	 * ACCESS_FINE_LOCATION权限
	 *
	 * @param context Context
	 * @return boolean
	 */
	public static boolean isGPSEnabled(Context context)
	{
		LocationManager locationManager = ((LocationManager)context.getSystemService(Context.LOCATION_SERVICE));
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
}
