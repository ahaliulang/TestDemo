package com.adnonstop.communityplayer.bean;

import java.io.Serializable;

/**
 * Created by wxf on 2017/7/19.
 */

public class VideoPlayInfo implements Serializable
{
	public boolean voiceVisible = true;
	public String videoUrl;
	public boolean fixTime = true;
	public String coverUrl = "";
	public int coverWidth;
	public int coverHeight;
	public boolean forceLandscape = true;//强制横屏
	public String opusId; //作品id

	private VideoPlayInfo()
	{
	}

	/**
	 * 这是必须的参数
	 *
	 * @param videoUrl
	 * @param coverUrl
	 * @param coverWidth
	 * @param coverHeight
	 */
	public VideoPlayInfo(String videoUrl, String coverUrl, int coverWidth, int coverHeight)
	{
		this.videoUrl = videoUrl;
		this.coverUrl = coverUrl;
		this.coverWidth = coverWidth;
		this.coverHeight = coverHeight;
	}

	public VideoPlayInfo(String videoUrl, String coverUrl, int coverWidth, int coverHeight, boolean forceLandscape)
	{
		this.videoUrl = videoUrl;
		this.coverUrl = coverUrl;
		this.coverWidth = coverWidth;
		this.coverHeight = coverHeight;
		this.forceLandscape = forceLandscape;
	}

	public VideoPlayInfo(String videoUrl, String coverUrl, int coverWidth, int coverHeight, String opusId)
	{
		this.videoUrl = videoUrl;
		this.coverUrl = coverUrl;
		this.coverWidth = coverWidth;
		this.coverHeight = coverHeight;
		this.opusId = opusId;
	}
}
