package me.ahaliulang.testdemo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adnonstop.communityplayer.util.LogUtil;
import com.facebook.stetho.Stetho;


/**
 * author:tdn
 * time:2019/12/3
 * description:
 */
public class MyApplication extends Application
{

	private int appCount;
	private boolean isRunInBackground = true;
	private boolean isRunForegound;


	@Override
	public void onCreate()
	{
		super.onCreate();
//		DoraemonKit.install(this);
		Stetho.initializeWithDefaults(this);
		registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
			@Override
			public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

			}

			@Override
			public void onActivityStarted(@NonNull Activity activity) {
				appCount++;
	/*			if (isRunInBackground) {
					//应用从后台回到前台 需要做的操作
					back2App(activity);
				}*/
				if(!isRunForegound){
					back2App(activity);
				}
			}

			@Override
			public void onActivityResumed(@NonNull Activity activity) {

			}

			@Override
			public void onActivityPaused(@NonNull Activity activity) {

			}

			@Override
			public void onActivityStopped(@NonNull Activity activity) {
				appCount--;
				if (appCount == 0) {
					//应用进入后台 需要做的操作
					leaveApp(activity);
				}
			}

			@Override
			public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

			}

			@Override
			public void onActivityDestroyed(@NonNull Activity activity) {

			}
		});
	}

	/**
	 * 从后台回到前台需要执行的逻辑
	 *
	 * @param activity
	 */
	private void back2App(Activity activity) {
//		isRunInBackground = false;
		isRunForegound = true;
		LogUtil.d("进入前台"+ appCount);
	}

	/**
	 * 离开应用 压入后台或者退出应用
	 *
	 * @param activity
	 */
	private void leaveApp(Activity activity) {
//		isRunInBackground = true;
		isRunForegound = false;
		LogUtil.d("进入后台" + appCount);
	}

}
