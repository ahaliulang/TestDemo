package com.adnonstop.communityplayer.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by wxf on 2018/2/3.
 */

public class SPUtils
{


	public static final String DISABLED_NOTIFICATION = "disabled_notification";//通知
	public static final String VIDEO_VOICE_MUTE = "voice_mute";//视频是否静音
	public static final String GLOBAL_VIDEO_VOICE_MUTE = "global_video_voice_mute";//视频全局静音
	public static final String APP_BRIGHTNESS = "app_brightness";//App的亮度
	public static final String GLOBAL_AUTO_PLAY_IN_4G = "global_auto_play_in_4g";//4G环境下自动播放视频
	public static final String ASSISTANT_TIMESTAP = "assistant_timestap";//印象小助手时间戳


	/**
	 * 保存在手机里面的文件名
	 */
	private static final String FILE_NAME = "community_user_data";

	/**
	 * 保存在手机里面视频进度的文件名
	 */
	public static final String POSITION_FILE_NAME = "position_file_name";


	/**
	 * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
	 */
	public static void put(Context context, String key, Object object)
	{
		put(context, FILE_NAME, key, object);
	}


	public static void put(Context context, String fileName, String key, Object object)
	{
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		try
		{
			if(object instanceof String)
			{
				editor.putString(key, (String)object);
			}
			else if(object instanceof Integer)
			{
				editor.putInt(key, (Integer)object);
			}
			else if(object instanceof Boolean)
			{
				editor.putBoolean(key, (Boolean)object);
			}
			else if(object instanceof Float)
			{
				editor.putFloat(key, (Float)object);
			}
			else if(object instanceof Long)
			{
				editor.putLong(key, (Long)object);
			}
			else
			{
				editor.putString(key, object.toString());
			}
			SharedPreferencesCompat.apply(editor);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
	 */
	public static Object get(Context context, String key, Object defaultObject)
	{
		return get(context, FILE_NAME, key, defaultObject);
	}


	public static Object get(Context context, String fileName, String key, Object defaultObject)
	{
		if(context == null)
		{
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		if(defaultObject instanceof String)
		{
			return sp.getString(key, (String)defaultObject);
		}
		else if(defaultObject instanceof Integer)
		{
			return sp.getInt(key, (Integer)defaultObject);
		}
		else if(defaultObject instanceof Boolean)
		{
			return sp.getBoolean(key, (Boolean)defaultObject);
		}
		else if(defaultObject instanceof Float)
		{
			return sp.getFloat(key, (Float)defaultObject);
		}
		else if(defaultObject instanceof Long)
		{
			return sp.getLong(key, (Long)defaultObject);
		}
		return null;
	}

	/**
	 * 缓存数据对象 需要序列化
	 *
	 * @param context
	 * @param object
	 * @param key     缓存对象键值，建议命名为"页面_对象名_XX"（XX为特殊后缀做作区分，如无需要可以不加），
	 */
	public static void putObject(Context context, Object object, String key)
	{
		putObject(context, FILE_NAME, object, key);
	}

	public static void putObject(Context context, String fileName, Object object, String key)
	{
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		if(object == null)
		{
			SharedPreferences.Editor editor = sp.edit().remove(key);
			SharedPreferencesCompat.apply(editor);
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try
		{
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return;
		}
		// 将对象放到OutputStream中
		// 将对象转换成byte数组，并将其进行base64编码
		String objectStr = new String(Base64.encode(baos.toByteArray()));
		try
		{
			baos.close();
			oos.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		SharedPreferences.Editor editor = sp.edit();
		// 将编码后的字符串写到base64.xml文件中
		editor.putString(key, objectStr);
		SharedPreferencesCompat.apply(editor);
	}

	public static Object getObject(Context context, String key)
	{
		return getObject(context, FILE_NAME, key);
	}

	public static Object getObject(Context context, String fileName, String key)
	{
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		try
		{
			String wordBase64 = sp.getString(key, "");
			// 将base64格式字符串还原成byte数组
			if(wordBase64 == null || wordBase64.equals(""))
			{
				return null;
			}
			byte[] objBytes = Base64.decode(wordBase64);
			ByteArrayInputStream bais = new ByteArrayInputStream(objBytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			Object obj = ois.readObject();
			bais.close();
			ois.close();
			return obj;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 移除某个key值已经对应的值
	 */
	public static void remove(Context context, String key)
	{
		remove(context, FILE_NAME, key);
	}

	public static void remove(Context context, String fileName, String key)
	{
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		SharedPreferencesCompat.apply(editor);
	}

	/**
	 * 清除所有数据
	 */
	public static void clear(Context context)
	{
		clear(context, FILE_NAME);
	}

	public static void clear(Context context, String fileName)
	{
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		SharedPreferencesCompat.apply(editor);
	}

	/**
	 * 查询某个key是否已经存在
	 */
	public static boolean contains(Context context, String key)
	{
		return contains(context, FILE_NAME, key);
	}

	public static boolean contains(Context context, String fileName, String key)
	{
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return sp.contains(key);
	}

	/**
	 * 返回所有的键值对
	 */
	public static Map<String, ?> getAll(Context context)
	{
		return getAll(context, FILE_NAME);
	}

	public static Map<String, ?> getAll(Context context, String fileName)
	{
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return sp.getAll();
	}


	/**
	 * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
	 */
	private static class SharedPreferencesCompat
	{
		private static final Method sApplyMethod = findApplyMethod();

		/**
		 * 反射查找apply的方法
		 */
		@SuppressWarnings({"unchecked", "rawtypes"})
		private static Method findApplyMethod()
		{
			try
			{
				Class clz = SharedPreferences.Editor.class;
				return clz.getMethod("apply");
			}
			catch(NoSuchMethodException e)
			{
			}
			return null;
		}

		/**
		 * 如果找到则使用apply执行，否则使用commit
		 */
		public static void apply(SharedPreferences.Editor editor)
		{
			try
			{
				if(sApplyMethod != null)
				{
					sApplyMethod.invoke(editor);
					return;
				}
			}
			catch(IllegalArgumentException e)
			{
			}
			catch(IllegalAccessException e)
			{
			}
			catch(InvocationTargetException e)
			{
			}
			editor.commit();
		}
	}


	/**
	 * 保存播放位置，以便进入视频信息流与退出视频信息流从当前位置播放
	 *
	 * @param context
	 * @param url      视频连接
	 * @param position 保存的视频播放位置
	 */
	public static void putPlayerPosition(Context context, String url, long position)
	{
		put(context, POSITION_FILE_NAME, url, position);
	}

	/**
	 * 获取视频连接保存到的播放位置
	 *
	 * @param context
	 * @param url
	 * @return
	 */
	public static long getPlayerPosition(Context context, String url)
	{
		if(get(context, POSITION_FILE_NAME, url, 0L) instanceof Long)
		{
			return (long)get(context, POSITION_FILE_NAME, url, 0L);
		}
		return 0L;
	}

	/**
	 * 移除视频连接保存到的播放位置
	 *
	 * @param context
	 * @param url
	 */
	public static void removePlayerPosition(Context context, String url)
	{
		remove(context, POSITION_FILE_NAME, url);
	}

	/**
	 * 清除掉保存到视频所有位置
	 *
	 * @param context
	 */
	public static void clearPlayerPosition(Context context)
	{
		clear(context, POSITION_FILE_NAME);
	}

}
