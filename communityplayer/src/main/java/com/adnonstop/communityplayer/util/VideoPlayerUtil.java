package com.adnonstop.communityplayer.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.WindowManager;

import java.util.Formatter;
import java.util.Locale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

/**
 * author:tdn
 * time:2018/12/25
 * description:
 */
public class VideoPlayerUtil
{
	/**
	 * Get activity from context object
	 *
	 * @param context something
	 * @return object of Activity or null if it is not Activity
	 */
	public static Activity scanForActivity(Context context)
	{
		if(context == null) return null;
		if(context instanceof Activity)
		{
			return (Activity)context;
		}
		else if(context instanceof ContextWrapper)
		{
			return scanForActivity(((ContextWrapper)context).getBaseContext());
		}
		return null;
	}

	/**
	 * Get AppCompatActivity from context
	 *
	 * @param context
	 * @return AppCompatActivity if it's not null
	 */
	private static AppCompatActivity getAppCompActivity(Context context)
	{
		if(context == null) return null;
		if(context instanceof AppCompatActivity)
		{
			return (AppCompatActivity)context;
		}
		else if(context instanceof ContextThemeWrapper)
		{
			return getAppCompActivity(((ContextThemeWrapper)context).getBaseContext());
		}
		return null;
	}

	public static void showActionBar(Context context)
	{
		if(context != null && getAppCompActivity(context) != null)
		{
			ActionBar ab = getAppCompActivity(context).getSupportActionBar();
			if(ab != null)
			{
				ab.setShowHideAnimationEnabled(false);
				ab.show();
			}
			scanForActivity(context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
			{
				WindowManager.LayoutParams lp = scanForActivity(context).getWindow().getAttributes();
				lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
				// 设置页面延伸到刘海区显示
				scanForActivity(context).getWindow().setAttributes(lp);
			}
		}

	}

	public static void hideActionBar(Context context)
	{
		if(context != null && getAppCompActivity(context) != null)
		{
			ActionBar ab = getAppCompActivity(context).getSupportActionBar();
			if(ab != null)
			{
				ab.setShowHideAnimationEnabled(false);
				ab.hide();
			}
			scanForActivity(context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			//scanForActivity(context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
			//scanForActivity(context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			//scanForActivity(context).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
			{
				WindowManager.LayoutParams lp = scanForActivity(context).getWindow().getAttributes();
				lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
				// 设置页面延伸到刘海区显示
				scanForActivity(context).getWindow().setAttributes(lp);
			}
		}
	}

	/**
	 * 获取屏幕宽度
	 *
	 * @param context
	 * @return width of the screen.
	 */
	public static int getScreenWidth(Context context)
	{
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * 获取屏幕高度
	 *
	 * @param context
	 * @return heiht of the screen.
	 */
	public static int getScreenHeight(Context context)
	{
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	/**
	 * dp转px
	 *
	 * @param context
	 * @param dpVal   dp value
	 * @return px value
	 */
	public static int dp2px(Context context, float dpVal)
	{
		return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources().getDisplayMetrics());
	}

	/**
	 * 将毫秒数格式化为"##:##"的时间
	 *
	 * @param milliseconds 毫秒数
	 * @return ##:##
	 */
	public static String formatTime(long milliseconds)
	{
		if(milliseconds <= 0 || milliseconds >= 24 * 60 * 60 * 1000)
		{
			return "00:00";
		}
		long totalSeconds = milliseconds / 1000;
		long seconds = totalSeconds % 60;
		long minutes = (totalSeconds / 60) % 60;
		long hours = totalSeconds / 3600;
		StringBuilder stringBuilder = new StringBuilder();
		Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
		if(hours > 0)
		{
			return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
		}
		else
		{
			return mFormatter.format("%02d:%02d", minutes, seconds).toString();
		}
	}

	/**
	 * 保存播放位置，以便下次播放时接着上次的位置继续播放.
	 *
	 * @param context
	 * @param url     视频链接url
	 */
	public static void savePlayPosition(Context context, String url, long position)
	{
		//有需求再加上，避免浪费资源
		//context.getSharedPreferences("VIDEO_PLAYER_PLAY_POSITION", Context.MODE_PRIVATE).edit().putLong(url, position).apply();
	}

	/**
	 * 取出上次保存的播放位置
	 *
	 * @param context
	 * @param url     视频链接url
	 * @return 上次保存的播放位置
	 */
	public static long getSavedPlayPosition(Context context, String url)
	{
		////有需求再加上，避免浪费资源
		//return context.getSharedPreferences("VIDEO_PLAYER_PLAY_POSITION", Context.MODE_PRIVATE).getLong(url, 0);
		return 0;
	}


	/**
	 * 获取 App 系统亮度
	 *
	 * @param context
	 * @return
	 */
	public static int getAppBrightness(Context context)
	{
		ContentResolver contentResolver = context.getContentResolver();
		return Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, 255);
	}
}

