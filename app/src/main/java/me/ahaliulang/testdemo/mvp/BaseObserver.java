package me.ahaliulang.testdemo.mvp;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.ahaliulang.testdemo.mvp.net.BaseModel;
import me.ahaliulang.testdemo.mvp.net.ErrorHandler;

/**
 * author:tdn
 * time:2019/12/27
 * description:
 */
public abstract class BaseObserver<T> implements Observer<BaseModel<T>> {

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(BaseModel<T> response) {
        if (response.getCode() == 200) {//通过200说明请求成功
            onSuccess(response);
        }else {
            onFail(ErrorHandler.handleException(new Throwable()));
        }
    }

    @Override
    public void onError(Throwable e) {
        onFail(ErrorHandler.handleException(e));
    }

    @Override
    public void onComplete() {

    }

    public abstract void onSuccess(BaseModel<T> t);

    public abstract void onFail(ErrorHandler.ResponseThrowable throwable);
}
