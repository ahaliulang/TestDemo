package me.ahaliulang.testdemo.bottomsheet;

import android.app.Dialog;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import me.ahaliulang.testdemo.R;
import me.ahaliulang.testdemo.utils.LogUtil;

/**
 * author:tdn
 * time:2019/12/3
 * description:
 */
public class MyBottomFragment extends BottomSheetDialogFragment {


    BottomSheetBehavior mBottomSheetBehavior;

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        final View root = View.inflate(getContext(), R.layout.fragment_bottom_sheet, null);
        dialog.setContentView(root);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) root.getParent()).getLayoutParams();
        final CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            mBottomSheetBehavior = (BottomSheetBehavior) behavior;
            mBottomSheetBehavior.setFitToContents(true);
            mBottomSheetBehavior.setHideable(false);
            mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    LogUtil.d("onStateChanged " + newState);
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        MyBottomFragment.this.dismiss();
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    LogUtil.d("onSlide " + slideOffset);
                }
            });

            root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    root.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    int height = root.getMeasuredHeight();
//                    mBottomSheetBehavior.setPeekHeight(1080);
                }
            });
        }
    }
}
