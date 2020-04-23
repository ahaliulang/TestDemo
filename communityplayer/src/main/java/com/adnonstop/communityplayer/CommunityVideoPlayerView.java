package com.adnonstop.communityplayer;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.adnonstop.communityplayer.bean.PlayingCacheInfo;
import com.adnonstop.communityplayer.bean.VideoPlayInfo;
import com.adnonstop.communityplayer.controller.CommunityPlayerController;
import com.adnonstop.communityplayer.controller.PublishPlayerController;
import com.adnonstop.communityplayer.listener.IVideoPlayerListener;
import com.adnonstop.communityplayer.myinterface.IAutoPlay;
import com.adnonstop.communityplayer.myinterface.ICommunityVideoPlayer;
import com.adnonstop.communityplayer.myinterface.IMediaPlayer;
import com.adnonstop.communityplayer.util.LogUtil;
import com.adnonstop.communityplayer.util.NetUtils;
import com.adnonstop.communityplayer.util.SPUtils;
import com.adnonstop.communityplayer.util.VideoPlayerUtil;

import java.io.FileDescriptor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * author:tdn
 * time:2018/12/25
 * description: 播放器view
 */
public class CommunityVideoPlayerView extends FrameLayout implements ICommunityVideoPlayer, TextureView.SurfaceTextureListener
{
	/**
	 * 播放错误
	 **/
	public static final int STATE_ERROR = -1;
	/**
	 * 播放未开始
	 **/
	public static final int STATE_IDLE = 0;
	/**
	 * 播放准备中
	 **/
	public static final int STATE_PREPARING = 1;
	/**
	 * 播放准备就绪
	 **/
	public static final int STATE_PREPARED = 2;
	/**
	 * 正在播放
	 **/
	public static final int STATE_PLAYING = 3;
	/**
	 * 暂停播放
	 **/
	public static final int STATE_PAUSED = 4;
	/**
	 * 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，缓冲区数据足够后恢复播放)
	 **/
	public static final int STATE_BUFFERING_PLAYING = 5;
	/**
	 * 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，此时暂停播放器，继续缓冲，缓冲区数据足够后恢复暂停
	 **/
	public static final int STATE_BUFFERING_PAUSED = 6;
	/**
	 * 播放完成
	 **/
	public static final int STATE_COMPLETED = 7;

	/**
	 * 第一帧开始
	 */
	public static final int STATE_FIRST_FRAME_START = 8;


	protected int mCurrentState = STATE_IDLE;
	private ScreenMode mCurrentScreenMode = ScreenMode.Normal;
	private PlayerType mPlayerType = PlayerType.Aliyun;

	protected Context mContext;
	private AudioManager mAudioManager;
	protected IMediaPlayer mMediaPlayer;
	private FrameLayout mContainer;
	private AdaptTextureView mTextureView;
	protected BaseVideoPlayerController mController;
	private SurfaceTexture mSurfaceTexture;
	private Surface mSurface;
	private String mVideoUrl; //视频 url
	private String mCoverUrl; // 封面 url
	private FileDescriptor mFileDescriptor;//视频文件描述符
	private int mCoverWidth, mCoverHeight;//视频宽高
	private VideoPlayInfo mVideoPlayInfo;//视频播放信息
	private boolean continueFromLatestPosition = false;
	private long skipToPosition;
	private boolean mVoiceMute;//视频是否静音，true 静音，false 反之
	protected int mCurrentPageID;
	private boolean mAutoPlay;//是否自动播放，true 是，false 反之
	private boolean mLooping = true;//是否是循环播放，true 是，false 反之
	private IMediaPlayer.VideoScalingMode mScalingMode = IMediaPlayer.VideoScalingMode.VIDEO_SCALING_MODE_DEFAULT;

	/***************监听*******************************/
	private IVideoPlayerListener.OnScreenModeListener mOnScreenModeListener;
	private IVideoPlayerListener.OnProgressListener mOnProgressListener;
	private IVideoPlayerListener.OnCircleStartListener mOnCircleStartListener;
	private IVideoPlayerListener.OnSeekCompleteListener mOnSeekCompleteListener;
	private IVideoPlayerListener.OnFirstFrameStartListener mOnFirstFrameStartListener;
	/***************监听*******************************/


	private boolean mTypeBanner = false;//类型为商业banner
	private PlayingCacheInfo mPlayingCacheInfo;

