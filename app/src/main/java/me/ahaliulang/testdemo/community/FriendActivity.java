package me.ahaliulang.testdemo.community;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adnonstop.communityplayer.CommunityListVideoHelper;
import com.adnonstop.communityplayer.bean.VideoPlayInfo;

import java.util.ArrayList;
import java.util.List;

import me.ahaliulang.testdemo.R;

public class FriendActivity extends AppCompatActivity {


    private RecyclerView mRecyclerView;
    private CommunityListVideoHelper mCommunityListVideoHelper;
    private VideoAdapter videoAdapter;
    private List<VideoPlayInfo> videoPlayInfos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        mRecyclerView = findViewById(R.id.recycler_view);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        videoAdapter = new VideoAdapter(this,videoPlayInfos);
    }
}
