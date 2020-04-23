package com.adnonstop.communityplayer.listener;

/**
 * author:tdn
 * time:2019/5/17
 * description:供给外部使用的回调
 */
public interface IVideoPlayerListener
{
	/**
	 * 视频模式切换监听
	 */
	public interface OnScreenModeListener
	{
		/**
		 * 进入全屏
		 */
		void enterFullScreen();

		/**
		 * 退出全屏
		 */
		void exitFullScreen();
	}

	/**
	 * 视频进度监听
	 */
	public interface OnProgressListener
	{
		/**
		 * @param position 当前时刻
		 * @param duration 总时长
		 */
		void onProgress(long position, long duration);
	}

	/**
	 * 视频循环播放开始监听
	 */
	public interface OnCircleStartListener
	{
		void onCircleStart();
	}

	public interface OnSeekCompleteListener
	{
		void onSeekComplete();
	}

	/**
	 * 首帧开始播放监听
	 */
	public interface OnFirstFrameStartListener
	{
		void onFirstFrameStart();
	}
}
