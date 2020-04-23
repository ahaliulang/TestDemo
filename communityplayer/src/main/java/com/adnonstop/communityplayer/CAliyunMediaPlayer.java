package com.adnonstop.communityplayer;

import android.content.Context;
import android.text.TextUtils;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.adnonstop.communityplayer.myinterface.IMediaPlayer;
import com.adnonstop.communityplayer.util.LogUtil;
import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.bean.InfoCode;
import com.aliyun.player.nativeclass.CacheConfig;
import com.aliyun.player.source.UrlSource;

import java.io.FileDescriptor;
import java.lang.ref.WeakReference;


/**
 * author:tdn
 * time:2018/12/29
 * description:阿里云系统播放器
 */
public class CAliyunMediaPlayer extends AbstractMediaPlayer
{

	private final AliPlayer mInternalAliyunVodPlayer;
	private final CAliyunMediaPlayer.CAliyunMediaPlayerPlayerListenerHolder mInternalListenerAdapter;

	private final Object mInitLock = new Object();
	private boolean mIsReleased;
	private boolean mLooping;
	private long mCurrentPosition;

	public CAliyunMediaPlayer(Context context)
	{
		synchronized(this.mInitLock)
		{
			mInternalAliyunVodPlayer = AliPlayerFactory.createAliPlayer(context.getApplicationContext());
			CacheConfig cacheConfig = new CacheConfig();
			cacheConfig.mEnable = true;
			cacheConfig.mDir = CommunityPlayer.getVideoCachePath();
			cacheConfig.mMaxDurationS = Integer.MAX_VALUE;
			cacheConfig.mMaxSizeMB = 1024 * 1024 * 1024;
			this.mInternalAliyunVodPlayer.setCacheConfig(cacheConfig);
		}
		this.mInternalAliyunVodPlayer.setScaleMode(IPlayer.ScaleMode.SCALE_ASPECT_FILL);
		this.mInternalAliyunVodPlayer.enableLog(false);
		this.mInternalListenerAdapter = new CAliyunMediaPlayer.CAliyunMediaPlayerPlayerListenerHolder(this);
		this.attachInternalListeners();
	}

	private void attachInternalListeners()
	{
		this.mInternalAliyunVodPlayer.setOnPreparedListener(this.mInternalListenerAdapter);
		this.mInternalAliyunVodPlayer.setOnCompletionListener(this.mInternalListenerAdapter);
		this.mInternalAliyunVodPlayer.setOnVideoSizeChangedListener(this.mInternalListenerAdapter);
		this.mInternalAliyunVodPlayer.setOnErrorListener(this.mInternalListenerAdapter);
		this.mInternalAliyunVodPlayer.setOnInfoListener(this.mInternalListenerAdapter);
		this.mInternalAliyunVodPlayer.setOnRenderingStartListener(this.mInternalListenerAdapter);
		this.mInternalAliyunVodPlayer.setOnSeekCompleteListener(this.mInternalListenerAdapter);
		this.mInternalAliyunVodPlayer.setOnLoadingStatusListener(this.mInternalListenerAdapter);
		this.mInternalAliyunVodPlayer.setOnStateChangedListener(this.mInternalListenerAdapter);
	}

	@Override
	public void setSurface(Surface surface)
	{
		this.mInternalAliyunVodPlayer.setSurface(surface);
	}

	@Override
	public void setDataSource(String path) throws Exception
	{
		if(TextUtils.isEmpty(path)) return;
		UrlSource urlSource = new UrlSource();
		urlSource.setUri(path);
		this.mInternalAliyunVodPlayer.setDataSource(urlSource);
	}

	@Override
	public void setDataSource(FileDescriptor fd) throws Exception
	{
		LogUtil.d("阿里云播放器不支持此设置方法");
	}

