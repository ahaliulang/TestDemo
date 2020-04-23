package com.adnonstop.communityplayer;

import android.annotation.TargetApi;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.adnonstop.communityplayer.myinterface.IMediaPlayer;

import java.io.FileDescriptor;
import java.lang.ref.WeakReference;

/**
 * author:tdn
 * time:2018/12/29
 * description: Android系统播放器
 */
public class CAndroidMediaPlayer extends AbstractMediaPlayer
{
	private final MediaPlayer mInternalMediaPlayer;
	private final CAndroidMediaPlayer.CAndroidMediaPlayerListenerHolder mInternalListenerAdapter;

	private String mDataSource;
	private final Object mInitLock = new Object();
	private boolean mIsReleased;
	private boolean mLooping;


	public CAndroidMediaPlayer()
	{
		synchronized(this.mInitLock)
		{
			this.mInternalMediaPlayer = new MediaPlayer();
		}

		this.mInternalMediaPlayer.setAudioStreamType(3);
		this.mInternalListenerAdapter = new CAndroidMediaPlayer.CAndroidMediaPlayerListenerHolder(this);
		this.attachInternalListeners();
	}

	private void attachInternalListeners()
	{
		this.mInternalMediaPlayer.setOnPreparedListener(this.mInternalListenerAdapter);
		this.mInternalMediaPlayer.setOnCompletionListener(this.mInternalListenerAdapter);
		this.mInternalMediaPlayer.setOnVideoSizeChangedListener(this.mInternalListenerAdapter);
		this.mInternalMediaPlayer.setOnErrorListener(this.mInternalListenerAdapter);
		this.mInternalMediaPlayer.setOnInfoListener(this.mInternalListenerAdapter);
		this.mInternalMediaPlayer.setOnSeekCompleteListener(this.mInternalListenerAdapter);
	}

	@TargetApi(14)
	public void setSurface(Surface surface)
	{
		this.mInternalMediaPlayer.setSurface(surface);
	}

	@Override
	public void setDataSource(String path) throws Exception
	{
		this.mDataSource = path;
		Uri uri = Uri.parse(path);
		String scheme = uri.getScheme();
		if(!TextUtils.isEmpty(scheme) && scheme.equalsIgnoreCase("file"))
		{
			this.mInternalMediaPlayer.setDataSource(uri.getPath());
		}
		else
		{
			this.mInternalMediaPlayer.setDataSource(path);
		}
	}

	@Override
	public void setDataSource(FileDescriptor fd) throws Exception
	{
		if(fd != null)
		{
			this.mInternalMediaPlayer.setDataSource(fd);
		}
	}

	@Override
	public void setDisplay(SurfaceHolder sh)
	{
		Object var2 = this.mInitLock;
		synchronized(this.mInitLock)
		{
			if(!this.mIsReleased)
			{
				this.mInternalMediaPlayer.setDisplay(sh);
			}
		}
	}

	@Override
	public void setPlayingCache(boolean enable, String saveDir, int maxDuration, long maxSize)
	{
		//donothing
	}

	@Override
	public void prepareAsync() throws IllegalStateException
	{
		this.mInternalMediaPlayer.prepareAsync();
	}

	@Override
	public void start() throws IllegalStateException
	{
		this.mInternalMediaPlayer.start();
	}

	@Override
	public void pause() throws IllegalStateException
	{
		this.mInternalMediaPlayer.pause();
	}

	@Override
	public void resume() throws IllegalStateException
	{
		this.mInternalMediaPlayer.start();
	}

	@Override
	public void stop() throws IllegalStateException
	{
		this.mInternalMediaPlayer.stop();
	}

	@Override
	public void seekTo(long position)
	{
		this.mInternalMediaPlayer.seekTo((int)position);
	}

	@Override
	public void setMuteMode(boolean var1)
	{
		if(var1)
		{
			this.mInternalMediaPlayer.setVolume(0f, 0f);

		}
		else
		{
			this.mInternalMediaPlayer.setVolume(1f, 1f);
		}
	}

	@Override
	public long getDuration()
	{
		try
		{
			return (long)this.mInternalMediaPlayer.getDuration();
		}
		catch(IllegalStateException var2)
		{
			return 0L;
		}
	}

