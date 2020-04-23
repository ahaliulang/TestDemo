package me.ahaliulang.testdemo.mvp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import org.jetbrains.annotations.NotNull;

import me.ahaliulang.testdemo.mvp.net.ApiService;
import me.ahaliulang.testdemo.mvp.net.RetrofitHelper;

/**
 * author:tdn
 * time:2019/12/27
 * description:
 */
public abstract class BasePresenter<T extends BaseMvpView> implements IPresenter {

    private T mMvpView;
    private ApiService mApiService;

    private LifecycleOwner mLifecycleOwner;

    public BasePresenter(Context mContext, T mMvpView) {
        this.mMvpView = mMvpView;
        mApiService = RetrofitHelper.getInstance().getApiService();
    }


    public T getMvpView() {
        return mMvpView;
    }


    public ApiService getApiService() {
        return mApiService;
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        this.mLifecycleOwner = owner;
    }

    @Override
    public void onDestroy(@NotNull LifecycleOwner owner) {
        this.mMvpView = null;
    }
}
