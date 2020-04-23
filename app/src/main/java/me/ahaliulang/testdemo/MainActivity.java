package me.ahaliulang.testdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.adnonstop.communityplayer.util.LogUtil;


import me.ahaliulang.testdemo.bean.MessageEvent;
import me.ahaliulang.testdemo.bottomsheet.BottomSheetActivity;
import me.ahaliulang.testdemo.eventbus.MainEventBusActivity;
import me.ahaliulang.testdemo.fragment.MyFragmentTest;
import me.ahaliulang.testdemo.hierarchy.HierarchyActivity;
import me.ahaliulang.testdemo.lottie.LottieActivity;
import me.ahaliulang.testdemo.material_design.MaterialDesignActivity;
import me.ahaliulang.testdemo.recyclerview.RecyclerViewActivity;
import me.ahaliulang.testdemo.taskaffinity.AffinityActivity;
import me.ahaliulang.testdemo.vibrate.VibrateActivity;
import me.ahaliulang.testdemo.video.AutoPlayActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onClick_01(View view) {
        startActivity(new Intent(this, HierarchyActivity.class));
    }

    public void onClick_02(View view) {
        startActivity(new Intent(this, BottomSheetActivity.class));
    }

    public void onClick_03(View view) {
        startActivity(new Intent(this, AutoPlayActivity.class));
    }

    public void onClick_04(View view) {
        startActivity(new Intent(this, LottieActivity.class));
    }

    public void onClick_05(View view) {
        startActivity(new Intent(this, MaterialDesignActivity.class));
    }

    public void onClick_06(View view) {
        Intent intent = new Intent(MainActivity.this, AffinityActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void onClick_07(View view) {
        Intent intent = new Intent(MainActivity.this, MyFragmentTest.class);
        startActivity(intent);
    }

    public void onClick_08(View view) {
        Intent intent = new Intent(MainActivity.this, RecyclerViewActivity.class);
        startActivity(intent);
    }

    public void onClick_09(View view) {
        Intent intent = new Intent(MainActivity.this, VibrateActivity.class);
        startActivity(intent);
    }

    public void onClick_10(View view) {
        Intent intent = new Intent(MainActivity.this, MainEventBusActivity.class);
        startActivity(intent);
    }

}



