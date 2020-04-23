package com.adnonstop.communityplayer.util.dailog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.adnonstop.communityplayer.R;
import com.adnonstop.communityplayer.util.CommunityPlayerUtils;


public class TextDialog extends Dialog
{
	private Context mContext;
	private OnDialogItemClickListener mListener;

	private FrameLayout mContentLayout;
	private TextView mContent;
	private TextView mButtonLeft;
	private TextView mButtonRight;
	private TextView mTitle;

	public TextDialog(@NonNull Context context)
	{
		super(context, R.style.mydialog);
		mContext = context;
		init();
	}

	public TextDialog(@NonNull Context context, int themeResId)
	{
		super(context, themeResId);
		mContext = context;
		init();
	}

	protected TextDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener)
	{
		super(context, cancelable, cancelListener);
		mContext = context;
		init();
	}

	public interface OnDialogItemClickListener
	{
		void onButtonLeft(TextDialog dialog);

		void onButtonRight(TextDialog dialog);

		void onDismiss();
	}

	public interface OnEditTextDialogItemClickListener
	{
		void onButtonLeft(TextDialog dialog, String text);

		void onButtonRight(TextDialog dialog, String text);
	}

	public void setOnDialogItemClickListener(OnDialogItemClickListener listener)
	{
		mListener = listener;
	}

	private void init()
	{
		View view = LayoutInflater.from(mContext).inflate(R.layout.c_textdialog_ui, null, false);

		mContentLayout = view.findViewById(R.id.c_layout_content);
		mContent = view.findViewById(R.id.c_tv_content);

		mButtonLeft = view.findViewById(R.id.c_tv_left_button);
		mButtonLeft.setOnClickListener(mOnClickListener);
		mButtonLeft.setOnTouchListener(mOnTouchListener);

		mButtonRight = view.findViewById(R.id.c_tv_right_button);
		mButtonRight.setOnClickListener(mOnClickListener);
		mButtonRight.setOnTouchListener(mOnTouchListener);

		setContentView(view);
	}

	private View.OnClickListener mOnClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			if(view == mButtonLeft)
			{
				if(mListener != null) mListener.onButtonLeft(TextDialog.this);
			}
			else if(view == mButtonRight)
			{
				if(mListener != null) mListener.onButtonRight(TextDialog.this);
			}
		}
	};

	private View.OnTouchListener mOnTouchListener = new View.OnTouchListener()
	{
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent)
		{
			if(view == mButtonLeft)
			{
				switch(motionEvent.getAction())
				{
					case MotionEvent.ACTION_DOWN:
						mButtonLeft.setBackgroundColor(0xfff5f5f5);
						break;

					case MotionEvent.ACTION_CANCEL:
					case MotionEvent.ACTION_UP:
						mButtonLeft.setBackgroundColor(0x00000000);
						break;
				}
			}
			else if(view == mButtonRight)
			{
				switch(motionEvent.getAction())
				{
					case MotionEvent.ACTION_DOWN:
						mButtonRight.setBackgroundColor(0xfff5f5f5);
						break;

					case MotionEvent.ACTION_CANCEL:
					case MotionEvent.ACTION_UP:
						mButtonRight.setBackgroundColor(0x00000000);
						break;
				}
			}
			return false;
		}
	};

	/**
	 * 设置文字内容
	 *
	 * @param content 对话框内容
	 * @param left    左按钮文字
	 * @param right   右按钮文字
	 */
	public void setText(String content, String left, String right)
	{
		mContent.setText(content);
		mButtonLeft.setText(left);
		mButtonRight.setText(right);
	}

	/**
	 * 设置文字颜色
	 *
	 * @param contentColor 对话框内容文字颜色
	 * @param leftColor    左按钮文字颜色
	 * @param rightColor   右按钮文字颜色
	 */
	public void setTextColor(int contentColor, int leftColor, int rightColor)
	{
		mContent.setTextColor(contentColor);
		mButtonLeft.setTextColor(leftColor);
		mButtonRight.setTextColor(rightColor);
	}

	/**
	 * 添加标题
	 *
	 * @param title 标题内容
	 */
	public void addTitle(String title)
	{
		if(title == null || title.length() <= 0) return;

		if(mTitle == null)
		{
			mTitle = new TextView(mContext);
			mTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			mTitle.setTextColor(0xff333333);
			Paint paint = mTitle.getPaint();
			paint.setFakeBoldText(true);
			FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
			fl.topMargin = CommunityPlayerUtils.getRealPixel(64);
			mContentLayout.addView(mTitle, fl);

			((FrameLayout.LayoutParams)(mContent.getLayoutParams())).topMargin = CommunityPlayerUtils.getRealPixel(120);
		}

		mTitle.setText(title);
	}

	/**
	 * 获取主显示Layout进行自定义布局（原布局会全部remove）
	 *
	 * @return
	 */
	public FrameLayout getContentLayout()
	{
		mContentLayout.removeAllViews();
		return mContentLayout;
	}

	@Override
	public void dismiss()
	{
		super.dismiss();
		if(mListener != null)
		{
			mListener.onDismiss();
		}
	}

	@Override
	public void show()
	{
		if(checkContextState())
		{
			super.show();
		}
	}

	private boolean checkContextState()
	{
		Context context = getContext();
		if(context instanceof Activity)
		{
			return !((Activity)context).isFinishing() && !((Activity)context).isDestroyed();
		}
		else if(context instanceof ContextWrapper)
		{
			Context baseContext = ((ContextWrapper)context).getBaseContext();
			if(baseContext instanceof Activity)
			{
				return !((Activity)baseContext).isFinishing() && !((Activity)baseContext).isDestroyed();
			}
		}
		return false;
	}
}
