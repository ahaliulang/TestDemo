package com.adnonstop.communityplayer;

import android.content.Context;
import android.text.TextUtils;

import com.adnonstop.communityplayer.bean.Constant;
import com.adnonstop.communityplayer.util.CommunityPlayerUtils;
import com.adnonstop.communityplayer.util.LogUtil;
import com.adnonstop.communityplayer.util.SPUtils;

import java.io.File;

/**
 * author:tdn
 * time:2019/4/24
 * description:社区播放器一些要初始化的操作
 */
public class CommunityPlayer
{

	/**
	 * app代号
	 */
	public static int APP_CODE = 1;
	/**
	 * 在一起代号
	 */
	public static final int CIRCLE = 1;
	/**
	 * 美人相机代号
	 */
	public static final int BEAUTY = 3;
	/**
	 * 简拼代号
	 */
	public static final int JANE = 2;
	/**
	 * 印象代号
	 */
	public static final int INTERPHOTO = 4;
	/**
	 * 图片合成器代号
	 */
	public static final int COMPLEX = 5;
	/**
	 * 简客
	 */
	public static final int PLUS = 6;
	/**
	 * 小美人
	 */
	public static final int TINY = 7;
	/**
	 * 型男相机
	 */
	public static final int MAN = 6;
	/**
	 * poco相机
	 */
	public static final int POCO = 8;


	/**
	 * app主题颜色
	 */
	public static int s_APP_SKIN_COLOR = 0;
	/**
	 * 渐变色1
	 */
	public static int s_APP_SKIN_COLOR1 = 0;
	/**
	 * 渐变色2
	 */
	public static int s_APP_SKIN_COLOR2 = 0;


	private static String sAppPath;//App 主目录绝对路径

	private static String sVideoCachePath;//视频缓存目录 = CommunityPlayer.getsAppPath() + Constant.PATH_PAGE_VIDEO_CACHE + Constant.HIDE_FILE;
	private static Context mContext;


	/**
	 * 初始化社区
	 * 设置社区一些必要的参数
	 *
	 * @param app_code        app所属代号 {@link CommunityPlayer#CIRCLE}
	 * @param app_path        设置app主目录
	 * @param protocol_header web版直接打开社区所用协议头
	 */
	public static void init(Context context, int app_code, String app_path, String protocol_header)
	{
		if(context != null)
		{
			mContext = context.getApplicationContext();
		}
		APP_CODE = app_code;
		sAppPath = TextUtils.isEmpty(app_path) ? "" : app_path;
		String tempPath = "";
		if(TextUtils.isEmpty(app_path))
		{
			tempPath = CommunityPlayerUtils.getSdcardPath();
		}
		else
		{
			if(!app_path.startsWith("/"))
			{
				tempPath = CommunityPlayerUtils.getSdcardPath() + "/" + app_path;
			}
			if(app_path.endsWith("/"))
			{
				tempPath = CommunityPlayerUtils.getSdcardPath() + app_path.substring(0, app_path.length() - 1);
			}
		}

		sVideoCachePath = tempPath + Constant.PATH_PAGE_VIDEO_CACHE + Constant.HIDE_FILE;

		LogUtil.init(context);
		//清除掉位置信息
		SPUtils.clearPlayerPosition(context);
		//创建视频缓存目录
		try
		{
			File file = new File(sVideoCachePath);
			if(!file.exists())
			{
				file.mkdirs();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * 设置皮肤颜色
	 *
	 * @param color  主颜色
	 * @param color1 渐变颜色1 可以为空
	 * @param color2 渐变颜色2 可以为空
	 */
	public static void setAppSkinColor(int color, int color1, int color2)
	{
		s_APP_SKIN_COLOR = color;
		s_APP_SKIN_COLOR1 = color1;
		s_APP_SKIN_COLOR2 = color2;
	}


	/*public static boolean isDetectedFirstPlay()
	{
		return sDetectedFirstPlay;
	}

	public static void setDetectedFirstPlay(boolean detectedFirstPlay)
	{
		sDetectedFirstPlay = detectedFirstPlay;
	}

	public static boolean isAutoPlayInList()
	{
		return sAutoPlayInList;
	}

	public static void setAutoPlayInList(boolean autoPlayInList)
	{
		sAutoPlayInList = autoPlayInList;
	}

	public static boolean isShowingDialog()
	{
		return sShowingDialog;
	}

	public static void setShowingDialog(boolean showingDialog)
	{
		sShowingDialog = showingDialog;
	}*/


	/**
	 * 获取App主目录
	 *
	 * @return
	 */
	public static String getsAppPath()
	{
		return sAppPath;
	}

	/**
	 * 获取视频缓存目录
	 *
	 * @return
	 */
	public static String getVideoCachePath()
	{
		return sVideoCachePath;
	}

	/**
	 * 设置是否在4G环境下自动播放视频
	 *
	 * @param context
	 * @param auto    true 自动播放，false 反之
	 */
	public static void setGlobalAutoPlayIn4G(Context context, boolean auto)
	{
		if(context != null)
		{
			SPUtils.put(context, SPUtils.GLOBAL_AUTO_PLAY_IN_4G, auto);
		}
	}


	/**
	 * 获取4G环境下自动播放视频的状态
	 *
	 * @param context
	 * @return true 自动播放 ，false 反之
	 */
	public static boolean getGlobalAutoPlayIn4G(Context context)
	{
		if(context != null)
		{
			return (boolean)SPUtils.get(context, SPUtils.GLOBAL_AUTO_PLAY_IN_4G, false);
		}
		return false;
	}


	/**
	 * 设置视频是否默认静音
	 *
	 * @param context
	 * @param mute    true 静音，false 反之
	 */
	public static void setGlobalVideoAutoMute(Context context, boolean mute)
	{
		if(context != null)
		{
			SPUtils.put(context, SPUtils.GLOBAL_VIDEO_VOICE_MUTE, mute);
		}
	}


	/**
	 * 获取视频是否默认静音状态
	 *
	 * @param context
	 * @return true 静音，false 反之
	 */
	public static boolean getGlobalVideoAutoMute(Context context)
	{
		if(context != null)
		{
			return (boolean)SPUtils.get(context, SPUtils.GLOBAL_VIDEO_VOICE_MUTE, false);
		}
		return false;
	}

	/**
	 * 获取 Applicaiont 的Context
	 *
	 * @return
	 */
	public static Context getApplicationContext()
	{
		return mContext;
	}
}
