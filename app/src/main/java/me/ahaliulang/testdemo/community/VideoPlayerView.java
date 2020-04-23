package me.ahaliulang.testdemo.community;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adnonstop.communityplayer.CommunityVideoPlayerView;
import com.adnonstop.communityplayer.bean.VideoPlayInfo;
import com.adnonstop.communityplayer.listener.IVideoPlayerListener;

/**
 * author:tdn
 * time:2019/7/23
 * description:派生社区播放器，做一些公共逻辑
 */
public class VideoPlayerView extends CommunityVideoPlayerView
{
	public VideoPlayerView(@NonNull Context context)
	{
		this(context, null);
	}

	public VideoPlayerView(@NonNull Context context, @Nullable AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public VideoPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}


	@Override
	public void setVideoPlayInfo(VideoPlayInfo videoPlayInfo)
	{
		super.setVideoPlayInfo(videoPlayInfo);
		if(!TextUtils.isEmpty(videoPlayInfo.opusId))
		{
		}
	}

}
























