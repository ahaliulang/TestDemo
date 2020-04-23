package me.ahaliulang.testdemo.recyclerview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import me.ahaliulang.testdemo.R;

public class RecyclerViewActivity extends AppCompatActivity {


    private RecyclerView mRv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        mRv = findViewById(R.id.rv);


        mRv.setLayoutManager(new LinearLayoutManager(this));

        final StudentAdapter studentAdapter = new StudentAdapter(this);
        studentAdapter.getDataSet().addAll(new Student(1, "sss1"), new Student(2, "sdsd2"), new Student(1, "sdsd3"), new Student(233, "sdsd4"));
        mRv.setAdapter(studentAdapter);

        findViewById(R.id.add_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studentAdapter.getDataSet().addAll(new Student(1990,"傻撒大声地"),new Student(5,"撒大声地所"));
            }
        });



    }


}
