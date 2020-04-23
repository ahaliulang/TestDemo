package com.adnonstop.communityplayer.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

public class BrightnessHelper
{
	private ContentResolver mResolver;
	private int maxBrightness = 255;
	private Context mContext;

	public BrightnessHelper(Context context)
	{
		mResolver = context.getContentResolver();
		mContext = context;
	}

	/*
	 * 调整亮度范围
	 */
	private int adjustBrightnessNumber(int brightness)
	{
		if(brightness < 0)
		{
			brightness = 0;
		}
		else if(brightness > 255)
		{
			brightness = 255;
		}
		return brightness;
	}

	/*
	 * 关闭自动调节亮度
	 */
	public void offAutoBrightness()
	{
		try
		{
			if(Settings.System.getInt(mResolver, Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC)
			{
				Settings.System.putInt(mResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
			}
		}
		catch(Settings.SettingNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 获取系统亮度
	 */
	public int getSystemBrightness()
	{
		return Settings.System.getInt(mResolver, Settings.System.SCREEN_BRIGHTNESS, 255);
	}


	/**
	 * 设置系统亮度，如果有设置了自动调节，请先调用offAutoBrightness()方法关闭自动调节，否则会设置失败
	 */
	public void setSystemBrightness(int newBrightness)
	{
		Settings.System.putInt(mResolver, Settings.System.SCREEN_BRIGHTNESS, adjustBrightnessNumber(newBrightness));
	}

	public int getMaxBrightness()
	{
		return maxBrightness;
	}


	/**
	 * 设置当前APP的亮度
	 *
	 * @param activity
	 * @param brightnessPercent 0 to 1 adjusts the brightness from dark to full bright
	 */
	public void setAppBrightness(Activity activity, float brightnessPercent)
	{
		Window window = activity.getWindow();
		WindowManager.LayoutParams layoutParams = window.getAttributes();
		layoutParams.screenBrightness = brightnessPercent;
		window.setAttributes(layoutParams);
	}

	/**
	 * 获取当前页面亮度
	 * @return
	 */
	public float getAppBrightness()
	{
		float brightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
		if(mContext instanceof Activity)
		{
			Window window = ((Activity)mContext).getWindow();
			WindowManager.LayoutParams layoutParams = window.getAttributes();
			brightness = layoutParams.screenBrightness;
		}
		if(brightness == WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE)
		{
			brightness = getSystemBrightness() / 255f;
		}
		return brightness;
	}
}
