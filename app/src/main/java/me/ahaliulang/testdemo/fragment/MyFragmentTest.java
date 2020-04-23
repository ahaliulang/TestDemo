package me.ahaliulang.testdemo.fragment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import me.ahaliulang.testdemo.R;

public class MyFragmentTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_fragment_test);
        ContentFragment contentFragment = ContentFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(contentFragment,"ttttt").addToBackStack(null).commit();
    }
}
