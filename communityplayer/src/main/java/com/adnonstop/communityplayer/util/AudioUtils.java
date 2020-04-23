package com.adnonstop.communityplayer.util;

import android.media.AudioManager;

/**
 * Created by lgh on 2018/11/1
 */
public class AudioUtils
{
	public static void reqAudioFocus(AudioManager manager)
	{
		boolean isMusicActive = manager.isMusicActive();
		if(isMusicActive)
		{
			int result = manager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
			if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
			{
				// 请求成功
			}
			else
			{
				// 请求失败
			}
		}
	}

	public static void abandonAudioFocus(AudioManager manager)
	{
		manager.abandonAudioFocus(null);
	}
}
