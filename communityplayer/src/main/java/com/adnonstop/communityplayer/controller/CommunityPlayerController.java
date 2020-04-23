package com.adnonstop.communityplayer.controller;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.adnonstop.communityplayer.BaseVideoPlayerController;
import com.adnonstop.communityplayer.CommunityPlayer;
import com.adnonstop.communityplayer.CommunityVideoPlayerView;
import com.adnonstop.communityplayer.R;
import com.adnonstop.communityplayer.ScreenMode;
import com.adnonstop.communityplayer.listener.IVideoPlayerListener;
import com.adnonstop.communityplayer.myinterface.IAutoPlay;
import com.adnonstop.communityplayer.myinterface.ICommunityVideoPlayer;
import com.adnonstop.communityplayer.myinterface.IOnClickTinyScreen;
import com.adnonstop.communityplayer.util.AudioUtils;
import com.adnonstop.communityplayer.util.CommunityPlayerUtils;
import com.adnonstop.communityplayer.util.NetUtils;
import com.adnonstop.communityplayer.util.SPUtils;
import com.adnonstop.communityplayer.util.StatusBarUtil;
import com.adnonstop.communityplayer.util.VideoPlayerUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.adnonstop.communityplayer.CommunityVideoPlayerView.STATE_IDLE;

/**
 * author:tdn
 * time:2018/12/25
 * description: 社区页面控制器
 */
public class CommunityPlayerController extends BaseVideoPlayerController implements View.OnClickListener, SeekBar.OnSeekBarChangeListener
{

	/**********************公共部分*************************/
	private RelativeLayout mBaseVideoRelativeLayout;
	private ImageView mFirstFrameImageView;
	private RelativeLayout mAdjustRelativeLayout;
	private ProgressBar mAdjustProgressBar;
	private FrameLayout mCoverFrameLayout;
	/**********************公共部分*************************/

	/**********************全屏特有部分*************************/
	private ProgressBar mFullPreProgressBar;
	private ImageView mFullLoadingImageView;
	private RelativeLayout mFullWidgetRelativeLayout;
	private ImageView mFullBackImageView;
	private ImageView mFullPlayStateImageView;
	private TextView mFullCurrentTextView;
	private SeekBar mFullSeekBar;
	private TextView mFullTotalTextView;
	/**********************全屏特有部分*************************/

	/**********************小屏特有部分*************************/
	private LinearLayout mNormalVoiceLinearLayout;
	private ImageView mNormalLoadingImageView;
	public ImageView mNormalPlayStateImageView;
	private TextView mNormalPositionTextView;
	private ImageView mNormalVoiceImageView;
	private ImageView mEnterFullImageView;

	/**********************小屏特有部分*************************/


	private ObjectAnimator mLoadingRotationAnim;
	private CountDownTimer mDismissTopBottomCountDownTimer;
	private int mCurrentPlayState = STATE_IDLE;
	private boolean mFullWidgetVisible;
	private AudioManager mAudioManager;
	private IVideoPlayerListener.OnProgressListener mOnProgressListener;

	private int mSeekbarCurrentPosition;
	private boolean mOnlyShowVoice; //只显示声音


	public CommunityPlayerController(@NonNull Context context)
	{
		this(context, null);
	}

	public CommunityPlayerController(@NonNull Context context, @Nullable AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public CommunityPlayerController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init();
	}