	@Override
	public void setDisplay(SurfaceHolder sh)
	{
		Object var2 = this.mInitLock;
		synchronized(this.mInitLock)
		{
			if(!this.mIsReleased)
			{
				this.mInternalAliyunVodPlayer.setDisplay(sh);
			}
		}
	}

	@Override
	public void setPlayingCache(boolean enable, String saveDir, int maxDuration, long maxSize)
	{
		CacheConfig cacheConfig = new CacheConfig();
		cacheConfig.mEnable = enable;
		cacheConfig.mDir = saveDir;
		cacheConfig.mMaxDurationS = maxDuration;
		cacheConfig.mMaxSizeMB = (int)maxSize;
		this.mInternalAliyunVodPlayer.setCacheConfig(cacheConfig);
	}

	@Override
	public void prepareAsync() throws IllegalStateException
	{
		this.mInternalAliyunVodPlayer.prepare();
	}

	@Override
	public void start() throws IllegalStateException
	{
		this.mInternalAliyunVodPlayer.start();
	}

	@Override
	public void pause() throws IllegalStateException
	{
		this.mInternalAliyunVodPlayer.pause();
	}

	@Override
	public void resume() throws IllegalStateException
	{
		this.mInternalAliyunVodPlayer.start();
	}

	@Override
	public void stop() throws IllegalStateException
	{
		this.mInternalAliyunVodPlayer.stop();
	}

	@Override
	public void seekTo(long position)
	{
		this.mInternalAliyunVodPlayer.seekTo((int)position);
	}

	@Override
	public void setMuteMode(boolean var1)
	{
		this.mInternalAliyunVodPlayer.setMute(var1);
	}

