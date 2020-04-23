package com.adnonstop.communityplayer.myinterface;

import com.adnonstop.communityplayer.bean.VideoPlayInfo;

import java.io.FileDescriptor;

/**
 * author:tdn
 * time:2018/12/25
 * description: 抽象接口，用于规范
 */
public interface ICommunityVideoPlayer
{
	/**
	 * 设置视频 url
	 *
	 * @param url
	 */
	void setVideoUrl(String url);


	/**
	 * 设置视频文件描述符
	 *
	 * @param fileDescriptor
	 */
	void setVideoFileDescriptor(FileDescriptor fileDescriptor);


	/**
	 * 设置视频封面
	 *
	 * @param url
	 */
	void setCoverUrl(String url);


	/**
	 * 设置视频播放的信息
	 *
	 * @param videoPlayInfo
	 */
	void setVideoPlayInfo(VideoPlayInfo videoPlayInfo);


	/***
	 * 设置页面id
	 */
	void setPageID(int id);

	/**
	 * 开始播放
	 *
	 * @param auto 是否自动播放
	 */
	void start(boolean auto);

	/**
	 * 从指定的位置开始播放
	 *
	 * @param position 播放位置
	 */
	void start(long position);


	/**
	 * 重新播放
	 */
	void restart();

	/**
	 * 暂停播放
	 */
	void pause();


	/**
	 * 滑动到指定的位置继续播放
	 *
	 * @param position
	 */
	void seekTo(long position);


	/**
	 * 设置音量
	 *
	 * @param volume 音量值
	 */
	void setVolume(int volume);


	/**
	 * 设置静音模式
	 *
	 * @param mute true 静音，false 反之
	 */
	void setMuteMode(boolean mute, boolean save);


	/**
	 * 开始播放时，是否从上一次的位置继续播放
	 *
	 * @param latestPosition
	 */
	void continueFromLatestPosition(boolean latestPosition);

	/******************************当前播放器状态*****************************/
	boolean isIdle();

	boolean isPreparing();

	boolean isPrepared();

	boolean isPlaying();

	boolean isBufferingPlaying();

	boolean isBufferingPaused();

	boolean isPaused();

	boolean isError();

	boolean isCompleted();

	boolean isLooping();
	/******************************当前播放器状态*****************************/

	/******************************播放器模式*****************************/
	boolean isFullScreen();

	boolean isTinyWindow();

	boolean isNormal();
	/******************************播放器模式*****************************/


	/**
	 * 是否自动播放
	 *
	 * @return
	 */
	boolean isAutoPlay();


	/**
	 * 是否静音
	 *
	 * @return
	 */
	boolean isMute();


	/**
	 * 获取最大音量
	 *
	 * @return 最大音量值
	 */
	int getMaxVolume();

	/**
	 * 获取当前音量
	 *
	 * @return 当前音量值
	 */
	int getVolume();


	/**
	 * 获取办法给总时长，毫秒
	 *
	 * @return 视频总时长ms
	 */
	long getDuration();

	/**
	 * 获取当前播放的位置，毫秒
	 *
	 * @return 当前播放位置，ms
	 */
	long getCurrentPosition();


	/**
	 * 进入全屏模式
	 *
	 * @param landscape true 横屏，false 反之
	 */
	void enterFullScreen(boolean landscape);

	/**
	 * 退出全屏模式
	 *
	 * @return true 退出
	 */
	boolean exitFullScreen();


	/**
	 * 进入小窗口模式
	 */
	void enterTinyWindow();

	/**
	 * 退出小窗口模式
	 *
	 * @return true 退出小窗口
	 */
	boolean exitTinyWindow();


	/**
	 * 获取视频缓冲百分比
	 *
	 * @return 缓冲白百分比
	 */
	int getBufferPercentage();

	/**
	 * 释放ICommunityVideoPlayer，释放后，内部的播放器被释放掉，同时如果在全屏、小窗口模式下都会退出
	 * 并且控制器的UI也应该恢复到最初始的状态.
	 */
	void release();


}
