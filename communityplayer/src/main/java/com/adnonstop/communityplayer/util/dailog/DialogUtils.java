package com.adnonstop.communityplayer.util.dailog;

import android.content.Context;

/**
 * author:tdn
 * time:2019/6/11
 * description:
 */
public class DialogUtils
{


	private static TextDialog sTextDialog;

	/**
	 * 显示白色圆角背景弹框
	 *
	 * @param context         上下文
	 * @param title           标题，可以为null
	 * @param content         文本内容
	 * @param leftButtonText  左按钮文字
	 * @param rightButtonText 右按钮文字
	 * @param cancelOutside   点击外面是否关闭对话框
	 * @param listener        按钮点击监听器
	 */
	public static void showRoundedRectangleDialog(Context context, String title, String content, String leftButtonText, String rightButtonText, boolean cancelOutside, TextDialog.OnDialogItemClickListener listener)
	{
		if(context == null) return;
		if(sTextDialog != null)
		{
			sTextDialog.hide();
		}
		sTextDialog = new TextDialog(context);
		sTextDialog.setText(content, leftButtonText, rightButtonText);
		sTextDialog.addTitle(title);
		sTextDialog.setCanceledOnTouchOutside(cancelOutside);
		sTextDialog.setOnDialogItemClickListener(listener);
		sTextDialog.show();
	}

	public static void hideDialog()
	{
		if(sTextDialog != null && sTextDialog.isShowing())
		{
			sTextDialog.dismiss();
		}
	}


}