	@Override
	public long getDuration()
	{
		try
		{
			return (long)this.mInternalAliyunVodPlayer.getDuration();
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
			return mCurrentPosition;
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
		this.mInternalAliyunVodPlayer.setLoop(var1);
	}

	@Override
	public boolean getLooping()
	{
		return mLooping;
	}

	@Override
	public void setVideoScalingMode(VideoScalingMode var1)
	{
		if(var1 == VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT)
		{
			this.mInternalAliyunVodPlayer.setScaleMode(IPlayer.ScaleMode.SCALE_ASPECT_FIT);
		}
		else if(var1 == VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
		{
			this.mInternalAliyunVodPlayer.setScaleMode(IPlayer.ScaleMode.SCALE_ASPECT_FILL);
		}
	}

	@Override
	public void reset()
	{
		try
		{
			this.mInternalAliyunVodPlayer.reset();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		this.resetListeners();
		this.attachInternalListeners();
	}

	@Override
	public void release()
	{
		this.mIsReleased = true;
		this.mInternalAliyunVodPlayer.release();
		this.resetListeners();
		this.attachInternalListeners();
	}


	@Override
	public Object getInternalPlayer()
	{
		return mInternalAliyunVodPlayer;
	}

	private class CAliyunMediaPlayerPlayerListenerHolder implements IPlayer.OnPreparedListener, IPlayer.OnCompletionListener,IPlayer.OnStateChangedListener, IPlayer.OnVideoSizeChangedListener,IPlayer.OnLoadingStatusListener, IPlayer.OnErrorListener, IPlayer.OnRenderingStartListener, IPlayer.OnInfoListener, IPlayer.OnSeekCompleteListener
	{
		public final WeakReference<CAliyunMediaPlayer> mWeakMediaPlayer;

		public CAliyunMediaPlayerPlayerListenerHolder(CAliyunMediaPlayer mp)
		{
			this.mWeakMediaPlayer = new WeakReference(mp);
		}


		@Override
		public void onCompletion()
		{
			CAliyunMediaPlayer self = (CAliyunMediaPlayer)this.mWeakMediaPlayer.get();
			if(self != null)
			{
				CAliyunMediaPlayer.this.notifyOnCompletion();
			}
		}

		@Override
		public void onPrepared()
		{
			CAliyunMediaPlayer self = (CAliyunMediaPlayer)this.mWeakMediaPlayer.get();
			if(self != null)
			{
				CAliyunMediaPlayer.this.notifyOnPrepared();
			}
		}

		@Override
		public void onVideoSizeChanged(int i, int i1)
		{
			CAliyunMediaPlayer self = (CAliyunMediaPlayer)this.mWeakMediaPlayer.get();
			if(self != null)
			{
				CAliyunMediaPlayer.this.notifyOnVideoSizeChanged(i, i1);
			}
		}


		@Override
		public void onSeekComplete()
		{
			CAliyunMediaPlayer self = (CAliyunMediaPlayer)this.mWeakMediaPlayer.get();
			if(self != null)
			{
				CAliyunMediaPlayer.this.notifyOnSeekComplete();
			}
		}

		@Override
		public void onError(ErrorInfo errorInfo)
		{
			CAliyunMediaPlayer self = (CAliyunMediaPlayer)this.mWeakMediaPlayer.get();
			if(self != null)
			{
				CAliyunMediaPlayer.this.notifyOnError(errorInfo.getCode(), errorInfo.getExtra());
			}
		}

		@Override
		public void onInfo(InfoBean infoBean)
		{
			CAliyunMediaPlayer self = (CAliyunMediaPlayer)this.mWeakMediaPlayer.get();
			int defaultCode = IMediaPlayer.C_MEDIA_INFO_DEFAULT;
			InfoCode code = infoBean.getCode();
			if(code == InfoCode.LoopingStart)
			{
				if(self != null)
				{
					CAliyunMediaPlayer.this.notifyOnCircleStart();
				}
			}
			else if(code == InfoCode.CurrentPosition)
			{
				mCurrentPosition = infoBean.getExtraValue();
			}
			if(self != null)
			{
				CAliyunMediaPlayer.this.notifyOnInfo(defaultCode, null);
			}
		}

		@Override
		public void onRenderingStart()
		{
			CAliyunMediaPlayer self = (CAliyunMediaPlayer)this.mWeakMediaPlayer.get();
			if(self != null)
			{
				CAliyunMediaPlayer.this.notifyOnFirstFrameStart();
				CAliyunMediaPlayer.this.notifyOnInfo(IMediaPlayer.C_MEDIA_INFO_VIDEO_RENDERING_START, null);
			}
		}

		@Override
		public void onLoadingBegin()
		{
			LogUtil.d("onLoadingBegin");
			CAliyunMediaPlayer self = (CAliyunMediaPlayer)this.mWeakMediaPlayer.get();
			int defaultCode = IMediaPlayer.C_MEDIA_INFO_BUFFERING_START;
			if(self != null)
			{
				CAliyunMediaPlayer.this.notifyOnInfo(defaultCode, null);
			}
		}

		@Override
		public void onLoadingProgress(int i, float v)
		{
//			LogUtil.d("onLoadingProgress");
//			CAliyunMediaPlayer self = (CAliyunMediaPlayer)this.mWeakMediaPlayer.get();
//			int defaultCode = IMediaPlayer.C_MEDIA_INFO_BUFFERING_START;
//			if(self != null)
//			{
//				CAliyunMediaPlayer.this.notifyOnInfo(defaultCode, null);
//			}
		}

		@Override
		public void onLoadingEnd()
		{
			LogUtil.d("onLoadingEnd");
			CAliyunMediaPlayer self = (CAliyunMediaPlayer)this.mWeakMediaPlayer.get();
			int defaultCode = IMediaPlayer.C_MEDIA_INFO_BUFFERING_END;
			if(self != null)
			{
				CAliyunMediaPlayer.this.notifyOnInfo(defaultCode, null);
			}
		}

		@Override
		public void onStateChanged(int i)
		{

		}
	}

	public AliPlayer getInternalAliyunVodPlayer()
	{
		return mInternalAliyunVodPlayer;
	}
}
