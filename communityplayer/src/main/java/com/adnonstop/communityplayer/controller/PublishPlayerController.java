package com.adnonstop.communityplayer.controller;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * author:tdn
 * time:2019/6/26
 * description: 发布页
 */
public class PublishPlayerController extends CommunityPlayerController
{
	public PublishPlayerController(@NonNull Context context)
	{
		this(context, null);
	}

	public PublishPlayerController(@NonNull Context context, @Nullable AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public PublishPlayerController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init()
	{
		setFixedFullScreen(false);
		setLandscapeInFullScreen(false);
		setClearTinyScreen(true);
	}

	public void setPlayIcon(@DrawableRes int res)
	{
		mNormalPlayStateImageView.setImageResource(res);
	}

	@Override
	protected void onMuteModeChanged(boolean mute,boolean save)
	{
		if(mCommunityVideoPlayer != null)
		{
			if(mCommunityVideoPlayer.isFullScreen())
			{
				super.onMuteModeChanged(false,false);
			}
			else
			{
				super.onMuteModeChanged(true,false);
			}
		}
	}


}
