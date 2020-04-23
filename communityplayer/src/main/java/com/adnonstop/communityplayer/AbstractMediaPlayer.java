package com.adnonstop.communityplayer;

import com.adnonstop.communityplayer.myinterface.IMediaPlayer;

/**
 * author:tdn
 * time:2018/12/29
 * description:抽象播放器，具体的播放器需继承此类
 * {@link  CAndroidMediaPlayer}
 * {@link CAliyunMediaPlayer}
 */
public abstract class AbstractMediaPlayer implements IMediaPlayer
{
	private OnPreparedListener mOnPreparedListener;
	private OnVideoSizeChangedListener mOnVideoSizeChangedListener;
	private OnCompletionListener mOnCompletionListener;
	private OnErrorListener mOnErrorListener;
	private OnInfoListener mOnInfoListener;
	private OnFirstFrameStartListener mOnFirstFrameStartListener;
	private OnCircleStartListener mOnCircleStartListener;
	private OnSeekCompleteListener mOnSeekCompleteListener;


	public AbstractMediaPlayer()
	{
	}

	public final void setOnPreparedListener(OnPreparedListener onPreparedListener)
	{
		mOnPreparedListener = onPreparedListener;
	}

	public final void setOnVideoSizeChangedListener(OnVideoSizeChangedListener onVideoSizeChangedListener)
	{
		mOnVideoSizeChangedListener = onVideoSizeChangedListener;
	}


	public final void setOnCompletionListener(OnCompletionListener onCompletionListener)
	{
		mOnCompletionListener = onCompletionListener;
	}


	public final void setOnErrorListener(OnErrorListener onErrorListener)
	{
		mOnErrorListener = onErrorListener;
	}

	public final void setOnInfoListener(OnInfoListener onInfoListener)
	{
		mOnInfoListener = onInfoListener;
	}


	public final void setOnFirstFrameStartListener(OnFirstFrameStartListener onFirstFrameStartListener)
	{
		mOnFirstFrameStartListener = onFirstFrameStartListener;
	}

	public final void setOnCircleStartListener(OnCircleStartListener onCircleStartListener)
	{
		mOnCircleStartListener = onCircleStartListener;
	}

	public final void setOnSeekCompleteListener(OnSeekCompleteListener onSeekCompleteListener)
	{
		mOnSeekCompleteListener = onSeekCompleteListener;
	}


	public void resetListeners()
	{
		this.mOnPreparedListener = null;
		this.mOnVideoSizeChangedListener = null;
		this.mOnCompletionListener = null;
		this.mOnErrorListener = null;
		this.mOnInfoListener = null;
		this.mOnFirstFrameStartListener = null;
		this.mOnCircleStartListener = null;
	}


	protected final void notifyOnPrepared()
	{
		if(this.mOnPreparedListener != null)
		{
			this.mOnPreparedListener.onPrepared(this);
		}
	}

	protected final void notifyOnVideoSizeChanged(int width, int height)
	{
		if(this.mOnVideoSizeChangedListener != null)
		{
			this.mOnVideoSizeChangedListener.onVideoSizeChanged(this, width, height);
		}
	}

	protected final void notifyOnCompletion()
	{
		if(this.mOnCompletionListener != null)
		{
			this.mOnCompletionListener.onCompletion(this);
		}
	}

	protected final boolean notifyOnError(Object what, Object extra)
	{
		return this.mOnErrorListener != null && this.mOnErrorListener.onError(this, what, extra);
	}

	protected final boolean notifyOnInfo(int what, Object extra)
	{
		return this.mOnInfoListener != null && this.mOnInfoListener.onInfo(this, what, extra);
	}

	protected final void notifyOnFirstFrameStart()
	{
		if(this.mOnCompletionListener != null)
		{
			this.mOnFirstFrameStartListener.onFirstFrameStart(this);
		}
	}

	protected final void notifyOnCircleStart()
	{
		if(this.mOnCircleStartListener != null)
		{
			this.mOnCircleStartListener.onCircleStartListener(this);
		}
	}

	protected final void notifyOnSeekComplete()
	{
		if(this.mOnSeekCompleteListener != null)
		{
			this.mOnSeekCompleteListener.onSeekComplete(this);
		}
	}


}
