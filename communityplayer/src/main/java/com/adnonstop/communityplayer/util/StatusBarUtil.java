package com.adnonstop.communityplayer.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import androidx.annotation.ColorInt;

public class StatusBarUtil
{

	public static int SYSTEM_FLAG = 3;


	/**
	 * 透明状态栏和导航栏
	 *
	 * @param activity
	 */
	public static void transparencyStatusBarAndNavigationBar(Activity activity)
	{

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			//透明状态栏
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			//透明导航栏
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
	}


	public static void setTranslucentStatus(Activity activity, boolean on)
	{
		Window win = activity.getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if(on)
		{
			winParams.flags |= bits;
		}
		else
		{
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}


	/**
	 * 修改状态栏颜色
	 *
	 * @param windowObj   窗体
	 * @param statusColor 颜色
	 */
	public static void setStatusBarColor1(Object windowObj, int statusColor)
	{
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			Window window = getWindow(windowObj);
			if(window != null)
			{
				//取消状态栏透明
				window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
				//添加Flag把状态栏设为可绘制模式
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
				//设置状态栏颜色
				window.setStatusBarColor(statusColor);
				//让view不根据系统窗口来调整自己的布局
//                ViewGroup mContentView = (ViewGroup)window.findViewById(Window.ID_ANDROID_CONTENT);
//                View mChildView = mContentView.getChildAt(0);
//                if(mChildView != null)
//                {
//                    ViewCompat.setFitsSystemWindows(mChildView, false);
//                    ViewCompat.requestApplyInsets(mChildView);
//                }
			}
		}
	}

