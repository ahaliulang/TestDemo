package com.adnonstop.communityplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.adnonstop.communityplayer.myinterface.ICommunityVideoPlayer;
import com.adnonstop.communityplayer.myinterface.IOnClickTinyScreen;
import com.adnonstop.communityplayer.util.BrightnessHelper;
import com.adnonstop.communityplayer.util.VideoPlayerUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * author:tdn
 * time:2018/12/25
 * description: 控制器抽象类
 */
public abstract class BaseVideoPlayerController extends RelativeLayout implements View.OnTouchListener
{

	private Context mContext;
	protected ICommunityVideoPlayer mCommunityVideoPlayer;

	private Timer mUpdateProgressTimer;
	private TimerTask mUpdateProgressTimerTask;

	private float mDownX;
	private float mDownY;
	private boolean mNeedChangePosition;
	private boolean mNeedChangeVolume;
	private boolean mNeedChangeBrightness;
	private static final int THRESHOLD = 80;
	private long mGestureDownPosition;
	private float mGestureDownBrightness;
	private int mGestureDownVolume;
	private long mNewPosition;
	protected ScreenMode mCurrentScreenMode = ScreenMode.Normal;

	protected boolean mFixedFullScreen;//是否固定全屏，true 是，false 否
	protected boolean mLandscapeInFullScreen = true;//全屏模式下是否横屏，true 是，false 否
	protected boolean mClearTinyScreen = false;//小屏模式下是否是清爽模式（只播放视频，没有进度、声音按钮等），true是，false 否

	protected int mViewWidth; //Normal 模式下 显示的宽
	protected int mViewHeight;//Normal 模式下 显示的高

	protected IOnClickTinyScreen mIOnClickTinyScreen;//自定义小屏点击事件

	private BrightnessHelper mBrightnessHelper;


	public BaseVideoPlayerController(@NonNull Context context)
	{
		this(context, null);
	}

	public BaseVideoPlayerController(@NonNull Context context, @Nullable AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public BaseVideoPlayerController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		mContext = context;
		this.setOnTouchListener(this);
		mBrightnessHelper = new BrightnessHelper(context);
	}


	public void setCommunityVideoPlayer(ICommunityVideoPlayer communityVideoPlayer)
	{
		mCommunityVideoPlayer = communityVideoPlayer;
		if(mFixedFullScreen)
		{
			mCommunityVideoPlayer.enterFullScreen(mLandscapeInFullScreen);
		}
	}


	/**
	 * 实际视图的宽高
	 *
	 * @param w
	 * @param h
	 */
	public void setFirstFrameViewWidthAndHeight(int w, int h)
	{
		mViewWidth = w;
		mViewHeight = h;
	}

	public abstract void loadCoverImage(String coverUrl);

	public abstract void onPlayStateChanged(int playState);

	protected abstract void onPlayModeChanged(ScreenMode playMode);

	protected abstract void onMuteModeChanged(boolean mute, boolean save);

	/**
	 * 重置控制器，将控制器恢复到初始状态。
	 */
	protected abstract void reset();

	public void onVideoSizeChange(int width, int height)
	{
	}


	/**
	 * 开启更新进度的计时器。
	 */
	protected void startUpdateProgressTimer()
	{
		cancelUpdateProgressTimer();
		if(mUpdateProgressTimer == null)
		{
			mUpdateProgressTimer = new Timer();
		}
		if(mUpdateProgressTimerTask == null)
		{
			mUpdateProgressTimerTask = new TimerTask()
			{
				@Override
				public void run()
				{
					BaseVideoPlayerController.this.post(new Runnable()
					{
						@Override
						public void run()
						{
							updateProgress();
						}
					});
				}
			};
		}
		mUpdateProgressTimer.schedule(mUpdateProgressTimerTask, 0, 200);
	}


	/**
	 * 取消更新进度的计时器。
	 */
	protected void cancelUpdateProgressTimer()
	{
		if(mUpdateProgressTimer != null)
		{
			mUpdateProgressTimer.cancel();
			mUpdateProgressTimer = null;
		}
		if(mUpdateProgressTimerTask != null)
		{
			mUpdateProgressTimerTask.cancel();
			mUpdateProgressTimerTask = null;
		}
	}


	/**
	 * 更新进度，包括进度条进度，展示的当前播放位置时长，总时长等。
	 */
	protected abstract void updateProgress();


