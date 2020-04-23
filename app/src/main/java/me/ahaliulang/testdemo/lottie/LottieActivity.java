package me.ahaliulang.testdemo.lottie;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.adnonstop.communityplayer.util.LogUtil;
import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import me.ahaliulang.testdemo.R;

public class LottieActivity extends AppCompatActivity {

    private LottieAnimationView mView1, mView2, mView3;
    private Button mStartBtn, mMatchBtn, mSuccessBtn;
    private float mMaxFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottie);

        mView1 = findViewById(R.id.view_01);
        mView2 = findViewById(R.id.view_02);
        mView3 = findViewById(R.id.view_03);
        mStartBtn = findViewById(R.id.start_btn);
        mMatchBtn = findViewById(R.id.match_btn);
        mSuccessBtn = findViewById(R.id.success_btn);


        mView1.setCacheComposition(false);
        mView2.setCacheComposition(false);
        mView1.setImageAssetsFolder("match/images");
        mView2.setImageAssetsFolder("match/images");
        mView3.setImageAssetsFolder("match/images");
        mView1.setAnimation(R.raw.loop);
        mView2.setAnimation(R.raw.avatar_success);

        mView3.setAnimation(R.raw.loop);

        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mView1.setMinFrame(16);
                mView1.setProgress(0f);
                mView1.playAnimation();

                mView2.setVisibility(View.VISIBLE);
                mView2.setMinAndMaxFrame(60, 88);
                mView2.setProgress(0f);
                mView2.playAnimation();

                mView3.setProgress(0f);
                mView3.setMinFrame(0);
                mView3.pauseAnimation();
//                mView3.playAnimation();
            }
        });

        mView1.setRepeatCount(ValueAnimator.INFINITE);
        mView1.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int frame = mView1.getFrame();
                if (frame == 25 && mView1.getMinFrame() == 0) {
                    mView1.setMinFrame(25);
                }
            }
        });


        mView2.setRepeatCount(ValueAnimator.INFINITE);
        mView2.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                LogUtil.d(mView2.getMaxFrame()+"");
            }
        });

       


        mMatchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                mView1.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//                ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
//                animator.setDuration(600);
//                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                    @Override
//                    public void onAnimationUpdate(ValueAnimator animation) {
//                        LogUtil.d(animation.getAnimatedFraction() + "");
//                        mView1.setScale(1f - animation.getAnimatedFraction());
//                        mView1.setAlpha(1f - animation.getAnimatedFraction());
//                        if (((Float) animation.getAnimatedValue()) >= 1f) {
//                            mView2.setMinAndMaxFrame(25, 66);
//                            mView3.playAnimation();
//                        }
//                    }
//                });
//                animator.start();

                mView1.setProgress(0f);
                mView1.setMinFrame(0);
                mView1.pauseAnimation();
                mView2.setProgress(0f);
                mView2.setMinFrame(0);
                mView2.playAnimation();
                mView3.setProgress(0f);
                mView3.setMinFrame(0);
                mView3.pauseAnimation();
            }
        });

        mSuccessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mView2.setMinAndMaxFrame(66, 128);
//                mView2.setRepeatCount(0);
//                ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
//                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                    @Override
//                    public void onAnimationUpdate(ValueAnimator animation) {
//                        mView3.setAlpha(1f - animation.getAnimatedFraction());
//                    }
//                });
//                animator.start();

                mView1.setProgress(0f);
                mView1.setMinFrame(0);
                mView1.pauseAnimation();
                mView2.setProgress(0f);
                mView2.setMinFrame(0);
                mView2.pauseAnimation();
                mView3.setProgress(0f);
                mView3.setMinFrame(0);
                mView3.playAnimation();
            }
        });


    }
}