	public static Window getWindow(Object windowObj)
	{
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			if(windowObj == null) return null;
			Window window = null;
			if(windowObj instanceof Activity)
			{
				window = ((Activity)windowObj).getWindow();
			}
			else if(windowObj instanceof Dialog)
			{
				window = ((Dialog)windowObj).getWindow();
			}
			return window;
		}
		return null;
	}


	/**
	 * 设置状态栏黑色文字
	 * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
	 *
	 * @param activity
	 * @return 1:MIUUI 2:Flyme 3:android6.0
	 */
	public static int setStatusBarBlackText(Activity activity)
	{
		int result = 0;
		if(!confirmMUp())
		{
			if(MIUISetStatusBarLightMode(activity, true))
			{
				result = 1;
			}
			else if(FlymeSetStatusBarLightMode(activity.getWindow(), true))
			{
				result = 2;
			}
			else
			{
				activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
				result = 3;
			}
		}
		else
		{
			if(MIUISetStatusBarLightMode(activity, true))
			{
				result = 1;
			}
			else
			{

				activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
				result = 3;
			}
		}
		SYSTEM_FLAG = result;
		return result;
	}

	/**
	 * 设置状态栏图标虚化
	 * 尽可能少地显示状态栏文字图标
	 *
	 * @param activity
	 */
	public static void setStatusLow(Activity activity)
	{
		if(confirmMUp())
		{
			activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
		}
	}

	/**
	 * 状态栏文字改成白色
	 */
	public static void setStatusBarWhiteText(Activity activity, int type)
	{
		if(type == 1)
		{
			MIUISetStatusBarLightMode(activity, false);
		}
		else if(type == 2)
		{
			FlymeSetStatusBarLightMode(activity.getWindow(), false);
		}
		else if(type == 3)
		{
			activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
		}
	}

	/**
	 * 清除状态栏的文字颜色
	 * 状态栏文字颜色恢复成白色
	 */
	public static void clearStatusBarTextColor(Activity activity)
	{
		int type = SYSTEM_FLAG;
		setStatusBarWhiteText(activity, type);
	}

	/**
	 * 清除状态栏的文字颜色
	 *
	 * @param activity           activity
	 * @param hasStatusBarHeight 是否有状态栏高度
	 */
	public static void clearStatusBarTextColor(Activity activity, boolean hasStatusBarHeight)
	{
		clearStatusBarTextColor(activity);
		if(!hasStatusBarHeight)
		{
			View rootView = ((ViewGroup)activity.findViewById(android.R.id.content)).getChildAt(0);
			if(rootView != null)
			{
				rootView.setPadding(0, 0, 0, 0);
			}
		}
		else
		{
			View rootView = ((ViewGroup)activity.findViewById(android.R.id.content)).getChildAt(0);
			if(rootView != null)
			{
				rootView.setPadding(0, getStatusBarHeight(activity), 0, 0);
			}
		}
	}


	/**
	 * 设置状态栏图标为深色和魅族特定的文字风格
	 * 可以用来判断是否为Flyme用户
	 *
	 * @param window 需要设置的窗口
	 * @param dark   是否把状态栏文字及图标颜色设置为深色
	 * @return boolean 成功执行返回true
	 */
	public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark)
	{
		boolean result = false;
		if(window != null)
		{
			try
			{
				WindowManager.LayoutParams lp = window.getAttributes();
				Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
				Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
				darkFlag.setAccessible(true);
				meizuFlags.setAccessible(true);
				int bit = darkFlag.getInt(null);
				int value = meizuFlags.getInt(lp);
				if(dark)
				{
					value |= bit;
				}
				else
				{
					value &= ~bit;
				}
				meizuFlags.setInt(lp, value);
				window.setAttributes(lp);
				result = true;
			}
			catch(Exception e)
			{

			}
		}
		return result;
	}

	/**
	 * 需要MIUIV6以上
	 *
	 * @param activity
	 * @param dark     是否把状态栏文字及图标颜色设置为深色
	 * @return boolean 成功执行返回true
	 */
	public static boolean MIUISetStatusBarLightMode(Activity activity, boolean dark)
	{
		boolean result = false;
		Window window = activity.getWindow();
		if(window != null)
		{
			Class clazz = window.getClass();
			try
			{
				int darkModeFlag = 0;
				Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
				Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
				darkModeFlag = field.getInt(layoutParams);
				Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
				if(dark)
				{
					extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
				}
				else
				{
					extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
				}
				result = true;

				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
				{
					//开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
					if(dark)
					{
						activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
					}
					else
					{
						activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
					}
				}
			}
			catch(Exception e)
			{

			}
		}
		return result;
	}

	/**
	 * 透明虚拟键盘
	 *
	 * @param activity
	 */
	public static void hideNavigation(Activity activity)
	{
		Window win = activity.getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		winParams.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		win.setAttributes(winParams);
	}

	/**
	 * 获取状态栏高度
	 *
	 * @param context context
	 * @return 状态栏高度
	 */
	public static int getStatusBarHeight(Context context)
	{
		if(!confirmFourUp())
		{
			return getStatusBarHeightByReflection(context);
		}
		// 获得状态栏高度
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		return context.getResources().getDimensionPixelSize(resourceId);
	}

	/**
	 * 清除状态栏所有风格(还原状态栏)
	 * 清除文字颜色，清除状态栏透明，清除状态栏文字颜色
	 *
	 * @param activity
	 */
	public static void clearStatusBarStyle(Activity activity)
	{
		clearStatusBarTextColor(activity, true);
//		setTranslucentStatus(activity, false);
		setStatusBarColor1(activity, Color.BLACK);
	}

	/**
	 * 计算状态栏颜色
	 * <p>
	 * eg :View.setBackgroundColor(calculateStatusColor(color, alpha));
	 *
	 * @param color color值
	 * @param alpha alpha值
	 * @return 最终的状态栏颜色
	 */
	private static int calculateStatusColor(@ColorInt int color, int alpha)
	{
		if(alpha == 0)
		{
			return color;
		}
		float a = 1 - alpha / 255f;
		int red = color >> 16 & 0xff;
		int green = color >> 8 & 0xff;
		int blue = color & 0xff;
		red = (int)(red * a + 0.5);
		green = (int)(green * a + 0.5);
		blue = (int)(blue * a + 0.5);
		return 0xff << 24 | red << 16 | green << 8 | blue;
	}

	/**
	 * 退出全屏
	 *
	 * @param activity
	 */
	public static void quitFullScreen(Activity activity)
	{
		activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	/**
	 * 设置全屏
	 *
	 * @param activity
	 */
	public static void setFullScreen(Activity activity)
	{
		activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	/**
	 * 判断手机版本是否是4.4及以上
	 *
	 * @return
	 */
	public static boolean confirmFourUp()
	{
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
	}

	/**
	 * 判断手机版本是否是6.0及以上
	 *
	 * @return
	 */
	public static boolean confirmMUp()
	{
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
	}

	/**
	 * 社区状态栏--白底黑字  适配6.0及以上
	 *
	 * @param activity
	 */
	public static void setStatusBar(Activity activity, int color)
	{
		setStatusBarColor1(activity, color);
		View rootView = ((ViewGroup)activity.findViewById(android.R.id.content)).getChildAt(0);
		if(rootView != null)
		{
			rootView.setPadding(0, getStatusBarHeight(activity), 0, 0);
		}
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			if(color == 0xffffffff)
			{
				setStatusBarBlackText(activity);
			}
		}
	}


	/**
	 * 获取导航栏高度
	 *
	 * @param context
	 * @return
	 */
	public static int getNavigationBarHeight(Context context)
	{
		int result = 0;
		int resourceId = 0;
		int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
		if(rid != 0)
		{
			resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
			return context.getResources().getDimensionPixelSize(resourceId);
		}
		else return 0;
	}

	private static int getStatusBarHeightByReflection(Context context)
	{
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try
		{
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		}
		catch(Exception e1)
		{
			e1.printStackTrace();
		}
		return statusBarHeight;
	}
}