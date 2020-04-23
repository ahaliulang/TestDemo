package com.adnonstop.communityplayer.bean;

/**
 * author:tdn
 * time:2019/7/30
 * description:
 */
public class PlayingCacheInfo
{

	private boolean enable; //是否可以边播边存。
	private String saveDir; //缓存的目录（绝对路径）
	private int maxDuration; //能缓存的单个视频最大长度（单位：秒）。如果单个视频超过这个值，就不缓存。
	private long maxSize; //缓存目录的所有缓存文件的总的最大大小（单位：MB）。如果超过则删除最旧文件，如果还是不够，则不缓存。

	public PlayingCacheInfo(boolean enable, String saveDir, int maxDuration, long maxSize)
	{
		this.enable = enable;
		this.saveDir = saveDir;
		this.maxDuration = maxDuration;
		this.maxSize = maxSize;
	}

	public boolean isEnable()
	{
		return enable;
	}

	public void setEnable(boolean enable)
	{
		this.enable = enable;
	}

	public String getSaveDir()
	{
		return saveDir;
	}

	public void setSaveDir(String saveDir)
	{
		this.saveDir = saveDir;
	}

	public int getMaxDuration()
	{
		return maxDuration;
	}

	public void setMaxDuration(int maxDuration)
	{
		this.maxDuration = maxDuration;
	}

	public long getMaxSize()
	{
		return maxSize;
	}

	public void setMaxSize(long maxSize)
	{
		this.maxSize = maxSize;
	}
}