	public CommunityVideoPlayerView(@NonNull Context context, @Nullable AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public CommunityVideoPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		mContext = context;
		init();
	}

	private void init()
	{
		mContainer = new FrameLayout(mContext);
		mContainer.setBackgroundColor(Color.BLACK);
		LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		this.addView(mContainer, params);
	}

	public FrameLayout getContainer()
	{
		return mContainer;
	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1)
	{
		if(mSurfaceTexture == null)
		{
			mSurfaceTexture = surfaceTexture;
			openMediaPlayer();
		}
		else
		{
			mTextureView.setSurfaceTexture(mSurfaceTexture);
		}
	}


	public CommunityVideoPlayerView(@NonNull Context context)
	{
		this(context, null);
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1)
	{

	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture)
	{

		//OPPO 有些机型有问题，
		if("OPPO".equalsIgnoreCase(android.os.Build.BRAND) && "OPPO A59m".equals(Build.MODEL))
		{
			releasePlayer(false);
			return true;
		}
		return mSurfaceTexture == null;
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture)
	{
	}

	@Override
	public void setVideoUrl(String url)
	{
		mVideoUrl = url;
	}

	@Override
	public void setVideoFileDescriptor(FileDescriptor fileDescriptor)
	{
		mFileDescriptor = fileDescriptor;
	}

	@Override
	public void setCoverUrl(String url)
	{
		mCoverUrl = url;
	}

	@Override
	public void setVideoPlayInfo(VideoPlayInfo videoPlayInfo)
	{
		mVideoPlayInfo = videoPlayInfo;
		if(videoPlayInfo != null)
		{
			mVideoUrl = videoPlayInfo.videoUrl;
			mCoverUrl = videoPlayInfo.coverUrl;
			mCoverWidth = videoPlayInfo.coverWidth;
			mCoverHeight = videoPlayInfo.coverHeight;
		}
	}

	@Override
	public void setPageID(int id)
	{
		mCurrentPageID = id;
	}

	/**
	 * 获取页面id
	 *
	 * @return
	 */
	public int getPageID()
	{
		return mCurrentPageID;
	}

	protected void openMediaPlayer()
	{
		// 屏幕常亮
		mContainer.setKeepScreenOn(true);
		if(mMediaPlayer == null)
		{
			initMediaPlayer();
		}
		// 设置监听
		if(mMediaPlayer != null)
		{
			mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
			mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
			mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
			mMediaPlayer.setOnErrorListener(mOnErrorListener);
			mMediaPlayer.setOnInfoListener(mOnInfoListener);
			mMediaPlayer.setOnFirstFrameStartListener(mOnInnerFirstFrameStartListener);
			mMediaPlayer.setOnCircleStartListener(mOnInnerCircleStartListener);
			mMediaPlayer.setOnSeekCompleteListener(mOnInnerSeekCompleteListener);
			// 设置dataSource
			try
			{


				if(!TextUtils.isEmpty(mVideoUrl))
				{
					mMediaPlayer.setDataSource(mVideoUrl);
				}
				else if(mFileDescriptor != null)
				{
					mMediaPlayer.setDataSource(mFileDescriptor);
				}
				else
				{
					return;
				}
				if(mSurface == null)
				{
					mSurface = new Surface(mSurfaceTexture);
				}
				mMediaPlayer.setSurface(mSurface);
				mMediaPlayer.prepareAsync();
				mCurrentState = STATE_PREPARING;
				mController.onPlayStateChanged(mCurrentState);
				LogUtil.d("STATE_PREPARING");
			}
			catch(Exception e)
			{
				e.printStackTrace();
				LogUtil.e("打开播放器发生错误", e);
			}
		}
	}

	@Override
	public void start(boolean auto)
	{
		LogUtil.d("Start--->auto:" + auto);
		CommunityVideoPlayerManager.instance().setPageID(mCurrentPageID).setCurrentVideoPlayer(CommunityVideoPlayerView.this);
		try
		{
			//如果记录的状态下为暂停状态
			if(CommunityVideoPlayerManager.instance().getPauseSparseArray().get(mCurrentPageID))
			{
				return;
			}
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}

		boolean autoPlayIn4G = (boolean)SPUtils.get(getContext(), SPUtils.GLOBAL_AUTO_PLAY_IN_4G, false);
		if(NetUtils.getNetType(getContext()) == NetUtils.TYPE_MOBILE && !autoPlayIn4G && PlayerType.Native != getPlayerType())
		{
			return;
		}
		trueStart();
	}


