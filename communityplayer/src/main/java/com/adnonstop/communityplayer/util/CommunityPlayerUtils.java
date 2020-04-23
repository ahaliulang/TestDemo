package com.adnonstop.communityplayer.util;

import android.app.Activity;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.adnonstop.communityplayer.CommunityPlayer;

import java.io.File;

/**
 * author:tdn
 * time:2019/4/24
 * description: 工具类
 */
public class CommunityPlayerUtils
{


	public static float sDensity;
	public static float sDensityDpi;
	public static int sScreenW;
	public static int sScreenH;

	public static int sRelativeScreenW = 720;
	public static int sRelativeScreenH = 1280;


	/**
	 * 需要在 BaseActivity 里面初始化
	 *
	 * @param activity
	 */
	public static void init(Activity activity)
	{
		Display dis = activity.getWindowManager().getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		dis.getMetrics(dm);
		int h = dis.getHeight();
		int w = dis.getWidth();
		sScreenW = w < h ? w : h;
		sScreenH = w < h ? h : w;
		sDensity = dm.density;
		sDensityDpi = dm.densityDpi;
	}

	public static int getScreenW()
	{
		return sScreenW;
	}

	public static int getScreenH()
	{
		return sScreenH;
	}

	public static String getSdcardPath()
	{
		File sdcard = Environment.getExternalStorageDirectory();
		if(sdcard == null)
		{
			return "";
		}
		return sdcard.getPath();
	}

	/**
	 * 获取app主题颜色
	 *
	 * @return
	 */
	public static int GetSkinColor()
	{
		return CommunityPlayer.s_APP_SKIN_COLOR;
	}

	/**
	 * 获取渐变色1
	 *
	 * @return
	 */
	public static int GetSkinColor1()
	{
		if(CommunityPlayer.s_APP_SKIN_COLOR1 == 0)
		{
			return GetSkinColor();
		}
		return CommunityPlayer.s_APP_SKIN_COLOR1;
	}

	/**
	 * 获取渐变色2
	 *
	 * @return
	 */
	public static int GetSkinColor2()
	{
		if(CommunityPlayer.s_APP_SKIN_COLOR2 == 0)
		{
			return GetSkinColor();
		}
		return CommunityPlayer.s_APP_SKIN_COLOR2;
	}


	/**
	 * 是否使屏幕常亮
	 *
	 * @param activity
	 */
	public static void keepScreenLongLight(Activity activity, boolean isOpenLight)
	{
		Window window = activity.getWindow();
		if(isOpenLight)
		{
			window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
		else
		{
			window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}

	}

	public static int getRealPixel(int pxSrc)
	{
		int pix = (int)(pxSrc * sDensity / 2.0);
		if(pxSrc == 1 && pix == 0)
		{
			pix = 1;
		}
		return pix;
	}

	public static int getRealPixel2(int pxSrc)
	{
		int pix = (int)(pxSrc * sScreenW / sRelativeScreenW);
		if(pxSrc == 1 && pix == 0)
		{
			pix = 1;
		}
		return pix;
	}

}
