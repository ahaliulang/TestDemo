package me.ahaliulang.testdemo.community;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adnonstop.communityplayer.bean.VideoPlayInfo;
import com.adnonstop.communityplayer.controller.CommunityPlayerController;

import java.io.Serializable;

import me.ahaliulang.testdemo.R;
import me.ahaliulang.testdemo.utils.Utils;

/**
 * author:tdn
 * time:2019/12/10
 * description:
 */
public class VideoHolder extends RecyclerView.ViewHolder {

    private VideoPlayerView mVideoPlayerView;

    private int overrideWidth = 700, overrideHeight = 700;

    public VideoHolder(@NonNull View itemView) {
        super(itemView);
        mVideoPlayerView = itemView.findViewById(R.id.video_player_view);
    }


    public void setData(VideoPlayInfo videoPlayInfo) {
        mVideoPlayerView.setVideoPlayInfo(videoPlayInfo);
        ImageRect rect = calculateImageViewWidthAndHeight2(videoPlayInfo.coverWidth, videoPlayInfo.coverHeight);
        CommunityPlayerController controller = new CommunityPlayerController(mVideoPlayerView.getContext());
        controller.setFirstFrameViewWidthAndHeight(rect.width, rect.height);
        mVideoPlayerView.setController(controller);
    }

    private ImageRect calculateImageViewWidthAndHeight2(int w, int h) {
        overrideWidth = Utils.getScreenW();
        overrideHeight = h;
        float scale = Utils.formatData(w * 1.0f / h, 2);//只取小数点后两位，避免几个像素误差导致图片样式错误
        float scale_9_16 = Utils.formatData(9 * 1.0f / 16, 2);
        float scale_16_9 = Utils.formatData(16 * 1.0f / 9, 2);
        float scale_3_4 = Utils.formatData(3f / 4f, 2);

        float wScale = (float) overrideWidth / (float) w;//屏幕宽和图片宽的比

        if (scale < scale_3_4) {
            scale = scale_3_4;
        }
        overrideHeight = (int) (overrideWidth / scale);
        //setContentImgSize(mListVedioView, overrideWidth, overrideHeight);
        setContentImgSize(mVideoPlayerView, overrideWidth, overrideHeight);
        return new ImageRect(overrideWidth, overrideHeight);
    }

    public static class ImageRect implements Serializable {
        public int width;
        public int height;

        public ImageRect() {
        }

        public ImageRect(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    public void setContentImgSize(View view, int width, int height) {
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) view.getLayoutParams();
        p.width = width;
        p.height = height;
        view.setLayoutParams(p);
    }
}
