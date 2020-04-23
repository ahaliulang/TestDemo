package com.adnonstop.communityplayer;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import com.adnonstop.communityplayer.controller.CommunityPlayerController;
import com.adnonstop.communityplayer.myinterface.AbstractBaseViewHolder;
import com.adnonstop.communityplayer.myinterface.IHeadView;
import com.adnonstop.communityplayer.myinterface.IPlayViewHolder;
import com.adnonstop.communityplayer.util.CommunityPlayerUtils;
import com.adnonstop.communityplayer.util.LogUtil;
import com.adnonstop.communityplayer.util.NetUtils;
import com.adnonstop.communityplayer.util.NetWatchdogUtils;
import com.adnonstop.communityplayer.util.SPUtils;
import com.bumptech.glide.load.resource.gif.GifDrawable;

import java.lang.ref.WeakReference;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * author:tdn
 * time:2018/12/27
 * description: recyclerview 列表视频播放帮助类
 */
public class CommunityListVideoHelper
{


	protected CommunityVideoPlayerView mCommunityVideoPlayerView;//当前播放器view
	protected AbstractBaseViewHolder mBaseViewHolder;//正在播放的那个item
	protected WeakReference<Object> mObjectWeakReference;
	private IHeadView mHeaderView;
	private NetWatchdogUtils mNetWatchDog;//网络切换监听
	private ViewGroup mViewGroup;//列表view
	private int mVisibleItemCount;//列表可见的item数据


	public CommunityListVideoHelper(Object obj)
	{
		mObjectWeakReference = new WeakReference<>(obj);
		CommunityVideoPlayerManager.instance().setPageID(mObjectWeakReference.get().hashCode());
		mNetWatchDog = new NetWatchdogUtils(CommunityPlayer.getApplicationContext());
		mNetWatchDog.startWatch();
		mNetWatchDog.setNetChangeListener(new NetWatchdogUtils.NetChangeListener()
		{
			@Override
			public void onWifiTo4G()
			{
				if(CommunityPlayer.APP_CODE == CommunityPlayer.INTERPHOTO)
				{
					if(mViewGroup != null && mObjectWeakReference != null && mObjectWeakReference.get() != null)
					{
						updateController();
						boolean autoPlayIn4G = (boolean)SPUtils.get(mViewGroup.getContext(), SPUtils.GLOBAL_AUTO_PLAY_IN_4G, false);
						if(!autoPlayIn4G)
						{
							CommunityVideoPlayerManager.instance().setPageID(mObjectWeakReference.get().hashCode()).releaseVideoPlayer();
						}
					}
				}
			}

			@Override
			public void on4GToWifi()
			{
				if(CommunityPlayer.APP_CODE == CommunityPlayer.INTERPHOTO)
				{
					if(mViewGroup != null && mObjectWeakReference != null && mObjectWeakReference.get() != null)
					{
						updateController();
						boolean autoPlayIn4G = (boolean)SPUtils.get(mViewGroup.getContext(), SPUtils.GLOBAL_AUTO_PLAY_IN_4G, false);
						if(!autoPlayIn4G)
						{
							updateVideoState(mViewGroup, mVisibleItemCount);
						}
					}
				}
			}

			@Override
			public void onReNetConnected(boolean isReconnect)
			{

			}

			@Override
			public void onNetUnConnected()
			{

			}
		});
	}

	/**
	 * 控制滑动期间的播放停止
	 */
	public void onScrolled()
	{
		if(mCommunityVideoPlayerView != null && mBaseViewHolder != null)
		{
			Rect rect = new Rect();
			mCommunityVideoPlayerView.getLocalVisibleRect(rect);
			float distanceOne = getTopDistance(mBaseViewHolder);
			if(rect.height() < distanceOne)
			{
				CommunityVideoPlayerManager.instance().setPageID(mObjectWeakReference.get().hashCode()).releaseVideoPlayer();
				mBaseViewHolder = null;
			}
		}
		else if(mHeaderView != null && mHeaderView.getCommunityVideoPlayerView() != null && mHeaderView.getCommunityVideoPlayerView().getVisibility() == View.VISIBLE && (mHeaderView.getCommunityVideoPlayerView() == mCommunityVideoPlayerView || mCommunityVideoPlayerView == null))
		{
			Rect rect = new Rect();
			mHeaderView.getCommunityVideoPlayerView().getLocalVisibleRect(rect);
			//VideoView的可视高度小于五分之一时，暂停播放
			float distanceOne = mHeaderView.getCommunityVideoPlayerView().getHeight() * (1.0f / 5);
			if(rect.height() < distanceOne)
			{
				CommunityVideoPlayerManager.instance().setPageID(mObjectWeakReference.get().hashCode()).releaseVideoPlayer();
			}
		}


	}

