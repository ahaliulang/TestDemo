package com.adnonstop.communityplayer.myinterface;

import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.FileDescriptor;

/**
 * author:tdn
 * time:2018/12/28
 * description: 用于规范不同播放器的统一接口，如系统播放器、阿里云播放器等
 */
public interface IMediaPlayer
{

	public static final int C_MEDIA_INFO_DEFAULT = 9999;
	//视频首帧开始播放
	public static final int C_MEDIA_INFO_VIDEO_RENDERING_START = 9527;
	//视频缓冲开始
	public static final int C_MEDIA_INFO_BUFFERING_START = 9528;
	//视频缓冲结束
	public static final int C_MEDIA_INFO_BUFFERING_END = 9529;


	/**
	 * 设置surface
	 *
	 * @param var1
	 */
	void setSurface(Surface var1);


	/**
	 * 设置视频播放链接
	 *
	 * @param var1
	 */
	void setDataSource(String var1) throws Exception;


	/**
	 * 设置视频播放文件描述符，用以支持本地视频播放，
	 *
	 * @param fd
	 * @throws Exception
	 */
	void setDataSource(FileDescriptor fd) throws Exception;

	/**
	 * 设置surfaceHolder
	 *
	 * @param var1
	 */
	void setDisplay(SurfaceHolder var1);


	/**
	 * 设置视频缓存相关
	 *
	 * @param enable      是否可以边播边存。
	 * @param saveDir     缓存的目录（绝对路径）
	 * @param maxDuration 能缓存的单个视频最大长度（单位：秒）。如果单个视频超过这个值，就不缓存。
	 * @param maxSize     缓存目录的所有缓存文件的总的最大大小（单位：MB）。如果超过则删除最旧文件，如果还是不够，则不缓存。
	 */
	void setPlayingCache(boolean enable, String saveDir, int maxDuration, long maxSize);

	/**
	 * 准备视频(异步)
	 *
	 * @throws IllegalStateException
	 */
	void prepareAsync() throws IllegalStateException;

	/**
	 * 开始播放
	 *
	 * @throws IllegalStateException
	 */
	void start() throws IllegalStateException;

	/**
	 * 暂停播放
	 *
	 * @throws IllegalStateException
	 */
	void pause() throws IllegalStateException;

	/**
	 * 恢复播放
	 *
	 * @throws IllegalStateException
	 */
	void resume() throws IllegalStateException;

	/**
	 * 停止播放
	 *
	 * @throws IllegalStateException
	 */
	void stop() throws IllegalStateException;

	/**
	 * 拖动到的时间点
	 *
	 * @param position
	 */
	void seekTo(long position);

	/**
	 * 设置静音
	 *
	 * @param var1
	 */
	void setMuteMode(boolean var1);

	/**
	 * 获取当前视频总时长
	 *
	 * @return
	 */
	long getDuration();

	/**
	 * 获取当前播放位置
	 *
	 * @return
	 */
	long getCurrentPosition();

	/**
	 * 设置循环播放
	 *
	 * @param var1
	 */
	void setLooping(boolean var1);

	/**
	 * 获取是否循环
	 *
	 * @return
	 */
	boolean getLooping();


	void setVideoScalingMode(VideoScalingMode var1);

	/**
	 * 重置播放器
	 */
	void reset();

	/**
	 * 释放播放器资源
	 */
	void release();

	/**
	 * 设置视频准备结束的监听事件
	 *
	 * @param var1
	 */
	void setOnPreparedListener(IMediaPlayer.OnPreparedListener var1);

	/**
	 * 设置首帧开始播放的监听事件
	 *
	 * @param var1
	 */
	void setOnFirstFrameStartListener(IMediaPlayer.OnFirstFrameStartListener var1);

	/**
	 * 设置视频大小改变监听事件
	 *
	 * @param var1
	 */
	void setOnVideoSizeChangedListener(IMediaPlayer.OnVideoSizeChangedListener var1);

	/**
	 * 设置视频播放结束的监听事件
	 *
	 * @param var1
	 */
	void setOnCompletionListener(IMediaPlayer.OnCompletionListener var1);

	/**
	 * 设置视频出错的监听事件
	 *
	 * @param var1
	 */
	void setOnErrorListener(IMediaPlayer.OnErrorListener var1);

	/**
	 * 设置视频信息的监听事件
	 *
	 * @param var1
	 */
	void setOnInfoListener(IMediaPlayer.OnInfoListener var1);


	/**
	 * 设置视频循环播放开始事件
	 *
	 * @param var1
	 */
	void setOnCircleStartListener(IMediaPlayer.OnCircleStartListener var1);

	/**
	 * 设置拖动视频结束事件
	 *
	 * @param var1
	 */
	void setOnSeekCompleteListener(IMediaPlayer.OnSeekCompleteListener var1);


	/**
	 * 获取播放器实例
	 *
	 * @return
	 */
	Object getInternalPlayer();


	/**
	 * 视频准备完成的监听事件
	 * 视频准备成功。失败则走OnErrorListener回调
	 */
	public interface OnPreparedListener
	{
		void onPrepared(IMediaPlayer mp);
	}

	/**
	 * 视频画面大小变化事件。
	 */
	public interface OnVideoSizeChangedListener
	{
		void onVideoSizeChanged(IMediaPlayer mp, int width, int height);
	}

	/**
	 * 视频播放结束监听事件。
	 */
	public interface OnCompletionListener
	{
		void onCompletion(IMediaPlayer mp);
	}

	/**
	 * 播放器出错事件。
	 */
	public interface OnErrorListener
	{
		boolean onError(IMediaPlayer mp, Object what, Object extra);
	}

	/**
	 * 播放器信息事件。
	 */
	public interface OnInfoListener
	{
		boolean onInfo(IMediaPlayer mp, int what, Object extra);
	}


	/**
	 * 首帧开始播放的监听事件。
	 */
	public interface OnFirstFrameStartListener
	{
		void onFirstFrameStart(IMediaPlayer mp);
	}


	/**
	 * 视频循环播放开始事件
	 */
	public interface OnCircleStartListener
	{
		void onCircleStartListener(IMediaPlayer mp);
	}


	public interface OnSeekCompleteListener
	{
		void onSeekComplete(IMediaPlayer mp);
	}


	/**
	 * 屏幕模式类
	 */
	public static enum VideoScalingMode
	{
		//默认，无特殊需求，按照小屏裁剪铺满（去除黑边）
		//大屏适应大小（等比例显示）的规则显示,直接设置这个值无效
		VIDEO_SCALING_MODE_DEFAULT(-1),
		//适应大小
		VIDEO_SCALING_MODE_SCALE_TO_FIT(0),
		//裁剪铺满
		VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING(1);

		private int mode;

		private VideoScalingMode(int mode)
		{
			this.mode = mode;
		}}

}
