package me.ahaliulang.testdemo.vibrate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;

import me.ahaliulang.testdemo.R;

public class VibrateActivity extends AppCompatActivity {

    private Vibrator mVibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vibrate);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void onVibrate_01(View view) {
        mVibrator.vibrate(100);

    }

    public void onVibrate_02(View view) {
        mVibrator.vibrate(new long[]{10,100,80,100,80,100},-1);
    }
}
