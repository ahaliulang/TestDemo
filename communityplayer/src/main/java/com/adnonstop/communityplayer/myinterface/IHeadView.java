package com.adnonstop.communityplayer.myinterface;

import com.adnonstop.communityplayer.CommunityVideoPlayerView;

/**
 * author:tdn
 * time:2019/4/24
 * description: 作品详情头部需实现这个接口
 */
public interface IHeadView
{
	/**
	 * 获取视频播放view
	 *
	 * @return
	 */
	CommunityVideoPlayerView getCommunityVideoPlayerView();


	/**
	 * 是否是视频
	 */
	boolean isVideo();
}
