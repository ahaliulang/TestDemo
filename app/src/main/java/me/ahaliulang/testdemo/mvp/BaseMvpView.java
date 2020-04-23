package me.ahaliulang.testdemo.mvp;

/**
 * author:tdn
 * time:2019/12/27
 * description:
 */
public interface BaseMvpView{


    /**
     * 显示错误信息
     * @param msg
     */
    void showError(String msg);


    /**
     * 显示toast
     */
    void showToast();
}
