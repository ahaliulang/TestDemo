package me.ahaliulang.testdemo.hierarchy;

import androidx.appcompat.app.AppCompatActivity;
import me.ahaliulang.testdemo.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class HierarchyActivity extends AppCompatActivity
{

	private TextView mTestTv;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hierarchy);
		mTestTv = findViewById(R.id.test_tv);
		mTestTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					Thread.sleep(6000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}


}
