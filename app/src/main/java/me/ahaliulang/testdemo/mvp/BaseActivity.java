package me.ahaliulang.testdemo.mvp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;

import org.jetbrains.annotations.NotNull;


public abstract class BaseActivity<P extends IPresenter> extends AppCompatActivity {

    private Context mContext;
    private P mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
        initPresenter();
        initLifecycleObserver(getLifecycle());
        initData();
        initView();
        initListener();

    }

    @CallSuper
    @MainThread
    protected void initLifecycleObserver(@NotNull Lifecycle lifecycle) {
        if (mPresenter != null) {
            mPresenter.setLifecycleOwner(this);
            lifecycle.addObserver(mPresenter);
        }
    }


    public abstract void initPresenter();

    public abstract void initData();

    public abstract void initView();

    public abstract void initListener();


    public Context getContext() {
        return mContext;
    }
}