	protected static final int RECYCLER_MIN_VELOCITY_Y = 8000;

	public void onFling(int velocityY)
	{
		//当fling的 velocityY 值大于 8000 时，释放播放器
		if(Math.abs(velocityY) > RECYCLER_MIN_VELOCITY_Y)
		{
			CommunityVideoPlayerManager.instance().setPageID(mObjectWeakReference.get().hashCode()).releaseVideoPlayer();
			mBaseViewHolder = null;
		}
	}

	private float getTopDistance(AbstractBaseViewHolder item)
	{
		float videoHeight = item.getVideoHeight();
		float distance = (videoHeight <= 0 ? item.getView().getHeight() : videoHeight) * (1.0f / 5);
		return distance;
	}


	/**
	 * @param listView
	 * @param visibleItemCount
	 */
	public void updateVideoState(ViewGroup listView, int visibleItemCount)
	{
		if(!(listView instanceof RecyclerView) || visibleItemCount <= 0)
		{
			return;
		}
		mViewGroup = listView;
		mVisibleItemCount = visibleItemCount;
		RecyclerView.LayoutManager layoutManager = ((RecyclerView)mViewGroup).getLayoutManager();
		if(layoutManager instanceof LinearLayoutManager)
		{
			mVisibleItemCount = ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition() - ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition() + 1;
		}
		try
		{
			RecyclerView recyclerView = (RecyclerView)listView;
			//根据可见item播放视频
			if(visibleItemCount == 1)
			{
				View view = listView.getChildAt(0);
				if(view == null)
				{
					return;
				}
				//作品详情头部布局
				else if(view instanceof IHeadView)
				{
					playVideo((IHeadView)view);
				}
				RecyclerView.ViewHolder childViewHolder = recyclerView.getChildViewHolder(view);
				playVideo(childViewHolder);
			}
			else if(visibleItemCount == 2)
			{
				View firstItem = listView.getChildAt(0);
				RecyclerView.ViewHolder fistViewHolder = recyclerView.getChildViewHolder(firstItem);
				View secondItem = listView.getChildAt(1);
				RecyclerView.ViewHolder secondViewHolder = recyclerView.getChildViewHolder(secondItem);

				//两个都是作品OpusArticleHolder
				if((fistViewHolder instanceof IPlayViewHolder && secondViewHolder instanceof IPlayViewHolder))
				{
					AbstractBaseViewHolder firstArticleHolder = ((AbstractBaseViewHolder)fistViewHolder);
					AbstractBaseViewHolder secondArticleHolder = ((AbstractBaseViewHolder)secondViewHolder);
					boolean onePlay = false;
					boolean twoPlay = false;
					Rect rectOne = new Rect();
					Rect rectTwo = new Rect();
					firstArticleHolder.getCommunityVideoPlayerView().getLocalVisibleRect(rectOne);
					secondArticleHolder.getCommunityVideoPlayerView().getLocalVisibleRect(rectTwo);
					float distanceOne = getTopDistance(firstArticleHolder);
					float distanceTwo = getTopDistance(secondArticleHolder);
					LogUtil.d(rectOne.height() + " " + distanceOne + " " + rectOne.top + " " + CommunityPlayerUtils.getScreenH() * 2 / 3);
					if(rectOne.height() >= distanceOne && rectOne.top >= 0 && rectOne.top < CommunityPlayerUtils.getScreenH() * 2 / 3 && firstArticleHolder.isVideo())
					{
						onePlay = true;
					}
					if(rectTwo.height() >= distanceTwo && rectTwo.top == 0 && secondArticleHolder.isVideo())
					{
						twoPlay = true;
					}

					if(onePlay && twoPlay)
					{
						if(rectOne.height() >= rectTwo.height() || rectOne.height() >= firstArticleHolder.getVideoHeight())
						{
							playVideo(firstArticleHolder);
						}
						else
						{
							playVideo(secondArticleHolder);
						}
					}
					else if(onePlay && !twoPlay)
					{
						playVideo(firstArticleHolder);
					}
					else if(!onePlay && twoPlay)
					{
						playVideo(secondArticleHolder);
					}
					else
					{
						CommunityVideoPlayerManager.instance().setPageID(mObjectWeakReference.get().hashCode()).releaseVideoPlayer();
					}
				}
				//第一个是OpusArticleHolder，第二个不是,有可能是加载更多的ViewHolder
				else if(fistViewHolder instanceof IPlayViewHolder)
				{
					playVideo(fistViewHolder);
				}
				//其他情况
				else
				{
					//第一个 view 是 HeaderView,而且第二个是 OpusArticleHolder
					if(firstItem instanceof IHeadView && secondViewHolder instanceof IPlayViewHolder)
					{
						mHeaderView = (IHeadView)firstItem;
						AbstractBaseViewHolder secondArticleHolder = (AbstractBaseViewHolder)secondViewHolder;
						boolean onePlay = false;
						boolean twoPlay = false;
						Rect rectOne = new Rect();
						Rect rectTwo = new Rect();
						mHeaderView.getCommunityVideoPlayerView().getLocalVisibleRect(rectOne);
						secondArticleHolder.getCommunityVideoPlayerView().getLocalVisibleRect(rectTwo);
						float distanceOne = mHeaderView.getCommunityVideoPlayerView().getHeight() * (1.0f / 5);
						float distanceTwo = getTopDistance(secondArticleHolder);
						if(rectOne.height() >= distanceOne && rectOne.top >= 0 && rectOne.top < CommunityPlayerUtils.getScreenH() * 2 / 3 && mHeaderView.isVideo())
						{
							onePlay = true;
						}
						if(rectTwo.height() >= distanceTwo && rectTwo.top == 0 && secondArticleHolder.isVideo())
						{
							twoPlay = true;
						}
						if(onePlay && twoPlay)
						{
							playVideo(mHeaderView);
						}
						else if(onePlay && !twoPlay)
						{
							playVideo(mHeaderView);
						}
						else if(!onePlay && twoPlay)
						{
							playVideo(secondArticleHolder);
						}
						else
						{
							CommunityVideoPlayerManager.instance().setPageID(mObjectWeakReference.get().hashCode()).releaseVideoPlayer();
						}

					}
					else
					{
						playVideo(secondViewHolder);
					}
				}
			}
			else
			{
				boolean isPlay = false;
				View firstView = listView.getChildAt(0);
				if(firstView != null && recyclerView.getChildViewHolder(firstView) instanceof IPlayViewHolder)
				{
					AbstractBaseViewHolder firstArticleHolder = (AbstractBaseViewHolder)recyclerView.getChildViewHolder(firstView);
					Rect rectOne = new Rect();
					firstArticleHolder.getCommunityVideoPlayerView().getLocalVisibleRect(rectOne);
					if(firstArticleHolder.isVideo() && rectOne.top >= 0 && rectOne.height() >= firstArticleHolder.getCommunityVideoPlayerView().getHeight())
					{
						isPlay = true;
						playVideo(firstArticleHolder);
					}
				}
				if(!isPlay)
				{
					//一般来说一屏最多容纳 3 个 item，故取中间那个item
					View middleView = listView.getChildAt(1);
					if(middleView != null && recyclerView.getChildViewHolder(middleView) instanceof IPlayViewHolder)
					{
						AbstractBaseViewHolder middleArticleHolder = (AbstractBaseViewHolder)recyclerView.getChildViewHolder(middleView);
						if(middleArticleHolder.isVideo())
						{
							mBaseViewHolder = middleArticleHolder;
							mCommunityVideoPlayerView = middleArticleHolder.getCommunityVideoPlayerView();
							mCommunityVideoPlayerView.start(true);
						}
						else
						{
							CommunityVideoPlayerManager.instance().setPageID(mObjectWeakReference.get().hashCode()).releaseVideoPlayer();
						}
					}
					//如果中间那个不是可播放的item，则遍历可播放的view进行播放
					else
					{
						CommunityVideoPlayerManager.instance().setPageID(mObjectWeakReference.get().hashCode()).releaseVideoPlayer();
						for(int i = 0; i < visibleItemCount; i++)
						{
							View childView = listView.getChildAt(i);
							if(childView != null && recyclerView.getChildViewHolder(childView) instanceof IPlayViewHolder)
							{
								AbstractBaseViewHolder childViewHolder = (AbstractBaseViewHolder)recyclerView.getChildViewHolder(childView);
								Rect rect = new Rect();
								childViewHolder.getCommunityVideoPlayerView().getLocalVisibleRect(rect);
								float distance = getTopDistance(childViewHolder);
								if(rect.height() >= distance && rect.top >= 0 && rect.top < CommunityPlayerUtils.getScreenH() * 2 / 3 && childViewHolder.isVideo())
								{
									mBaseViewHolder = childViewHolder;
									mCommunityVideoPlayerView = childViewHolder.getCommunityVideoPlayerView();
									mCommunityVideoPlayerView.start(true);
									break;
								}
							}
						}
					}
				}

			}
		}
		catch(Exception e)
		{
			CommunityVideoPlayerManager.instance().setPageID(mObjectWeakReference.get().hashCode()).releaseVideoPlayer();
			e.printStackTrace();
		}
	}

