package me.ahaliulang.testdemo.bottomsheet;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import me.ahaliulang.testdemo.R;

public class BottomSheetActivity extends AppCompatActivity {

    private MyBottomFragment myBottomFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_sheet);
    }

    public void onClick_03(View view) {
        myBottomFragment  = new MyBottomFragment();
        myBottomFragment.show(getSupportFragmentManager(),null);
    }
}
