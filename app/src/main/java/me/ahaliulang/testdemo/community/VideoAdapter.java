package me.ahaliulang.testdemo.community;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adnonstop.communityplayer.bean.VideoPlayInfo;

import java.util.List;

import me.ahaliulang.testdemo.R;

/**
 * author:tdn
 * time:2019/12/10
 * description:
 */
public class VideoAdapter extends RecyclerView.Adapter {

    private Context mContxt;
    private List<VideoPlayInfo> mDatas;

    public VideoAdapter(Context mContxt, List<VideoPlayInfo> mDatas) {
        this.mContxt = mContxt;
        this.mDatas = mDatas;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContxt).inflate(R.layout.item_video, parent, false);
        return new VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VideoHolder) {
            ((VideoHolder) holder).setData(mDatas.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }


}
