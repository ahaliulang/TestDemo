package me.ahaliulang.testdemo.mvp.net;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * author:tdn
 * time:2019/12/27
 * description:
 */
public class RetrofitHelper {

    private static RetrofitHelper sRetrofitHelper;
    private ApiService mApiService;


    public RetrofitHelper() {
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder().connectTimeout(60 * 1000, TimeUnit.MILLISECONDS)//60秒链接超时
                .writeTimeout(60 * 1000, TimeUnit.MILLISECONDS)//写入超时60秒
                .readTimeout(60 * 1000, TimeUnit.MILLISECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("baidu.com").addConverterFactory(GsonConverterFactory.create())//添加gson转换器
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//添加rxjava转换器
                .client(mOkHttpClient).build();
        mApiService = retrofit.create(ApiService.class);
    }

    public static RetrofitHelper getInstance() {
        if (sRetrofitHelper == null) {
            synchronized (RetrofitHelper.class) {
                if (sRetrofitHelper == null) {
                    sRetrofitHelper = new RetrofitHelper();
                }
            }
        }
        return sRetrofitHelper;
    }

    public ApiService getApiService() {
        return mApiService;
    }
}