	@Override
	public void start(long position)
	{
		skipToPosition = position;
		start(true);
	}

	private IMediaPlayer.OnPreparedListener mOnPreparedListener = new IMediaPlayer.OnPreparedListener()
	{
		@Override
		public void onPrepared(IMediaPlayer mp)
		{
			mCurrentState = STATE_PREPARED;

			//系统声音没有声音
			if(mAudioManager != null && mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0)
			{
				SPUtils.put(mContext, SPUtils.VIDEO_VOICE_MUTE, true);
				if(mController != null)
				{
					mController.onMuteModeChanged(true, true);
				}
			}
			else
			{
				if(mController instanceof PublishPlayerController)
				{
					mController.onMuteModeChanged(true, false);
				}
				else if(mController instanceof CommunityPlayerController)
				{
					boolean globalMute = (boolean)SPUtils.get(mContext, SPUtils.GLOBAL_VIDEO_VOICE_MUTE, false);
					if(mTypeBanner)
					{
						if(globalMute)
						{
							mController.onMuteModeChanged(true, true);
						}
						else
						{
							//boolean mute = (boolean)SPUtils.get(getContext(), SPUtils.VIDEO_VOICE_MUTE, false);
							mController.onMuteModeChanged(false, true);
						}
					}
					else
					{
						if(globalMute)
						{
							mController.onMuteModeChanged(true, true);
						}
						else
						{
							mController.onMuteModeChanged(false, true);
						}

					}
				}

			}

			mController.onPlayStateChanged(mCurrentState);
			LogUtil.d("onPrepared ——> STATE_PREPARED");
			if(mMediaPlayer != null)
			{
				mMediaPlayer.start();
				// 从上次的保存位置播放
				if(continueFromLatestPosition)
				{
					long savedPlayPosition = SPUtils.getPlayerPosition(mContext, mVideoUrl);
					mMediaPlayer.seekTo((int)savedPlayPosition);
				}
				// 跳到指定位置播放
				if(skipToPosition != 0)
				{
					mMediaPlayer.seekTo((int)skipToPosition);
				}
			}
		}

	};

