package com.jz.testapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.MonthView;


public class CMonthView extends MonthView {

    private int mRadius;
    private Paint imgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public CMonthView(Context context) {
        super(context);
        setLayerType(View.LAYER_TYPE_SOFTWARE, mSchemePaint);
        mOtherMonthTextPaint.setFakeBoldText(true);
    }

    @Override
    protected void onPreviewHook() {
        mRadius = Math.min(mItemWidth, mItemHeight) / 5 * 2;
    }

    /**
     * 如果需要点击Scheme没有效果，则return true
     *
     * @param canvas    canvas
     * @param calendar  日历日历calendar
     * @param x         日历Card x起点坐标
     * @param y         日历Card y起点坐标
     * @param hasScheme hasScheme 非标记的日期
     * @return false 则不绘制onDrawScheme，因为这里背景色是互斥的
     */
    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme) {
        return true;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, int y) {
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        float baselineY = mTextBaseLine + y;
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;

        drawDefault(canvas, calendar, x, y);
    }

    private void drawDefault(Canvas canvas, Calendar calendar, int x, int y) {
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;
        int itemL = cx - (mItemWidth - DensityUtil.dp2px(9f)) / 2;
        int itemR = cx + (mItemWidth - DensityUtil.dp2px(9f)) / 2;
        int itemB = y + mItemHeight - DensityUtil.dp2px(10f);

        imgPaint.setColor(Color.parseColor("#FF000A"));
        RectF rectF = new RectF(itemL, y, itemR, itemB);
        canvas.drawRoundRect(rectF, 15, 15, imgPaint);


        textPaint.setTextSize(DensityUtil.dp2px(15f));
        textPaint.setColor(Color.parseColor("#404856"));
        textPaint.setFakeBoldText(true);
        float textHeight = getTextHeight(textPaint);
        float textW = (int) textPaint.measureText(String.valueOf(calendar.getDay()));

        canvas.drawText(String.valueOf(calendar.getDay()), cx - textW / 2, textHeight + DensityUtil.dp2px(5f) + y, textPaint);


        textPaint.setTextSize(DensityUtil.dp2px(10f));
        textPaint.setColor(Color.parseColor("#8A8E8E"));
        textPaint.setFakeBoldText(false);
        textHeight = getTextHeight(textPaint);
        textW = (int) textPaint.measureText("+10");
        int imgL = (int) ((itemR - itemL - textW - DensityUtil.dp2px(15f)) / 2);

        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.ic_calendar_beans);
        Rect srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Rect desRect = new Rect(itemL + imgL,
                itemB - DensityUtil.dp2px(20f),
                itemL + imgL + DensityUtil.dp2px(15f),
                itemB - DensityUtil.dp2px(5f));
        canvas.drawBitmap(bitmap, srcRect, desRect, imgPaint);

        float marginT = (DensityUtil.dp2px(15f) - textHeight) / 2;
        canvas.drawText("+10", itemL + imgL + DensityUtil.dp2px(15f), itemB - DensityUtil.dp2px(20f) + marginT + textHeight, textPaint);


    }

    private void drawToday(Canvas canvas, Calendar calendar, int x, int y) {

    }

    private float getTextHeight(Paint paint) {
        Paint.FontMetrics metrics = paint.getFontMetrics();
        return metrics.descent + (metrics.bottom - metrics.top) / 2;
    }

}
