package me.ahaliulang.testdemo.material_design;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Canvas;
import android.graphics.Outline;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.shape.MaterialShapeDrawable;

import me.ahaliulang.testdemo.R;

public class MaterialDesignActivity extends AppCompatActivity {


    private ImageView mIv;
    private Canvas mCanvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_design);

        mIv = findViewById(R.id.iv);
        mIv.setClipToOutline(true);
    }
}