	@Override
	public long getCurrentPosition()
	{
		try
		{
			return (long)this.mInternalMediaPlayer.getCurrentPosition();
		}
		catch(IllegalStateException var2)
		{
			return 0L;
		}
	}

	@Override
	public void setLooping(boolean var1)
	{
		mLooping = var1;
		//在播放完成后调用播放方法，解决部分进行循环播放的bug
		//this.mInternalMediaPlayer.setLooping(var1);
	}

	@Override
	public boolean getLooping()
	{
		return mLooping;
	}

	@Override
	public void setVideoScalingMode(VideoScalingMode var1)
	{
		//do nothing
	}

	@Override
	public void reset()
	{
		try
		{
			this.mInternalMediaPlayer.reset();
		}
		catch(IllegalStateException var2)
		{
		}

		this.resetListeners();
		this.attachInternalListeners();
	}


	@Override
	public void release()
	{
		this.mIsReleased = true;
		this.mInternalMediaPlayer.release();
		this.resetListeners();
		this.attachInternalListeners();
	}


	@Override
	public Object getInternalPlayer()
	{
		return mInternalMediaPlayer;
	}


	private class CAndroidMediaPlayerListenerHolder implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener, MediaPlayer.OnSeekCompleteListener
	{
		public final WeakReference<CAndroidMediaPlayer> mWeakMediaPlayer;

		public CAndroidMediaPlayerListenerHolder(CAndroidMediaPlayer mp)
		{
			this.mWeakMediaPlayer = new WeakReference(mp);
		}

		public void onPrepared(MediaPlayer mp)
		{
			CAndroidMediaPlayer self = (CAndroidMediaPlayer)this.mWeakMediaPlayer.get();
			if(self != null)
			{
				CAndroidMediaPlayer.this.notifyOnPrepared();
			}
		}

		public void onCompletion(MediaPlayer mp)
		{
			CAndroidMediaPlayer self = (CAndroidMediaPlayer)this.mWeakMediaPlayer.get();
			if(self != null)
			{
				CAndroidMediaPlayer.this.notifyOnCompletion();
				if(mLooping)
				{
					CAndroidMediaPlayer.this.start();
				}
			}
		}

		public void onVideoSizeChanged(MediaPlayer mp, int width, int height)
		{
			CAndroidMediaPlayer self = (CAndroidMediaPlayer)this.mWeakMediaPlayer.get();
			if(self != null)
			{
				CAndroidMediaPlayer.this.notifyOnVideoSizeChanged(width, height);
			}
		}

		public boolean onError(MediaPlayer mp, int what, int extra)
		{
			CAndroidMediaPlayer self = (CAndroidMediaPlayer)this.mWeakMediaPlayer.get();
			return self != null && CAndroidMediaPlayer.this.notifyOnError(what, extra);
		}

		public boolean onInfo(MediaPlayer mp, int what, int extra)
		{
			CAndroidMediaPlayer self = (CAndroidMediaPlayer)this.mWeakMediaPlayer.get();
			//第一帧
			if(what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START)
			{
				if(self != null)
				{
					CAndroidMediaPlayer.this.notifyOnFirstFrameStart();
				}
			}
			int defaultCode = IMediaPlayer.C_MEDIA_INFO_DEFAULT;
			switch(what)
			{
				case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
					defaultCode = IMediaPlayer.C_MEDIA_INFO_VIDEO_RENDERING_START;
					break;
				case MediaPlayer.MEDIA_INFO_BUFFERING_START:
					defaultCode = IMediaPlayer.C_MEDIA_INFO_BUFFERING_START;
					break;
				case MediaPlayer.MEDIA_INFO_BUFFERING_END:
					defaultCode = IMediaPlayer.C_MEDIA_INFO_BUFFERING_END;
			}
			return self != null && CAndroidMediaPlayer.this.notifyOnInfo(defaultCode, extra);
		}

		public void onSeekComplete(MediaPlayer mp)
		{
			CAndroidMediaPlayer self = (CAndroidMediaPlayer)this.mWeakMediaPlayer.get();
			if(self != null)
			{
				CAndroidMediaPlayer.this.notifyOnSeekComplete();
			}
		}
	}


}