	@Override
	public boolean onTouch(View view, MotionEvent event)
	{
		// 只有全屏的时候才能拖动位置、亮度、声音
		if (mCommunityVideoPlayer == null || !mCommunityVideoPlayer.isFullScreen())
		{
			return false;
		}
		// 只有在播放、暂停、缓冲的时候能够拖动改变位置、亮度和声音
		if(!mCommunityVideoPlayer.isLooping())
		{
			if(mCommunityVideoPlayer.isIdle() || mCommunityVideoPlayer.isError() || mCommunityVideoPlayer.isPreparing() || mCommunityVideoPlayer.isPrepared())
			{
				hideChangePosition();
				hideChangeBrightness();
				hideChangeVolume();
				return false;
			}
		}
		float x = event.getX();
		float y = event.getY();
		switch(event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				mDownX = x;
				mDownY = y;
				mNeedChangePosition = false;
				mNeedChangeVolume = false;
				mNeedChangeBrightness = false;
				break;
			case MotionEvent.ACTION_MOVE:
				float deltaX = x - mDownX;
				float deltaY = y - mDownY;
				float absDeltaX = Math.abs(deltaX);
				float absDeltaY = Math.abs(deltaY);
				if(!mNeedChangePosition && !mNeedChangeVolume && !mNeedChangeBrightness)
				{
					// 只有在播放、暂停、缓冲的时候能够拖动改变位置、亮度和声音
					if(absDeltaX >= THRESHOLD)
					{
						//cancelUpdateProgressTimer();注释掉为拖动的时候还是显示实时进度
						mNeedChangePosition = true;
						mGestureDownPosition = mCommunityVideoPlayer.getCurrentPosition();
					}
					else if(absDeltaY >= THRESHOLD)
					{
						if(mDownX < getWidth() * 0.5f)
						{
							// 左侧改变亮度
							mNeedChangeBrightness = true;
							mGestureDownBrightness = mBrightnessHelper.getAppBrightness();
						}
						else
						{
							// 右侧改变声音
							mNeedChangeVolume = true;
							mGestureDownVolume = mCommunityVideoPlayer.getVolume();
						}
					}
				}
				if(mNeedChangePosition)
				{
					long duration = mCommunityVideoPlayer.getDuration();
					long toPosition = (long)(mGestureDownPosition + duration * deltaX / getWidth());
					mNewPosition = Math.max(0, Math.min(duration, toPosition));
					int newPositionProgress = (int)(100f * mNewPosition / duration);
					showChangePosition(duration, newPositionProgress);
				}
				if(mNeedChangeBrightness)
				{
					deltaY = -deltaY;
					float deltaBrightness = deltaY * 3 / getHeight();
					float newBrightness = mGestureDownBrightness + deltaBrightness;
					newBrightness = Math.max(0, Math.min(newBrightness, 1));
					float newBrightnessPercentage = newBrightness;
					WindowManager.LayoutParams params = VideoPlayerUtil.scanForActivity(mContext).getWindow().getAttributes();
					params.screenBrightness = newBrightnessPercentage;
					VideoPlayerUtil.scanForActivity(mContext).getWindow().setAttributes(params);
					int newBrightnessProgress = (int)(100f * newBrightnessPercentage);
					showChangeBrightness(newBrightnessProgress);
				}
				if(mNeedChangeVolume)
				{
					deltaY = -deltaY;
					int maxVolume = mCommunityVideoPlayer.getMaxVolume();
					int deltaVolume = (int)(maxVolume * deltaY * 3 / getHeight());
					int newVolume = mGestureDownVolume + deltaVolume;
					newVolume = Math.max(0, Math.min(maxVolume, newVolume));
					mCommunityVideoPlayer.setVolume(newVolume);
					int newVolumeProgress = (int)(100f * newVolume / maxVolume);
					showChangeVolume(newVolumeProgress);
				}
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				if(mNeedChangePosition)
				{
					mCommunityVideoPlayer.seekTo(mNewPosition);
					hideChangePosition();
					startUpdateProgressTimer();
					return true;
				}
				if(mNeedChangeBrightness)
				{
					hideChangeBrightness();
					return true;
				}
				if(mNeedChangeVolume)
				{
					hideChangeVolume();
					return true;
				}
				onViewClick();
				break;
		}
		return true;
	}


	/**
	 * 手势左右滑动改变播放位置时，显示控制器中间的播放位置变化视图，
	 * 在手势滑动ACTION_MOVE的过程中，会不断调用此方法。
	 *
	 * @param duration            视频总时长ms
	 * @param newPositionProgress 新的位置进度，取值0到100。
	 */
	protected abstract void showChangePosition(long duration, int newPositionProgress);

	/**
	 * 手势左右滑动改变播放位置后，手势up或者cancel时，隐藏控制器中间的播放位置变化视图，
	 * 在手势ACTION_UP或ACTION_CANCEL时调用。
	 */
	protected abstract void hideChangePosition();

	/**
	 * 手势在右侧上下滑动改变音量时，显示控制器中间的音量变化视图，
	 * 在手势滑动ACTION_MOVE的过程中，会不断调用此方法。
	 *
	 * @param newVolumeProgress 新的音量进度，取值1到100。
	 */
	protected abstract void showChangeVolume(int newVolumeProgress);

	/**
	 * 手势在左侧上下滑动改变音量后，手势up或者cancel时，隐藏控制器中间的音量变化视图，
	 * 在手势ACTION_UP或ACTION_CANCEL时调用。
	 */
	protected abstract void hideChangeVolume();

	/**
	 * 手势在左侧上下滑动改变亮度时，显示控制器中间的亮度变化视图，
	 * 在手势滑动ACTION_MOVE的过程中，会不断调用此方法。
	 *
	 * @param newBrightnessProgress 新的亮度进度，取值1到100。
	 */
	protected abstract void showChangeBrightness(int newBrightnessProgress);

	/**
	 * 手势在左侧上下滑动改变亮度后，手势up或者cancel时，隐藏控制器中间的亮度变化视图，
	 * 在手势ACTION_UP或ACTION_CANCEL时调用。
	 */
	protected abstract void hideChangeBrightness();

	protected abstract void onViewClick();

	public IOnClickTinyScreen getIOnClickTinyScreen()
	{
		return mIOnClickTinyScreen;
	}

	public void setIOnClickTinyScreen(IOnClickTinyScreen IOnClickTinyScreen)
	{
		mIOnClickTinyScreen = IOnClickTinyScreen;
	}
}

































