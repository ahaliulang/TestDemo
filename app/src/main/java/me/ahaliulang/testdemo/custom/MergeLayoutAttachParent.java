package me.ahaliulang.testdemo.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import me.ahaliulang.testdemo.R;
import me.ahaliulang.testdemo.utils.ToastUtils;

/**
 * author:tdn
 * time:2019/12/3
 * description:
 */
public class MergeLayoutAttachParent extends RelativeLayout {

    private View mView;

    public MergeLayoutAttachParent(Context context) {
        this(context, null);
    }

    public MergeLayoutAttachParent(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MergeLayoutAttachParent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mView = LayoutInflater.from(context).inflate(R.layout.view_merge, this,true);
        setClickable(true);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        if (mView != null && mView.getParent() != null) {
            ToastUtils.show(getContext(), mView.getParent().getClass().getSimpleName());
        }
    }
}