	private void init()
	{
		//Inflate布局
		LayoutInflater.from(getContext()).inflate(R.layout.c_view_community_player, this, true);
		mBaseVideoRelativeLayout = findViewById(R.id.base_video_relative_layout);
		mFirstFrameImageView = findViewById(R.id.first_frame_image_view);
		mAdjustRelativeLayout = findViewById(R.id.adjust_relative_layout);
		mAdjustProgressBar = findViewById(R.id.adjust_progress_bar);
		updateProgressColor(mAdjustProgressBar);
		mCoverFrameLayout = findViewById(R.id.cover_frame_layout);
		if(NetUtils.getNetType(getContext()) == NetUtils.TYPE_MOBILE)
		{
			mCoverFrameLayout.setBackgroundColor(Color.parseColor("#4d000000"));
		}
		else
		{
			mCoverFrameLayout.setBackgroundColor(Color.parseColor("#00000000"));
		}

		mFullPreProgressBar = findViewById(R.id.pre_progress_bar);
		updateProgressColor(mFullPreProgressBar);
		mFullLoadingImageView = findViewById(R.id.full_loading_image_view);
		mFullWidgetRelativeLayout = findViewById(R.id.widget_relative_layout);
		mFullBackImageView = findViewById(R.id.back_image_view);

		//适配刘海屏
		RelativeLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.topMargin = StatusBarUtil.getStatusBarHeight(getContext()) + CommunityPlayerUtils.getRealPixel(6);

		mFullPlayStateImageView = findViewById(R.id.full_play_state_image_view);
		mFullCurrentTextView = findViewById(R.id.current_text_view);
		mFullSeekBar = findViewById(R.id.seek_bar);
		mFullTotalTextView = findViewById(R.id.total_text_view);

		mNormalVoiceLinearLayout = findViewById(R.id.voice_linear_layout);
		mNormalLoadingImageView = findViewById(R.id.normal_loading_image_view);
		mNormalPlayStateImageView = findViewById(R.id.normal_play_state_image_view);
		mNormalPositionTextView = findViewById(R.id.position_text_view);
		mNormalVoiceImageView = findViewById(R.id.voice_image_view);
		mEnterFullImageView = findViewById(R.id.enter_full_image_view);

		mFullBackImageView.setOnClickListener(this);
		mFullPlayStateImageView.setOnClickListener(this);
		//mNormalPlayStateImageView.setOnClickListener(this);取消点击事件
		mNormalVoiceImageView.setOnClickListener(this);

		//this.setOnClickListener(this);

		mAudioManager = (AudioManager)getContext().getSystemService(Context.AUDIO_SERVICE);

		mFullSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				mSeekbarCurrentPosition = progress;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{
				if(mCommunityVideoPlayer != null)
				{
					mCommunityVideoPlayer.pause();
				}
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				if(mCommunityVideoPlayer != null)
				{
					long duration = mCommunityVideoPlayer.getDuration();
					long seekTo = (long)((float)mSeekbarCurrentPosition / 100 * duration);
					mCommunityVideoPlayer.seekTo(seekTo);
					mCommunityVideoPlayer.restart();
				}
			}
		});
	}


	private void updateProgressColor(ProgressBar progressBar)
	{
		int color = CommunityPlayerUtils.GetSkinColor() != 0 ? (CommunityPlayer.APP_CODE == CommunityPlayer.INTERPHOTO ? CommunityPlayerUtils.GetSkinColor1() : CommunityPlayerUtils.GetSkinColor()) : 0xffe75887;
		//Background
		ClipDrawable bgClipDrawable = new ClipDrawable(new ColorDrawable(0x80ffffff), Gravity.LEFT, ClipDrawable.HORIZONTAL);
		bgClipDrawable.setLevel(10000);
		//Progress
		ClipDrawable progressClip = new ClipDrawable(new ColorDrawable(color), Gravity.LEFT, ClipDrawable.HORIZONTAL);
		//Setup LayerDrawable and assign to progressBar
		Drawable[] progressDrawables = {bgClipDrawable, progressClip/*second*/, progressClip};
		LayerDrawable progressLayerDrawable = new LayerDrawable(progressDrawables);
		progressLayerDrawable.setId(0, android.R.id.background);
		progressLayerDrawable.setId(1, android.R.id.secondaryProgress);
		progressLayerDrawable.setId(2, android.R.id.progress);
		progressBar.setProgressDrawable(progressLayerDrawable);
	}

	@Override
	public void onClick(View v)
	{
		if(v == mFullPlayStateImageView)
		{
			if(mCommunityVideoPlayer.isIdle())
			{
				mCommunityVideoPlayer.start(false);
			}
			else if(mCommunityVideoPlayer.isPlaying() || mCommunityVideoPlayer.isBufferingPlaying())
			{
				mCommunityVideoPlayer.pause();
			}
			else if(mCommunityVideoPlayer.isPaused() || mCommunityVideoPlayer.isBufferingPaused())
			{
				mCommunityVideoPlayer.restart();
				mFullWidgetVisible = false;
			}
		}
		else if(v == mNormalPlayStateImageView)
		{
			if(mCommunityVideoPlayer.isIdle())
			{
				mCommunityVideoPlayer.start(false);
			}
			else if(mCommunityVideoPlayer.isPlaying() || mCommunityVideoPlayer.isBufferingPlaying())
			{
				mCommunityVideoPlayer.pause();
			}
			else if(mCommunityVideoPlayer.isPaused() || mCommunityVideoPlayer.isBufferingPaused())
			{
				mCommunityVideoPlayer.restart();
			}
		}
		else if(v == mFullBackImageView)
		{
			if(mCommunityVideoPlayer.isFullScreen() && !mFixedFullScreen)
			{
				mCommunityVideoPlayer.exitFullScreen();
			}
			else if(mCommunityVideoPlayer.isTinyWindow())
			{
				mCommunityVideoPlayer.exitTinyWindow();
			}
			//退出Activity
			else if(mFixedFullScreen)
			{
				if(getContext() instanceof Activity)
				{
					((Activity)getContext()).finish();
				}
			}
		}
		else if(v == mNormalVoiceImageView)
		{
			if(mCommunityVideoPlayer.isMute())
			{
				CommunityPlayer.setGlobalVideoAutoMute(getContext(), false);
				mCommunityVideoPlayer.setMuteMode(false, true);
				mNormalVoiceImageView.setImageResource(R.drawable.c_voice_on_icon);
				if(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0)
				{
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2, 0);
				}
				AudioUtils.reqAudioFocus(mAudioManager);
			}
			else
			{
				CommunityPlayer.setGlobalVideoAutoMute(getContext(), true);
				mCommunityVideoPlayer.setMuteMode(true, true);
				mNormalVoiceImageView.setImageResource(R.drawable.c_voice_off_icon);
				AudioUtils.abandonAudioFocus(mAudioManager);
			}

		}
		else if(v == this)
		{
			if((mCommunityVideoPlayer.isNormal() || mCommunityVideoPlayer.isTinyWindow()) && !mCommunityVideoPlayer.isIdle())
			{
				mCommunityVideoPlayer.enterFullScreen(mLandscapeInFullScreen);
			}
			else if(mCommunityVideoPlayer.isFullScreen())
			{
				setFullWidgetVisible(!mFullWidgetVisible);
			}
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar)
	{

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar)
	{
		if(mCommunityVideoPlayer.isBufferingPaused() || mCommunityVideoPlayer.isPaused())
		{
			mCommunityVideoPlayer.restart();
		}
		long position = (long)(mCommunityVideoPlayer.getDuration() * seekBar.getProgress() / 100f);
		mCommunityVideoPlayer.seekTo(position);
		startDismissTopBottomTimer();
	}


	@Override
	public void setCommunityVideoPlayer(ICommunityVideoPlayer communityVideoPlayer)
	{
		super.setCommunityVideoPlayer(communityVideoPlayer);

	}

	@Override
	public void setFirstFrameViewWidthAndHeight(int w, int h)
	{
		super.setFirstFrameViewWidthAndHeight(w, h);
		setFirstFrameWidthAndHeight(w, h, ImageView.ScaleType.CENTER_CROP);
	}

	private void setFirstFrameWidthAndHeight(int w, int h, ImageView.ScaleType type)
	{
		ViewGroup.LayoutParams layoutParams = mFirstFrameImageView.getLayoutParams();
		layoutParams.width = w;
		layoutParams.height = h;
		mFirstFrameImageView.setScaleType(type);
		mFirstFrameImageView.invalidate();
	}

	@Override
	public void loadCoverImage(String coverUrl)
	{
		if(!TextUtils.isEmpty(coverUrl))
		{
			Glide.with(getContext()).load(coverUrl).diskCacheStrategy(DiskCacheStrategy.ALL).dontTransform().into(mFirstFrameImageView);
		}
	}


	@Override
	public void onPlayStateChanged(int playState)
	{
		mCurrentPlayState = playState;
		switch(playState)
		{
			case STATE_IDLE:
				break;
			case CommunityVideoPlayerView.STATE_PREPARING:
				//显示第一帧
				mFirstFrameImageView.setVisibility(View.VISIBLE);
				startLoading(mCurrentScreenMode);
				break;
			case CommunityVideoPlayerView.STATE_PREPARED:
				break;
			case CommunityVideoPlayerView.STATE_FIRST_FRAME_START:
				mFirstFrameImageView.setVisibility(View.GONE);
				hideLoading(mCurrentScreenMode);
				startUpdateProgressTimer();
				break;
			case CommunityVideoPlayerView.STATE_PLAYING:
				hideLoading(mCurrentScreenMode);
				if(mCurrentScreenMode == ScreenMode.Normal)
				{
					//mNormalPlayStateImageView.setImageBitmap(null);
				}
				else if(mCurrentScreenMode == ScreenMode.Full)
				{
					mFullPlayStateImageView.setImageResource(R.drawable.c_video_pause_selector);
				}
				break;
			case CommunityVideoPlayerView.STATE_PAUSED:
				hideLoading(mCurrentScreenMode);
				if(mCurrentScreenMode == ScreenMode.Normal)
				{
					mNormalPlayStateImageView.setImageResource(R.drawable.c_opus_video_icon);
					mNormalPlayStateImageView.setVisibility(VISIBLE);
				}
				else if(mCurrentScreenMode == ScreenMode.Full)
				{
					mFullPlayStateImageView.setImageResource(R.drawable.c_video_start_selector);
					mFullPreProgressBar.setVisibility(GONE);
					mFullWidgetRelativeLayout.setVisibility(VISIBLE);
				}
				cancelDismissTopBottomTimer();
				break;
			case CommunityVideoPlayerView.STATE_BUFFERING_PLAYING:
				startLoading(mCurrentScreenMode);
				startDismissTopBottomTimer();
				break;
			case CommunityVideoPlayerView.STATE_BUFFERING_PAUSED:
				hideLoading(mCurrentScreenMode);
				cancelDismissTopBottomTimer();
				break;
			case CommunityVideoPlayerView.STATE_ERROR:
				cancelUpdateProgressTimer();
				break;
			case CommunityVideoPlayerView.STATE_COMPLETED:
				cancelUpdateProgressTimer();
				break;
		}
	}

	@Override
	protected void onPlayModeChanged(ScreenMode playMode)
	{
		final ScreenMode targetMode = playMode;
		if(targetMode != mCurrentScreenMode)
		{
			mCurrentScreenMode = targetMode;
			onPlayStateChanged(mCurrentPlayState);
			//解决第一帧黑边问题
			if(mCurrentScreenMode == ScreenMode.Full)
			{
				setFirstFrameWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, ImageView.ScaleType.FIT_CENTER);
				mEnterFullImageView.setVisibility(GONE);
			}
			else if(mCurrentScreenMode == ScreenMode.Normal)
			{
				if(mViewWidth <= 0 || mViewHeight <= 0)
				{
					setFirstFrameWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, ImageView.ScaleType.FIT_XY);
				}
				else
				{
					setFirstFrameWidthAndHeight(mViewWidth, mViewHeight, ImageView.ScaleType.CENTER_CROP);
				}
				if(!mClearTinyScreen)
				{
					mEnterFullImageView.setVisibility(VISIBLE);
					mNormalVoiceLinearLayout.setVisibility(VISIBLE);
				}
				else
				{
					mEnterFullImageView.setVisibility(GONE);
					mNormalLoadingImageView.setVisibility(GONE);
				}
				if(mOnlyShowVoice)
				{
					mEnterFullImageView.setVisibility(GONE);
					mNormalVoiceLinearLayout.setVisibility(VISIBLE);
					try
					{
						LayoutParams layoutParams = (LayoutParams)mNormalVoiceLinearLayout.getLayoutParams();
						layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
						mNormalVoiceLinearLayout.setLayoutParams(layoutParams);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	protected void onMuteModeChanged(boolean mute, boolean save)
	{
		if(mute)
		{
			mCommunityVideoPlayer.setMuteMode(true, save);
			mNormalVoiceImageView.setImageResource(R.drawable.c_voice_off_icon);
			AudioUtils.abandonAudioFocus(mAudioManager);
		}
		else
		{
			mCommunityVideoPlayer.setMuteMode(false, save);
			mNormalVoiceImageView.setImageResource(R.drawable.c_voice_on_icon);
			AudioUtils.reqAudioFocus(mAudioManager);
		}
		if(getContext() instanceof IAutoPlay)
		{
			mCommunityVideoPlayer.setMuteMode(false, save);
		}
	}

	@Override
	protected void reset()
	{
		mFullWidgetVisible = false;
		cancelUpdateProgressTimer();
		cancelDismissTopBottomTimer();
		mFullSeekBar.setProgress(0);
		mFullSeekBar.setSecondaryProgress(0);
		mFullPreProgressBar.setProgress(0);
		mFullPreProgressBar.setSecondaryProgress(0);
		mFirstFrameImageView.setVisibility(VISIBLE);

		if(NetUtils.getNetType(getContext()) == NetUtils.TYPE_MOBILE)
		{
			boolean autoPlayIn4G = (boolean)SPUtils.get(getContext(), SPUtils.GLOBAL_AUTO_PLAY_IN_4G, false);
			if(autoPlayIn4G)
			{
				setCoverColor(Color.parseColor("#00000000"));
				setPlayIconVisible(false);
			}
			else
			{
				setCoverColor(Color.parseColor("#4d000000"));
				setPlayIconVisible(true);
			}
		}
		else
		{
			setCoverColor(Color.parseColor("#00000000"));
		}

		mAdjustRelativeLayout.setVisibility(GONE);
		mFullPreProgressBar.setVisibility(GONE);
		mFullLoadingImageView.setVisibility(GONE);
		mFullWidgetRelativeLayout.setVisibility(GONE);
		mNormalVoiceLinearLayout.setVisibility(GONE);
		mEnterFullImageView.setVisibility(GONE);
		mNormalLoadingImageView.setVisibility(GONE);
		if(mFixedFullScreen && mCommunityVideoPlayer != null)
		{
			mCommunityVideoPlayer.enterFullScreen(mLandscapeInFullScreen);
		}
	}

	@Override
	protected void updateProgress()
	{
		long position = mCommunityVideoPlayer.getCurrentPosition();
		long duration = mCommunityVideoPlayer.getDuration();
		if(mOnProgressListener != null)
		{
			mOnProgressListener.onProgress(position, duration);
		}
		int progress = (int)(100f * position / duration);
		if(mCurrentScreenMode == ScreenMode.Normal)
		{
			//倒计时
			mNormalPositionTextView.setText(VideoPlayerUtil.formatTime(duration - position));
		}
		else if(mCurrentScreenMode == ScreenMode.Full)
		{

			mFullSeekBar.setProgress(progress);
			mFullCurrentTextView.setText(VideoPlayerUtil.formatTime(position));
			mFullTotalTextView.setText(VideoPlayerUtil.formatTime(duration));
			mFullPreProgressBar.setProgress(progress);
		}
	}

	@Override
	protected void showChangePosition(long duration, int newPositionProgress)
	{
		mFullWidgetRelativeLayout.setVisibility(GONE);
		mAdjustRelativeLayout.setVisibility(View.VISIBLE);
		mAdjustProgressBar.setProgress(newPositionProgress);
		mFullPreProgressBar.setVisibility(VISIBLE);
	}

	@Override
	protected void hideChangePosition()
	{
		mAdjustRelativeLayout.setVisibility(View.GONE);
		mFullPreProgressBar.setVisibility(VISIBLE);
	}

	@Override
	protected void showChangeVolume(int newVolumeProgress)
	{
		mFullWidgetRelativeLayout.setVisibility(GONE);
		mAdjustRelativeLayout.setVisibility(View.VISIBLE);
		mAdjustProgressBar.setProgress(newVolumeProgress);
		mFullPreProgressBar.setVisibility(VISIBLE);
	}

	@Override
	protected void hideChangeVolume()
	{
		mAdjustRelativeLayout.setVisibility(View.GONE);
		mFullPreProgressBar.setVisibility(VISIBLE);
	}

	@Override
	protected void showChangeBrightness(int newBrightnessProgress)
	{
		mFullWidgetRelativeLayout.setVisibility(GONE);
		mAdjustRelativeLayout.setVisibility(View.VISIBLE);
		mAdjustProgressBar.setProgress(newBrightnessProgress);
		mFullPreProgressBar.setVisibility(VISIBLE);
	}

	@Override
	protected void hideChangeBrightness()
	{
		mAdjustRelativeLayout.setVisibility(View.GONE);
		mFullPreProgressBar.setVisibility(VISIBLE);
	}

	@Override
	protected void onViewClick()
	{
		if((mCommunityVideoPlayer.isNormal() || mCommunityVideoPlayer.isTinyWindow()) && !mCommunityVideoPlayer.isIdle())
		{
			mCommunityVideoPlayer.enterFullScreen(mLandscapeInFullScreen);
		}
		else if(mCommunityVideoPlayer.isFullScreen())
		{
			setFullWidgetVisible(!mFullWidgetVisible);
		}
	}

	//开始加载动画
	private void startLoading(ScreenMode screenMode)
	{
		mNormalVoiceLinearLayout.setVisibility(GONE);
		mNormalPlayStateImageView.setVisibility(GONE);
		mFullWidgetRelativeLayout.setVisibility(GONE);
		if(screenMode == ScreenMode.Normal)
		{
			mFullPreProgressBar.setVisibility(GONE);
			mFullLoadingImageView.setVisibility(GONE);
			if(!mClearTinyScreen)
			{
				if(mOnlyShowVoice)
				{
					try
					{
						LayoutParams layoutParams = (LayoutParams)mNormalLoadingImageView.getLayoutParams();
						layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
						mNormalLoadingImageView.setLayoutParams(layoutParams);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					mEnterFullImageView.setVisibility(GONE);
				}
				else
				{
					mEnterFullImageView.setVisibility(VISIBLE);
				}
				if(mNormalLoadingImageView.getVisibility() != VISIBLE)
				{
					mNormalLoadingImageView.setVisibility(VISIBLE);
				}
				if(mLoadingRotationAnim != null)
				{
					mLoadingRotationAnim.cancel();
				}
				mLoadingRotationAnim = ObjectAnimator.ofFloat(mNormalLoadingImageView, "rotation", 0f, 360f);
				mLoadingRotationAnim.setDuration(1000);
				mLoadingRotationAnim.setRepeatCount(ValueAnimator.INFINITE);
				mLoadingRotationAnim.start();
			}
		}
		else if(screenMode == ScreenMode.Full)
		{
			mFullPreProgressBar.setVisibility(VISIBLE);
			mNormalLoadingImageView.setVisibility(GONE);
			mEnterFullImageView.setVisibility(GONE);
			mFullLoadingImageView.setVisibility(VISIBLE);
			mLoadingRotationAnim = ObjectAnimator.ofFloat(mFullLoadingImageView, "rotation", 0f, 360f);
			mLoadingRotationAnim.setDuration(1000);
			mLoadingRotationAnim.setRepeatCount(ValueAnimator.INFINITE);
			mLoadingRotationAnim.start();
		}
	}


	//隐藏加载动画
	private void hideLoading(ScreenMode screenMode)
	{
		if(mLoadingRotationAnim != null)
		{
			mLoadingRotationAnim.cancel();
			mLoadingRotationAnim = null;
		}
		mNormalLoadingImageView.setVisibility(GONE);
		mFullLoadingImageView.setVisibility(GONE);
		if(screenMode == ScreenMode.Normal)
		{
			if(!mClearTinyScreen)
			{
				mEnterFullImageView.setVisibility(VISIBLE);
				mNormalVoiceLinearLayout.setVisibility(VISIBLE);
				if(mOnlyShowVoice)
				{
					mEnterFullImageView.setVisibility(GONE);
				}
			}
			else
			{
				mNormalVoiceLinearLayout.setVisibility(GONE);
				mEnterFullImageView.setVisibility(GONE);
				if(mOnlyShowVoice)
				{
					mNormalVoiceLinearLayout.setVisibility(VISIBLE);
				}
			}

			mNormalPlayStateImageView.setVisibility(GONE);
			mFullPreProgressBar.setVisibility(GONE);
			mFullWidgetRelativeLayout.setVisibility(GONE);
		}
		else if(screenMode == ScreenMode.Full)
		{
			mNormalVoiceLinearLayout.setVisibility(GONE);
			mNormalPlayStateImageView.setVisibility(GONE);
			mFullPreProgressBar.setVisibility(VISIBLE);
			mFullWidgetRelativeLayout.setVisibility(GONE);
		}
	}

	/**
	 * 开启top、bottom自动消失的timer
	 */
	private void startDismissTopBottomTimer()
	{
		cancelDismissTopBottomTimer();
		if(mDismissTopBottomCountDownTimer == null)
		{
			mDismissTopBottomCountDownTimer = new CountDownTimer(3000, 3000)
			{
				@Override
				public void onTick(long millisUntilFinished)
				{

				}

				@Override
				public void onFinish()
				{
					setFullWidgetVisible(false);
				}
			};
		}
		mDismissTopBottomCountDownTimer.start();
	}

	/**
	 * 取消top、bottom自动消失的timer
	 */
	private void cancelDismissTopBottomTimer()
	{
		if(mDismissTopBottomCountDownTimer != null)
		{
			mDismissTopBottomCountDownTimer.cancel();
		}
	}

	private void setFullWidgetVisible(boolean visible)
	{
		mFullWidgetVisible = visible;
		if(mCurrentScreenMode == ScreenMode.Full)
		{
			if(visible)
			{
				mFullWidgetRelativeLayout.setVisibility(VISIBLE);
				mFullPreProgressBar.setVisibility(GONE);
				if(!mCommunityVideoPlayer.isPaused() && !mCommunityVideoPlayer.isBufferingPaused())
				{
					startDismissTopBottomTimer();
				}
			}
			else
			{
				mFullPreProgressBar.setVisibility(VISIBLE);
				mFullWidgetRelativeLayout.setVisibility(GONE);
				cancelDismissTopBottomTimer();
			}
		}
	}

	/**
	 * 设置为固定全屏模式
	 *
	 * @param fixedFullScreen true 固定为全屏，false 反之
	 */
	public void setFixedFullScreen(boolean fixedFullScreen)
	{
		mFixedFullScreen = fixedFullScreen;
	}

	/**
	 * 设置全屏模式下是否根据宽高自动横屏
	 *
	 * @param landscapeInFullScreen true 是,false 反之
	 */
	public void setLandscapeInFullScreen(boolean landscapeInFullScreen)
	{
		mLandscapeInFullScreen = landscapeInFullScreen;
	}


	/**
	 * 设置小屏模式下是否为清爽模式（没有进度、声音等UI）
	 *
	 * @param clearTinyScreen true 是，false 反之
	 */
	public void setClearTinyScreen(boolean clearTinyScreen)
	{
		mClearTinyScreen = clearTinyScreen;
	}


	/**
	 * 设置只显示声音UI
	 *
	 * @param onlyShowVoice
	 */
	public void setOnlyShowVoice(boolean onlyShowVoice)
	{
		mOnlyShowVoice = onlyShowVoice;

		if(mNormalVoiceLinearLayout != null && mEnterFullImageView != null)
		{
			try
			{
				LayoutParams layoutParams = (LayoutParams)mNormalVoiceLinearLayout.getLayoutParams();
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				mNormalVoiceLinearLayout.setLayoutParams(layoutParams);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			mEnterFullImageView.setVisibility(GONE);
		}
	}


	/**
	 * 设置视频进度监听
	 *
	 * @param onProgressListener
	 */
	public void setOnProgressListener(IVideoPlayerListener.OnProgressListener onProgressListener)
	{
		mOnProgressListener = onProgressListener;
	}


	/**
	 * 设置遮罩颜色
	 *
	 * @param color
	 */
	public void setCoverColor(int color)
	{
		mCoverFrameLayout.setBackgroundColor(color);
	}

	public void setIOnClickTinyScreen(IOnClickTinyScreen IOnClickTinyScreen)
	{
		mIOnClickTinyScreen = IOnClickTinyScreen;
	}

	public void setPlayIconVisible(boolean visible)
	{
		if(visible)
		{
			mNormalPlayStateImageView.setVisibility(VISIBLE);
		}
		else
		{
			mNormalPlayStateImageView.setVisibility(GONE);
		}
	}

	public void setClickEnterFullScreen(boolean clickEnterFullScreen)
	{
		if(clickEnterFullScreen)
		{
			setOnClickListener(this);
		}
		else
		{
			setOnClickListener(null);
		}
	}
}
