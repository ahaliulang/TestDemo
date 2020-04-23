package me.ahaliulang.testdemo.eventbus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.adnonstop.communityplayer.util.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.ahaliulang.testdemo.R;
import me.ahaliulang.testdemo.bean.MessageEvent;

public class SecondEventBusActivity extends AppCompatActivity {


    private Disposable mTimer;
    private TextView mCountDownTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_event_bus);
        mCountDownTv = findViewById(R.id.count_down_tv);
    }

    public void onSend(View view) {
        EventBus.getDefault().post(new MessageEvent());
    }
}
