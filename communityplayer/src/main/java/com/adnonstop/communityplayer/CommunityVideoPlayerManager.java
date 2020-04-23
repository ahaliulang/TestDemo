package com.adnonstop.communityplayer;

import android.util.SparseArray;

import com.adnonstop.communityplayer.listener.IVideoPlayerListener;
import com.adnonstop.communityplayer.util.SPUtils;

/**
 * author:tdn
 * time:2018/12/25
 * description:视频播放器管理器
 */
public class CommunityVideoPlayerManager
{

	//CommunityVideoPlayerView 集合，每个页面最多有一个 CommunityVideoPlayerView 实例
	private SparseArray<CommunityVideoPlayerView> mViewSparseArray = new SparseArray<>();
	private SparseArray<Boolean> mPauseSparseArray = new SparseArray<>();
	private int mCurrentPageID;

	private CommunityVideoPlayerManager()
	{
	}

	private static CommunityVideoPlayerManager sInstance;

	public static synchronized CommunityVideoPlayerManager instance()
	{
		if(sInstance == null)
		{
			sInstance = new CommunityVideoPlayerManager();
		}
		return sInstance;
	}

	public CommunityVideoPlayerManager setPageID(int id)
	{
		mCurrentPageID = id;
		return this;
	}

	public CommunityVideoPlayerView getCurrentVideoPlayerView()
	{
		return mViewSparseArray.get(mCurrentPageID);
	}

	public CommunityVideoPlayerView getVideoPlayer(int pageId)
	{
		return mViewSparseArray.get(pageId);
	}


	public void setCurrentVideoPlayer(CommunityVideoPlayerView videoPlayer)
	{
		if(videoPlayer != mViewSparseArray.get(mCurrentPageID))
		{
			releaseVideoPlayer();
			mViewSparseArray.put(mCurrentPageID, videoPlayer);
		}
	}


	/**
	 * 停止掉某个页面的播放
	 *
	 * @param pageID
	 */
	public void suspendVideoPlayer(int pageID)
	{
		mPauseSparseArray.put(pageID, true);
		CommunityVideoPlayerView communityVideoPlayerView = mViewSparseArray.get(pageID);
		if(communityVideoPlayerView != null && (communityVideoPlayerView.isPlaying() || communityVideoPlayerView.isBufferingPlaying()))
		{
			communityVideoPlayerView.pause();
		}
		//有可能正在准备播放，这时如果跳转其他页面会造成后台继续播放，所以把其释放掉
		else if(communityVideoPlayerView != null && !communityVideoPlayerView.isPaused() && !communityVideoPlayerView.isBufferingPaused())
		{
			communityVideoPlayerView.release();
		}
	}

	public void suspendVideoPlayer()
	{
		suspendVideoPlayer(mCurrentPageID);
	}


	public void resumeVideoPlayer(int pageID)
	{
		mPauseSparseArray.put(pageID, false);
		final CommunityVideoPlayerView communityVideoPlayerView = mViewSparseArray.get(pageID);
		if(communityVideoPlayerView != null)
		{
			if(communityVideoPlayerView.isPaused() || communityVideoPlayerView.isBufferingPaused())
			{
				//调整位置
				if(communityVideoPlayerView.getVideoPlayInfo() != null)
				{
					long playerPosition = SPUtils.getPlayerPosition(communityVideoPlayerView.getContext(), communityVideoPlayerView.getVideoPlayInfo().videoUrl);
					if(playerPosition > 0 && playerPosition < communityVideoPlayerView.getDuration() && communityVideoPlayerView.getDuration() != playerPosition)
					{
						communityVideoPlayerView.setOnSeekCompleteListener(new IVideoPlayerListener.OnSeekCompleteListener()
						{
							@Override
							public void onSeekComplete()
							{
								//如果当前是在前台，有在seek的过程中进入后台
								if(mPauseSparseArray == null || mPauseSparseArray.get(communityVideoPlayerView.getPageID()) == null || !mPauseSparseArray.get(communityVideoPlayerView.getPageID()))
								{
									communityVideoPlayerView.restart();
								}
							}
						});
						communityVideoPlayerView.seekTo(playerPosition);
					}
					else
					{
						communityVideoPlayerView.restart();
					}
				}
				else
				{
					communityVideoPlayerView.restart();
				}
			}
			else if(!communityVideoPlayerView.isBufferingPlaying() && !communityVideoPlayerView.isPlaying())
			{
				communityVideoPlayerView.release();
				communityVideoPlayerView.continueFromLatestPosition(true);
				communityVideoPlayerView.start(true);
			}
			if(communityVideoPlayerView.getVideoPlayInfo() != null)
			{
				SPUtils.removePlayerPosition(communityVideoPlayerView.getContext(), communityVideoPlayerView.getVideoPlayInfo().videoUrl);
			}
		}
	}

	public void resumeVideoPlayer()
	{
		resumeVideoPlayer(mCurrentPageID);
	}

	public void releaseVideoPlayer(int pageID)
	{
		CommunityVideoPlayerView communityVideoPlayerView = mViewSparseArray.get(pageID);
		if(communityVideoPlayerView != null)
		{
			communityVideoPlayerView.release();
		}
		if(mViewSparseArray != null)
		{
			mViewSparseArray.remove(pageID);
		}
	}


	public void releasePageID(int pageId)
	{
		if(mViewSparseArray != null)
		{
			mViewSparseArray.remove(pageId);
		}
	}

	public void releaseVideoPlayer()
	{
		releaseVideoPlayer(mCurrentPageID);
	}

	public void releaseAll()
	{
		for(int i = 0; i < mViewSparseArray.size(); i++)
		{
			CommunityVideoPlayerView communityVideoPlayerView = mViewSparseArray.valueAt(i);
			if(communityVideoPlayerView != null)
			{
				communityVideoPlayerView.release();
				mViewSparseArray.remove(mCurrentPageID);
			}
		}
		mViewSparseArray.clear();
		mPauseSparseArray.clear();
	}

	public boolean onBackPressed()
	{
		for(int i = 0; i < mViewSparseArray.size(); i++)
		{
			CommunityVideoPlayerView communityVideoPlayerView = mViewSparseArray.valueAt(i);
			if(communityVideoPlayerView != null)
			{
				if(communityVideoPlayerView.isFullScreen())
				{
					return communityVideoPlayerView.exitFullScreen();
				}
				else if(communityVideoPlayerView.isTinyWindow())
				{
					return communityVideoPlayerView.exitTinyWindow();
				}
			}
		}
		return false;
	}

	public SparseArray<Boolean> getPauseSparseArray()
	{
		return mPauseSparseArray;
	}

}
