package me.ahaliulang.testdemo.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import me.ahaliulang.testdemo.R;

/**
 * author:tdn
 * time:2020/1/17
 * description:
 */
public class ContentFragment extends Fragment {


    public static ContentFragment newInstance() {
        Bundle args = new Bundle();
        ContentFragment fragment = new ContentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(container == null){
            return null;
        }
        TextView textView = new TextView(container.getContext());
        textView.setText("I'm fragment");
        textView.setBackgroundColor(Color.parseColor("#fff000"));
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(layoutParams);
        container.addView(textView);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