	@Override
	public void pause()
	{
		try
		{
			if(mMediaPlayer != null)
			{
				if(mCurrentState == STATE_PLAYING)
				{
					mMediaPlayer.pause();
					mCurrentState = STATE_PAUSED;
					mController.onPlayStateChanged(mCurrentState);
					LogUtil.d("STATE_PAUSED");
				}
				if(mCurrentState == STATE_BUFFERING_PLAYING)
				{
					mMediaPlayer.pause();
					mCurrentState = STATE_BUFFERING_PAUSED;
					mController.onPlayStateChanged(mCurrentState);
					LogUtil.d("STATE_BUFFERING_PAUSED");
				}
			}
		}
		catch(IllegalStateException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void seekTo(long position)
	{
		if(mMediaPlayer != null && position >= 0 && position <= mMediaPlayer.getDuration())
		{
			mMediaPlayer.seekTo((int)position);
		}
	}

	@Override
	public void setVolume(int volume)
	{
		if(mAudioManager != null)
		{
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
		}
	}

	@Override
	public void restart()
	{
		try
		{
			boolean globalMute = (boolean)SPUtils.get(mContext, SPUtils.GLOBAL_VIDEO_VOICE_MUTE, false);
			if(globalMute)
			{
				if(mController != null)
				{
					mController.onMuteModeChanged(true, true);
				}
			}
			else
			{
				//boolean mute = (boolean)SPUtils.get(getContext(), SPUtils.VIDEO_VOICE_MUTE, false);
				if(mController != null)
				{
					mController.onMuteModeChanged(false, true);
				}
			}

			boolean autoPlayIn4G = (boolean)SPUtils.get(getContext(), SPUtils.GLOBAL_AUTO_PLAY_IN_4G, false);
			if(NetUtils.getNetType(getContext()) == NetUtils.TYPE_MOBILE && !autoPlayIn4G)
			{
				return;
			}

			if(mMediaPlayer != null)
			{
				if(mCurrentState == STATE_PAUSED)
				{
					mMediaPlayer.resume();
					mCurrentState = STATE_PLAYING;
					mController.onPlayStateChanged(mCurrentState);
					LogUtil.d("STATE_PLAYING");
				}
				else if(mCurrentState == STATE_BUFFERING_PAUSED)
				{
					mMediaPlayer.resume();
					mCurrentState = STATE_BUFFERING_PLAYING;
					mController.onPlayStateChanged(mCurrentState);
					LogUtil.d("STATE_BUFFERING_PLAYING");
				}
				else if(mCurrentState == STATE_COMPLETED || mCurrentState == STATE_ERROR)
				{
					mMediaPlayer.reset();
					openMediaPlayer();
				}
				else
				{
					LogUtil.d("CommunityVideoPlayerView在mCurrentState == " + mCurrentState + "时不能调用restart()方法.");
				}
			}
		}
		catch(IllegalStateException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void continueFromLatestPosition(boolean latestPosition)
	{
		this.continueFromLatestPosition = latestPosition;
	}

	@Override
	public boolean isIdle()
	{
		return mCurrentState == STATE_IDLE;
	}

	@Override
	public boolean isPreparing()
	{
		return mCurrentState == STATE_PREPARING;
	}

	@Override
	public boolean isPrepared()
	{
		return mCurrentState == STATE_PREPARED;
	}

	@Override
	public boolean isPlaying()
	{
		/**
		 * 部分机型系统播放器循环播放，执行{@link CommunityVideoPlayerView#mOnCompletionListener}后不会再调用相关的接口
		 * 导致mCurrentState 状态不会改变，然后暂停不了
		 */
		if(mMediaPlayer != null && mMediaPlayer.getInternalPlayer() instanceof MediaPlayer)
		{
			mCurrentState = ((MediaPlayer)mMediaPlayer.getInternalPlayer()).isPlaying() == true ? STATE_PLAYING : mCurrentState;
		}
		return mCurrentState == STATE_PLAYING;
	}

	@Override
	public boolean isBufferingPlaying()
	{
		return mCurrentState == STATE_BUFFERING_PLAYING;
	}

	@Override
	public boolean isBufferingPaused()
	{
		return mCurrentState == STATE_BUFFERING_PAUSED;
	}

	@Override
	public boolean isPaused()
	{
		return mCurrentState == STATE_PAUSED;
	}

	@Override
	public boolean isError()
	{
		return mCurrentState == STATE_ERROR;
	}

	@Override
	public boolean isCompleted()
	{
		return mCurrentState == STATE_COMPLETED;
	}

	@Override
	public boolean isLooping()
	{
		if(mMediaPlayer != null)
		{
			return mMediaPlayer.getLooping();
		}
		return false;
	}

	@Override
	public boolean isFullScreen()
	{
		return mCurrentScreenMode == ScreenMode.Full;
	}

	@Override
	public boolean isTinyWindow()
	{    //TODO
		return false;
	}

	@Override
	public boolean isNormal()
	{
		return mCurrentScreenMode == ScreenMode.Normal;
	}

	@Override
	public boolean isAutoPlay()
	{
		return mAutoPlay;
	}

	@Override
	public boolean isMute()
	{
		return mVoiceMute;
	}

	@Override
	public int getMaxVolume()
	{
		if(mAudioManager != null)
		{
			return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		}
		return 0;
	}

	@Override
	public int getVolume()
	{
		if(mAudioManager != null)
		{
			return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		}
		return 0;
	}

	@Override
	public long getDuration()
	{
		return mMediaPlayer != null ? mMediaPlayer.getDuration() : 0;
	}

	@Override
	public long getCurrentPosition()
	{
		return mMediaPlayer != null ? mMediaPlayer.getCurrentPosition() : 0;
	}

	@Override
	public void setMuteMode(boolean mute, boolean save)
	{
		mVoiceMute = mute;
		if(mMediaPlayer != null)
		{
			mMediaPlayer.setMuteMode(mute);
		}
		if(mContext != null && !(mContext instanceof IAutoPlay) && save)
		{
			SPUtils.put(mContext, SPUtils.VIDEO_VOICE_MUTE, mute);
		}
	}


	/**
	 * 退出全屏，移除mTextureView和mController，并添加到非全屏的容器中。
	 * 切换竖屏时需要在manifest的activity标签下添加android:configChanges="orientation|keyboardHidden|screenSize"配置，
	 * 以避免Activity重新走生命周期.
	 *
	 * @return true退出全屏.
	 */
	@Override
	public boolean exitFullScreen()
	{
		if(mOnScreenModeListener != null)
		{
			mOnScreenModeListener.exitFullScreen();
		}
		if(mCurrentScreenMode == ScreenMode.Full)
		{
			VideoPlayerUtil.showActionBar(mContext);
			VideoPlayerUtil.scanForActivity(mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

			if(mMediaPlayer != null)
			{
				//设置屏幕模式为裁剪铺满，不然会有黑边(默认)
				if(mScalingMode == IMediaPlayer.VideoScalingMode.VIDEO_SCALING_MODE_DEFAULT)
				{
					mMediaPlayer.setVideoScalingMode(IMediaPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
				}
				//手动设置了这个值
				else
				{
					mMediaPlayer.setVideoScalingMode(mScalingMode);
				}
			}

			ViewGroup contentView = (ViewGroup)VideoPlayerUtil.scanForActivity(mContext).findViewById(android.R.id.content);
			contentView.removeView(mContainer);
			contentView.setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
			LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			this.addView(mContainer, params);
			mCurrentScreenMode = ScreenMode.Normal;
			mController.onPlayModeChanged(mCurrentScreenMode);
			restart();
			//退出全屏设置亮度为原来 app 的亮度
			WindowManager.LayoutParams windowParams = VideoPlayerUtil.scanForActivity(mContext).getWindow().getAttributes();
			windowParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
			VideoPlayerUtil.scanForActivity(mContext).getWindow().setAttributes(windowParams);
			return true;
		}
		return false;
	}

	@Override
	public void enterTinyWindow()
	{

	}

	@Override
	public boolean exitTinyWindow()
	{
		return false;
	}

	@Override
	public int getBufferPercentage()
	{
		return 0;
	}

	protected void trueStart()
	{
		if(mCurrentState == STATE_IDLE)
		{
			if(mController instanceof CommunityPlayerController)
			{
				((CommunityPlayerController)mController).setCoverColor(Color.parseColor("#00000000"));
			}
			initAudioManager();
			initMediaPlayer();
			initTextureView();
			addTextureView();
		}
		else
		{
			LogUtil.d("CommunityVideoPlayerView 只有在mCurrentState == STATE_IDLE时才能调用start方法." + mCurrentState);
		}
	}


	private void releasePlayer(boolean removeTextureView)
	{
		if(mAudioManager != null)
		{
			mAudioManager.abandonAudioFocus(null);
			mAudioManager = null;
		}
		if(mMediaPlayer != null)
		{
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		if(removeTextureView)
		{
			mContainer.removeView(mTextureView);
		}
		if(mSurface != null)
		{
			mSurface.release();
			mSurface = null;
		}
		if(mSurfaceTexture != null)
		{
			mSurfaceTexture.release();
			mSurfaceTexture = null;
		}
		mCurrentState = STATE_IDLE;
	}

	/**
	 * 全屏，将mContainer(内部包含mTextureView和mController)从当前容器中移除，并添加到android.R.content中.
	 * 切换横屏时需要在manifest的activity标签下添加android:configChanges="orientation|keyboardHidden|screenSize"配置，
	 * 以避免Activity重新走生命周期
	 */
	@Override
	public void enterFullScreen(boolean landscape)
	{
		if(mController == null) return;
		if(mController.getIOnClickTinyScreen() != null)
		{
			mController.getIOnClickTinyScreen().onClickTinyScreen();
			return;
		}
		if(mOnScreenModeListener != null)
		{
			mOnScreenModeListener.enterFullScreen();
		}
		if(mCurrentScreenMode == ScreenMode.Full) return;

		// 隐藏ActionBar、状态栏，并横屏
		VideoPlayerUtil.hideActionBar(mContext);
		if(mCoverWidth > mCoverHeight && landscape)
		{
			VideoPlayerUtil.scanForActivity(mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		else
		{
			VideoPlayerUtil.scanForActivity(mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}


		if(mMediaPlayer != null)
		{
			//设置屏幕模式为裁剪铺满，不然会有黑边(默认)
			if(mScalingMode == IMediaPlayer.VideoScalingMode.VIDEO_SCALING_MODE_DEFAULT)
			{
				mMediaPlayer.setVideoScalingMode(IMediaPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT);
			}
			//手动设置了这个值
			else
			{
				mMediaPlayer.setVideoScalingMode(mScalingMode);
			}
		}


		ViewGroup contentView = (ViewGroup)VideoPlayerUtil.scanForActivity(mContext).findViewById(android.R.id.content);
		contentView.setFocusableInTouchMode(true);
		contentView.setFocusable(true);
		if(mCurrentScreenMode == ScreenMode.Normal)
		{
			this.removeView(mContainer);
		}
		LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		contentView.addView(mContainer, params);
		contentView.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
		mCurrentScreenMode = ScreenMode.Full;
		mController.onPlayModeChanged(mCurrentScreenMode);
		mController.onMuteModeChanged(false, true);
		LogUtil.d("MODE_FULL_SCREEN");
	}

	public BaseVideoPlayerController getController()
	{
		return mController;
	}

	private void initAudioManager()
	{
		if(mAudioManager == null)
		{
			mAudioManager = (AudioManager)getContext().getSystemService(Context.AUDIO_SERVICE);
			mAudioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
		}
	}

	private void initMediaPlayer()
	{
		if(mMediaPlayer == null)
		{
			if(mPlayerType == PlayerType.Native)
			{
				mMediaPlayer = new CAndroidMediaPlayer();
			}
			else
			{
				mMediaPlayer = new CAliyunMediaPlayer(mContext);
			}
			mMediaPlayer.setLooping(mLooping);
			if(mPlayingCacheInfo != null)
			{
				mMediaPlayer.setPlayingCache(mPlayingCacheInfo.isEnable(), mPlayingCacheInfo.getSaveDir(), mPlayingCacheInfo.getMaxDuration(), mPlayingCacheInfo.getMaxSize());
			}
		}
	}

	private void initTextureView()
	{
		if(mTextureView == null)
		{
			mTextureView = new AdaptTextureView(mContext);
			mTextureView.setSurfaceTextureListener(this);
		}
	}

	private void addTextureView()
	{
		mContainer.removeView(mTextureView);
		LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.CENTER;
		mContainer.addView(mTextureView, 0, params);
	}

	public void setController(BaseVideoPlayerController controller)
	{
		mContainer.removeView(mController);
		mController = controller;
		mController.reset();
		mController.setCommunityVideoPlayer(this);
		mController.loadCoverImage(mCoverUrl);
		//mVoiceMute = (boolean)SPUtils.get(mContext, SPUtils.VIDEO_VOICE_MUTE, false);
		//mController.onMuteModeChanged(false);
		if(mController instanceof CommunityPlayerController && mOnProgressListener != null)
		{
			((CommunityPlayerController)mController).setOnProgressListener(mOnProgressListener);
		}
		//播放新的视频开启声音
		/*if(mContext != null)
		{
			SPUtils.put(mContext, SPUtils.VIDEO_VOICE_MUTE, false);
		}*/
		LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		mContainer.addView(mController, params);
	}

	private IMediaPlayer.OnFirstFrameStartListener mOnInnerFirstFrameStartListener = new IMediaPlayer.OnFirstFrameStartListener()
	{
		@Override
		public void onFirstFrameStart(IMediaPlayer mp)
		{
			mCurrentState = STATE_FIRST_FRAME_START;
			mController.onPlayStateChanged(mCurrentState);
			if(mOnFirstFrameStartListener != null)
			{
				mOnFirstFrameStartListener.onFirstFrameStart();
			}
		}
	};

	private IMediaPlayer.OnCircleStartListener mOnInnerCircleStartListener = new IMediaPlayer.OnCircleStartListener()
	{
		@Override
		public void onCircleStartListener(IMediaPlayer mp)
		{
			if(mOnCircleStartListener != null)
			{
				mOnCircleStartListener.onCircleStart();
			}

		}
	};


	private IMediaPlayer.OnSeekCompleteListener mOnInnerSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener()
	{
		@Override
		public void onSeekComplete(IMediaPlayer mp)
		{
			if(mOnSeekCompleteListener != null)
			{
				mOnSeekCompleteListener.onSeekComplete();
			}
		}
	};

	@Override
	public void release()
	{
		LogUtil.d("Release");
		// 保存播放位置
		if(isPlaying() || isBufferingPlaying() || isBufferingPaused() || isPaused())
		{
			VideoPlayerUtil.savePlayPosition(mContext, mVideoUrl, getCurrentPosition());
		}
		else if(isCompleted())
		{
			VideoPlayerUtil.savePlayPosition(mContext, mVideoUrl, 0);
		}
		// 退出全屏或小窗口
		if(isFullScreen())
		{
			exitFullScreen();
		}
		if(isTinyWindow())
		{
			exitTinyWindow();
		}
		mCurrentScreenMode = ScreenMode.Normal;

		// 释放播放器
		releasePlayer(true);

		// 恢复控制器
		if(mController != null)
		{
			mController.reset();
		}
		//Runtime.getRuntime().gc();

		if(mController instanceof CommunityPlayerController)
		{
			if(NetUtils.getNetType(getContext()) == NetUtils.TYPE_MOBILE)
			{
				boolean autoPlayIn4G = (boolean)SPUtils.get(getContext(), SPUtils.GLOBAL_AUTO_PLAY_IN_4G, false);
				if(autoPlayIn4G)
				{

					((CommunityPlayerController)mController).setCoverColor(Color.parseColor("#00000000"));
					((CommunityPlayerController)mController).setPlayIconVisible(false);
				}
				else
				{
					((CommunityPlayerController)mController).setCoverColor(Color.parseColor("#4d000000"));
					((CommunityPlayerController)mController).setPlayIconVisible(true);
				}
			}
			else
			{
				((CommunityPlayerController)mController).setCoverColor(Color.parseColor("#00000000"));
			}
		}
		CommunityVideoPlayerManager.instance().releasePageID(mCurrentPageID);
	}


	private IMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener()
	{
		@Override
		public void onVideoSizeChanged(IMediaPlayer mp, int width, int height)
		{
			if(mPlayerType == PlayerType.Native)
			{
				mTextureView.adaptVideoSize(width, height);
			}
			if(mController != null)
			{
				mController.onVideoSizeChange(width, height);
			}
			LogUtil.d("onVideoSizeChanged ——> width：" + width + "， height：" + height);
		}
	};


	private IMediaPlayer.OnCompletionListener mOnCompletionListener = new IMediaPlayer.OnCompletionListener()
	{
		@Override
		public void onCompletion(IMediaPlayer mp)
		{
			mCurrentState = STATE_COMPLETED;
			LogUtil.d("onCompletion ——> STATE_COMPLETED");
			// 清除屏幕常亮
			//mContainer.setKeepScreenOn(false);
			//解决某些机型如华为 P8 状态播放完成状态不在改变的bug
			if(mMediaPlayer != null && mMediaPlayer.getLooping() && mPlayerType == PlayerType.Native)
			{
				mCurrentState = STATE_FIRST_FRAME_START;
			}
			mController.onPlayStateChanged(mCurrentState);
		}
	};


	private IMediaPlayer.OnErrorListener mOnErrorListener = new IMediaPlayer.OnErrorListener()
	{
		@Override
		public boolean onError(IMediaPlayer mp, Object what, Object extra)
		{
			mCurrentState = STATE_ERROR;
			mController.onPlayStateChanged(mCurrentState);
			return true;
		}
	};


	private IMediaPlayer.OnInfoListener mOnInfoListener = new IMediaPlayer.OnInfoListener()
	{
		@Override
		public boolean onInfo(IMediaPlayer mp, int what, Object extra)
		{
			if(what == IMediaPlayer.C_MEDIA_INFO_VIDEO_RENDERING_START)
			{
				// 播放器开始渲染
				mCurrentState = STATE_PLAYING;
				mController.onPlayStateChanged(mCurrentState);
			}
			else if(what == IMediaPlayer.C_MEDIA_INFO_BUFFERING_START)
			{
				// MediaPlayer暂时不播放，以缓冲更多的数据
				if(mCurrentState == STATE_PAUSED || mCurrentState == STATE_BUFFERING_PAUSED)
				{
					mCurrentState = STATE_BUFFERING_PAUSED;
				}
				else
				{
					mCurrentState = STATE_BUFFERING_PLAYING;
				}
				mController.onPlayStateChanged(mCurrentState);
			}
			else if(what == IMediaPlayer.C_MEDIA_INFO_BUFFERING_END)
			{
				// 填充缓冲区后，MediaPlayer恢复播放/暂停
				if(mCurrentState == STATE_BUFFERING_PLAYING)
				{
					mCurrentState = STATE_PLAYING;
					mController.onPlayStateChanged(mCurrentState);
				}
				if(mCurrentState == STATE_BUFFERING_PAUSED)
				{
					mCurrentState = STATE_PAUSED;
					mController.onPlayStateChanged(mCurrentState);
				}
			}
			return false;
		}

	};


	/**
	 * 设置播放器类型
	 *
	 * @param playerType or MediaPlayer.
	 */
	public void setPlayerType(PlayerType playerType)
	{
		mPlayerType = playerType;
	}

	public PlayerType getPlayerType()
	{
		return mPlayerType;
	}

	/**
	 * 设置是否自动播放，第一次进入页面视频没有自动播放可调用这个方法
	 *
	 * @param autoPlay
	 */
	public void setAutoPlay(boolean autoPlay)
	{
		mAutoPlay = autoPlay;
	}


	/**
	 * 设置是否循环播放
	 *
	 * @param looping true 循环播放，false 反之
	 */
	public void setLooping(boolean looping)
	{
		mLooping = looping;
		if(mMediaPlayer != null)
		{
			mMediaPlayer.setLooping(mLooping);
		}
	}


	/**
	 * 设置视频裁剪模式,支持适应大小，裁剪铺满两种模式
	 * {@link IMediaPlayer.VideoScalingMode}
	 *
	 * @param var1
	 */
	public void setVideoScalingMode(IMediaPlayer.VideoScalingMode var1)
	{
		mScalingMode = var1;
		if(mMediaPlayer != null && mScalingMode != IMediaPlayer.VideoScalingMode.VIDEO_SCALING_MODE_DEFAULT)
		{
			mMediaPlayer.setVideoScalingMode(mScalingMode);
		}
	}

	/**
	 * 设置视频模式切换监听
	 *
	 * @param onScreenModeListener
	 */
	public void setOnScreenModeListener(IVideoPlayerListener.OnScreenModeListener onScreenModeListener)
	{
		mOnScreenModeListener = onScreenModeListener;
	}


	/**
	 * 设置视频进度监听
	 *
	 * @param onProgressListener
	 */
	public void setOnProgressListener(IVideoPlayerListener.OnProgressListener onProgressListener)
	{
		mOnProgressListener = onProgressListener;
		if(mController instanceof CommunityPlayerController)
		{
			((CommunityPlayerController)mController).setOnProgressListener(mOnProgressListener);
		}
	}

	/**
	 * 设置循环播放开始监听
	 *
	 * @param onCircleStartListener
	 */
	protected void setOnCircleStartListener(IVideoPlayerListener.OnCircleStartListener onCircleStartListener)
	{
		mOnCircleStartListener = onCircleStartListener;
	}

	/**
	 * 设置首帧播放时间
	 *
	 * @param onFirstFrameStartListener
	 */
	protected void setOnFirstFrameStartListener(IVideoPlayerListener.OnFirstFrameStartListener onFirstFrameStartListener)
	{
		mOnFirstFrameStartListener = onFirstFrameStartListener;
	}


	public void setOnSeekCompleteListener(IVideoPlayerListener.OnSeekCompleteListener onSeekCompleteListener)
	{
		mOnSeekCompleteListener = onSeekCompleteListener;
	}

	public void setTypeBanner(boolean typeBanner)
	{
		mTypeBanner = typeBanner;
	}

	public VideoPlayInfo getVideoPlayInfo()
	{
		return mVideoPlayInfo;
	}

	/**
	 * 设置视频缓存相关
	 *
	 * @param enable      是否可以边播边存。
	 * @param saveDir     缓存的目录（绝对路径）
	 * @param maxDuration 能缓存的单个视频最大长度（单位：秒）。如果单个视频超过这个值，就不缓存。
	 * @param maxSize     缓存目录的所有缓存文件的总的最大大小（单位：MB）。如果超过则删除最旧文件，如果还是不够，则不缓存。
	 */
	public void setPlayingCache(boolean enable, String saveDir, int maxDuration, long maxSize)
	{
		mPlayingCacheInfo = new PlayingCacheInfo(enable, saveDir, maxDuration, maxSize);
		if(mMediaPlayer != null)
		{
			mMediaPlayer.setPlayingCache(enable, saveDir, maxDuration, maxSize);
		}
	}

	@Override
	protected void onAttachedToWindow()
	{
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();
	}
}
