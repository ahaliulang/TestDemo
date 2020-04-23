package me.ahaliulang.testdemo.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.adnonstop.communityplayer.util.LogUtil;

/**
 * author:tdn
 * time:2020/1/7
 * description:
 */
public class ChildView extends View {
    public ChildView(Context context) {
        super(context);
        setClickable(true);
    }

    public ChildView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setClickable(true);
    }

    public ChildView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtil.d("ChildView onTouchEvent");
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        LogUtil.d("ChildView dispatchTouchEvent");
        return super.dispatchTouchEvent(event);
    }
}
