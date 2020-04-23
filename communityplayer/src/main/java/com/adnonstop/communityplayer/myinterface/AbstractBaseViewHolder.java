package com.adnonstop.communityplayer.myinterface;

import android.view.View;
import android.widget.ImageView;

import com.adnonstop.communityplayer.CommunityVideoPlayerView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * author:tdn
 * time:2019/4/24
 * description: RecyclerView 列表 BaseOpusHolder 需继承这个抽象类
 */
public abstract class AbstractBaseViewHolder extends RecyclerView.ViewHolder
{
	public AbstractBaseViewHolder(@NonNull View itemView)
	{
		super(itemView);
	}

	/**
	 * 获取视频高度
	 *
	 * @return
	 */
	public abstract float getVideoHeight();

	/**
	 * 获取所持有的view
	 *
	 * @return
	 */
	public abstract View getView();

	/**
	 * 获取视频播放view
	 *
	 * @return
	 */
	public abstract CommunityVideoPlayerView getCommunityVideoPlayerView();

	/**
	 * 是否是视频
	 *
	 * @return
	 */
	public abstract boolean isVideo();

	/**
	 * 获取图片
	 *
	 * @return
	 */
	public ImageView getCoverImageView() {
		return null;
	}
}
