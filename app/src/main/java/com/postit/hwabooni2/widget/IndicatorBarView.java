package com.postit.hwabooni2.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.postit.hwabooni2.R;

public class IndicatorBarView extends View {

    double value = 0.3;
    double max = 1;
    int textSize = 78;
    int indicatorColor = Color.GRAY;
    int strokeColor = Color.BLACK;
    String leftText = "";
    String midText = "";
    String rightText = "";

    Paint paint = new Paint();

    public IndicatorBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        leftText = context.obtainStyledAttributes(attrs, R.styleable.IndicatorBarView).getString(R.styleable.IndicatorBarView_leftText);
        midText = context.obtainStyledAttributes(attrs, R.styleable.IndicatorBarView).getString(R.styleable.IndicatorBarView_midText);
        rightText = context.obtainStyledAttributes(attrs, R.styleable.IndicatorBarView).getString(R.styleable.IndicatorBarView_rightText);
        indicatorColor = context.obtainStyledAttributes(attrs, R.styleable.IndicatorBarView).getColor(R.styleable.IndicatorBarView_indicatorColor,Color.GRAY);
        strokeColor = context.obtainStyledAttributes(attrs, R.styleable.IndicatorBarView).getColor(R.styleable.IndicatorBarView_strokeColor,Color.BLACK);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;

        int measureWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(widthMeasureSpec);

        int mDefaultWidth = 500;
        int mDefaultHeight = 250;

        switch (measureWidthMode) {
            case MeasureSpec.AT_MOST:
                width = Math.min(mDefaultWidth, measureWidth);
                break;
            case MeasureSpec.EXACTLY:
                width = measureWidth;
                break;
            default:
                width = mDefaultWidth;
        }

        switch (measureHeightMode) {
            case MeasureSpec.AT_MOST:
                height = Math.min(mDefaultHeight, measureHeight);
                break;
            case MeasureSpec.EXACTLY:
                height = measureHeight;
                break;
            default:
                height = mDefaultHeight;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (canvas == null) return;
        int w = getWidth();
        int h = getHeight();
        int gray = ContextCompat.getColor(getContext(), R.color.indicator_gray);
        double key = value / max;
        paint.setColor(gray);
        canvas.drawRect(w * 0.00f, h * 0.1f, w * 1.00f, h * 0.54f, paint);
        paint.setColor(strokeColor);
        canvas.drawRect(w * 0.22f, h * 0.1f, w * 0.78f, h * 0.54f, paint);
        paint.setColor(indicatorColor);
        canvas.drawCircle((float) (w*key),h*0.32f,h*0.44f*0.4f,paint);

        paint.setLetterSpacing(-0.1f);
        paint.setTextSize(textSize);

        paint.setColor((key <= 0.22) ? Color.BLACK : gray);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(leftText, w * 0.02f, h * 0.64f + textSize, paint);
        paint.setColor((key > 0.22 && key<0.78) ? Color.BLACK : gray);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(midText, w * 0.5f, h * 0.64f + textSize, paint);
        paint.setColor((key >= 0.78) ? Color.BLACK : gray);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(rightText, w * 0.98f, h * 0.64f + textSize, paint);
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
        invalidate();
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        if(max==0) return;
        this.max = max;
        invalidate();
    }
}