	/**
	 * 播放视频当前 viewholder 的视频
	 *
	 * @param viewHolder 当前viewholder
	 */
	private void playVideo(RecyclerView.ViewHolder viewHolder)
	{
		if(viewHolder instanceof AbstractBaseViewHolder)
		{
			AbstractBaseViewHolder item = (AbstractBaseViewHolder)viewHolder;
			CommunityVideoPlayerView communityVideoPlayerView = item.getCommunityVideoPlayerView();
			if(item.isVideo())
			{
				if(mBaseViewHolder != item)
				{
					CommunityVideoPlayerManager.instance().setPageID(mObjectWeakReference.get().hashCode()).releaseVideoPlayer();
				}
				mBaseViewHolder = item;
				playVideo(communityVideoPlayerView);
			}
		}
	}

	/**
	 * 播放头部视频
	 *
	 * @param
	 */
	private void playVideo(IHeadView headerView)
	{
		if(headerView != null)
		{
			CommunityVideoPlayerView communityVideoPlayerView = headerView.getCommunityVideoPlayerView();
			if(communityVideoPlayerView != null && headerView.isVideo())
			{
				//view 全部或者部分可见
				if(communityVideoPlayerView.getLocalVisibleRect(new Rect()))
				{
					playVideo(communityVideoPlayerView);
				}
				//view 全部不可见
				else
				{
					CommunityVideoPlayerManager.instance().setPageID(mObjectWeakReference.get().hashCode()).releaseVideoPlayer();
				}
			}
		}
	}


