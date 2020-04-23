# 社区播放器 v1.0.0
社区播放器封装成库，提供给不同的APP使用，主要用法
- 直接进入全屏播放需实现 IAutoPlay 接口
- BaseOpusHolder 需继承AbstractBaseViewHolder
- 视频自动播放的 ViewHolder 需实现 IPlayViewHolder，如 OpusArticleHolder，HotUpperViewHolder
- RecyclerView 的 HeaderView 需实现 IHeadView 接口

必要的初始化
- 需要在 BaseActivity 里面初始化 CommunityPlayerUtils.init() 工具了
- 需要在社区初始化同时初始化社区播放器 CommunityPlayer.init()，同时设置 App 的主题颜色 CommunityPlayer.setAppSkinColor();
