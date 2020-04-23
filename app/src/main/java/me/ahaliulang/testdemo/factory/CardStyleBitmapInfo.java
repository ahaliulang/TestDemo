package me.ahaliulang.testdemo.factory;

import android.graphics.Bitmap;

import androidx.annotation.IntDef;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by lgh on 2018/9/10
 */
public class CardStyleBitmapInfo implements Serializable
{
	public static final int AVATAR = 1;
	public static final int VIDEO = 2;
	public static final int IMAGE = 3;

	public Bitmap bitmap;

	@IntDef({AVATAR, VIDEO, IMAGE})
	@Retention(RetentionPolicy.SOURCE)
	public @interface B_TYPE
	{
	}

	public int type; //1头像图片，2为视频,3为普通图片

	public CardStyleBitmapInfo(Bitmap bitmap, @B_TYPE int type)
	{
		this.bitmap = bitmap;
		this.type = type;
	}
}
