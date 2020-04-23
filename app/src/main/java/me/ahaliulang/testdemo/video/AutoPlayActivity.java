package me.ahaliulang.testdemo.video;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.adnonstop.communityplayer.CommunityVideoPlayerManager;
import com.adnonstop.communityplayer.CommunityVideoPlayerView;
import com.adnonstop.communityplayer.PlayerType;
import com.adnonstop.communityplayer.bean.VideoPlayInfo;
import com.adnonstop.communityplayer.controller.CommunityPlayerController;
import com.adnonstop.communityplayer.myinterface.IAutoPlay;

import me.ahaliulang.testdemo.R;


/**
 * Created by wxf on 2017/7/19.
 */
public class AutoPlayActivity extends AppCompatActivity implements IAutoPlay {
    private CommunityVideoPlayerView mCommunityVideoPlayerView1;
    private CommunityVideoPlayerView mCommunityVideoPlayerView2;
    private CommunityVideoPlayerView mCommunityVideoPlayerView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activty_auto_play);
        mCommunityVideoPlayerView1 = findViewById(R.id.video_01);
        mCommunityVideoPlayerView2 = findViewById(R.id.video_02);
        mCommunityVideoPlayerView3 = findViewById(R.id.video_03);

        VideoPlayInfo videoPlayInfo = new VideoPlayInfo("http://xsnsmv-alioss.adnonstop.com/xmen/20191204/14/16088988920191204141401597.mp4", "http://interphoto-xsns-alioss.adnonstop.com/xmen/20191204/14/16088988920191204141401423.jpg_m850-hd", 720, 1280);
        mCommunityVideoPlayerView1.setVideoPlayInfo(videoPlayInfo);
        mCommunityVideoPlayerView2.setVideoPlayInfo(videoPlayInfo);
        mCommunityVideoPlayerView3.setVideoPlayInfo(videoPlayInfo);
        mCommunityVideoPlayerView1.setPlayerType(PlayerType.Aliyun);
        mCommunityVideoPlayerView2.setPlayerType(PlayerType.Aliyun);
        mCommunityVideoPlayerView3.setPlayerType(PlayerType.Aliyun);
        CommunityPlayerController controller1 = new CommunityPlayerController(this);
//        controller1.setFixedFullScreen(false);
//        controller1.setLandscapeInFullScreen(videoPlayInfo.forceLandscape);

        mCommunityVideoPlayerView1.setController(controller1);
        mCommunityVideoPlayerView1.setMuteMode(false, false);
        mCommunityVideoPlayerView1.start(true);
        CommunityVideoPlayerManager.instance().setPageID(3).setCurrentVideoPlayer(mCommunityVideoPlayerView1);

        CommunityPlayerController controller2 = new CommunityPlayerController(this);
//        controller1.setFixedFullScreen(false);
//        controller2.setLandscapeInFullScreen(videoPlayInfo.forceLandscape);


        mCommunityVideoPlayerView2.setController(controller2);
        mCommunityVideoPlayerView2.setMuteMode(false, false);
        mCommunityVideoPlayerView2.start(true);
        CommunityVideoPlayerManager.instance().setPageID(2).setCurrentVideoPlayer(mCommunityVideoPlayerView2);


        CommunityPlayerController controller3 = new CommunityPlayerController(this);
//        controller1.setFixedFullScreen(false);
//        controller3.setLandscapeInFullScreen(videoPlayInfo.forceLandscape);

        mCommunityVideoPlayerView3.setController(controller3);
        mCommunityVideoPlayerView3.setMuteMode(false, false);
        CommunityVideoPlayerManager.instance().setPageID(1).setCurrentVideoPlayer(mCommunityVideoPlayerView3);


        CommunityVideoPlayerManager.instance().resumeVideoPlayer(1);
        CommunityVideoPlayerManager.instance().resumeVideoPlayer(2);
        CommunityVideoPlayerManager.instance().resumeVideoPlayer(3);

    }


    @Override
    protected void onResume() {
        super.onResume();
        CommunityVideoPlayerManager.instance().setPageID(this.hashCode()).resumeVideoPlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CommunityVideoPlayerManager.instance().setPageID(this.hashCode()).suspendVideoPlayer();
    }

    @Override
    protected void onDestroy() {
        CommunityVideoPlayerManager.instance().setPageID(this.hashCode()).releaseVideoPlayer();
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        AutoPlayActivity.this.finish();
    }

}