	public void controlGifState(RecyclerView listView, int visibleItemCount)
	{
		if(visibleItemCount > 1)
		{
			for(int index = 0; index < visibleItemCount; index++)
			{
				try
				{
					RecyclerView.ViewHolder view = listView.getChildViewHolder(listView.getChildAt(index));
					if(view instanceof AbstractBaseViewHolder)
					{
						AbstractBaseViewHolder item = (AbstractBaseViewHolder)view;
						Drawable d = item.getCoverImageView().getDrawable();

						if(item.getView().getTop() > -400 && item.getView().getTop() < 400)
						{
							if(d instanceof GifDrawable)
							{
								((GifDrawable)d).start();
							}
						}
						else
						{
							if(d instanceof GifDrawable)
							{
								((GifDrawable)d).stop();
							}
						}
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}


	/**
	 * 根据 CommunityVideoPlayerView 播放视频
	 *
	 * @param communityVideoPlayerView
	 */
	private void playVideo(CommunityVideoPlayerView communityVideoPlayerView)
	{
		if(communityVideoPlayerView == null)
		{
			updateVideoState(mViewGroup, mVisibleItemCount);
			return;
		}
		mCommunityVideoPlayerView = communityVideoPlayerView;
		CommunityVideoPlayerManager.instance().setPageID(mObjectWeakReference.get().hashCode()).setCurrentVideoPlayer(mCommunityVideoPlayerView);
		//tryToPlay(type);
		mCommunityVideoPlayerView.start(true);
	}


	public void resume()
	{
		CommunityVideoPlayerManager.instance().setPageID(mObjectWeakReference.get().hashCode()).resumeVideoPlayer();
	}

	public void pause()
	{
		CommunityVideoPlayerManager.instance().setPageID(mObjectWeakReference.get().hashCode()).suspendVideoPlayer();
	}

	public void release()
	{
		CommunityVideoPlayerManager.instance().setPageID(mObjectWeakReference.get().hashCode()).releaseVideoPlayer();
		if(mBaseViewHolder != null)
		{
			mBaseViewHolder = null;
		}
		if(mNetWatchDog != null)
		{
			mNetWatchDog.stopWatch();
		}
	}


	/**
	 * 检查4G状态下是否自动播放
	 *
	 * @param type
	 * @return
	 */
	private boolean checkAutoPlayIn4G(int type)
	{
		boolean result = false;
		if(mCommunityVideoPlayerView != null)
		{
			boolean autoPlayIn4G = (boolean)SPUtils.get(mCommunityVideoPlayerView.getContext(), SPUtils.GLOBAL_AUTO_PLAY_IN_4G, false);
			if(autoPlayIn4G)
			{
				((CommunityPlayerController)mCommunityVideoPlayerView.getController()).setCoverColor(Color.parseColor("#00000000"));
				((CommunityPlayerController)mCommunityVideoPlayerView.getController()).setPlayIconVisible(false);
				/*if(type == TYPE_START)
				{
					mCommunityVideoPlayerView.start(true);
				}
				else if(type == TYPE_RESUME)
				{
					CommunityVideoPlayerManager.instance().setPageID(mObjectWeakReference.get().hashCode()).resumeVideoPlayer();
				}*/
				result = true;
			}
		}
		return result;
	}


//	/**
//	 * 根据{@link CommunityPlayer#isAutoPlayInList()}
//	 * 决定是否自动播放
//	 *
//	 * @param type
//	 */
//	private void tryToPlay(int type)
//	{
//		//wifi状态下自动播放
//		if(mCommunityVideoPlayerView != null && NetUtils.getNetType(mCommunityVideoPlayerView.getContext()) == NetUtils.TYPE_WIFI)
//		{
//			CommunityPlayer.setAutoPlayInList(true);
//		}
//		//自动播放
//		if(CommunityPlayer.isAutoPlayInList())
//		{
//			((CommunityPlayerController)mCommunityVideoPlayerView.getController()).setCoverColor(Color.parseColor("#00000000"));
//			((CommunityPlayerController)mCommunityVideoPlayerView.getController()).setPlayIconVisible(false);
//			if(type == TYPE_START)
//			{
//				mCommunityVideoPlayerView.start(true);
//			}
//			else if(type == TYPE_RESUME)
//			{
//				CommunityVideoPlayerManager.instance().setPageID(mObjectWeakReference.get().hashCode()).resumeVideoPlayer();
//			}
//		}
//		//不自动播放，显示播放按钮和背景
//		else
//		{
//			((CommunityPlayerController)mCommunityVideoPlayerView.getController()).setCoverColor(Color.parseColor("#4d000000"));
//			((CommunityPlayerController)mCommunityVideoPlayerView.getController()).setPlayIconVisible(true);
//		}
//	}

//	/**
//	 * 显示流量播放视频弹框
//	 *
//	 * @param type
//	 */
//	private void showNetTipsDialog(final int type)
//	{
//		if(mCommunityVideoPlayerView == null && mObjectWeakReference != null)
//		{
//			mCommunityVideoPlayerView = CommunityVideoPlayerManager.instance().getVideoPlayer(mObjectWeakReference.get().hashCode());
//		}
//		if(mCommunityVideoPlayerView == null)
//		{
//			return;
//		}
//		//显示暂停的UI
//		pause();
//		if(mCommunityVideoPlayerView.getController() instanceof CommunityPlayerController)
//		{
//			((CommunityPlayerController)mCommunityVideoPlayerView.getController()).setCoverColor(Color.parseColor("#4d000000"));
//			((CommunityPlayerController)mCommunityVideoPlayerView.getController()).setPlayIconVisible(true);
//		}
//		CommunityPlayer.setShowingDialog(true);
//		DialogUtils.showRoundedRectangleDialog(mCommunityVideoPlayerView.getContext(), null, mCommunityVideoPlayerView.getContext().getString(R.string.wifi_dialog_tips), mCommunityVideoPlayerView.getContext().getString(R.string.dialog_cancel), mCommunityVideoPlayerView.getContext().getString(R.string.dialog_confirm), false, new TextDialog.OnDialogItemClickListener()
//		{
//			@Override
//			public void onButtonLeft(TextDialog dialog)
//			{
//				dialog.dismiss();
//				CommunityPlayer.setAutoPlayInList(false);
//				CommunityPlayer.setDetectedFirstPlay(true);
//				//恢复状态
//				CommunityVideoPlayerManager.instance().getPauseSparseArray().put(mObjectWeakReference.get().hashCode(), false);
//			}
//
//			@Override
//			public void onButtonRight(TextDialog dialog)
//			{
//				dialog.dismiss();
//				CommunityPlayer.setAutoPlayInList(true);
//				CommunityPlayer.setDetectedFirstPlay(true);
//				if(type == TYPE_START)
//				{
//					mCommunityVideoPlayerView.start(true);
//				}
//				else if(type == TYPE_RESUME)
//				{
//					CommunityVideoPlayerManager.instance().setPageID(mObjectWeakReference.get().hashCode()).resumeVideoPlayer();
//				}
//			}
//
//			@Override
//			public void onDismiss()
//			{
//				CommunityPlayer.setAutoPlayInList(false);
//				CommunityPlayer.setShowingDialog(false);
//			}
//		});
//	}


	private void updateController()
	{
		if(!(mViewGroup instanceof RecyclerView) || mVisibleItemCount <= 0)
		{
			return;
		}
		RecyclerView recyclerView = (RecyclerView)mViewGroup;
		boolean autoPlayIn4G = (boolean)SPUtils.get(mViewGroup.getContext(), SPUtils.GLOBAL_AUTO_PLAY_IN_4G, false);
		if(autoPlayIn4G || NetUtils.getNetType(mViewGroup.getContext()) == NetUtils.TYPE_WIFI)
		{
			for(int i = 0; i < mVisibleItemCount; i++)
			{
				View child = recyclerView.getChildAt(i);
				if(child instanceof CommunityVideoPlayerView && ((CommunityVideoPlayerView)child).getController() instanceof CommunityPlayerController)
				{
					CommunityVideoPlayerView communityVideoPlayerView = (CommunityVideoPlayerView)child;
					((CommunityPlayerController)communityVideoPlayerView.getController()).setCoverColor(Color.parseColor("#00000000"));
					((CommunityPlayerController)communityVideoPlayerView.getController()).setPlayIconVisible(false);
				}
			}
		}
		else
		{
			for(int i = 0; i < mVisibleItemCount; i++)
			{
				View child = recyclerView.getChildAt(i);
				if(child instanceof CommunityVideoPlayerView && ((CommunityVideoPlayerView)child).getController() instanceof CommunityPlayerController)
				{
					CommunityVideoPlayerView communityVideoPlayerView = (CommunityVideoPlayerView)child;
					((CommunityPlayerController)communityVideoPlayerView.getController()).setCoverColor(Color.parseColor("#4d000000"));
					((CommunityPlayerController)communityVideoPlayerView.getController()).setPlayIconVisible(true);
				}
			}
		}

	}
}
