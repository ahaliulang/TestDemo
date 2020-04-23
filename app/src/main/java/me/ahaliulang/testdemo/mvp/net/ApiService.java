package me.ahaliulang.testdemo.mvp.net;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * author:tdn
 * time:2019/12/27
 * description:
 */
public interface ApiService {

    @GET("ssss")
    Observable<BaseModel<String>> getActivityList(@Query("req") String req);

}
