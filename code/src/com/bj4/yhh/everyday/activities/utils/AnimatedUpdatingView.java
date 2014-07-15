
package com.bj4.yhh.everyday.activities.utils;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class AnimatedUpdatingView extends View {
    private Context mContext;

    private Paint mBackgroundPaint;

    private RadialGradient mRadialGradient;

    private LinearGradient mLinearGradient = null;

    private int mHoloBlue, mHoloGreen, mHoloOrange, mHoloRed;

    private int mRadialRadius = 0;

    private ValueAnimator mBackgroundAnimator;

    private ValueAnimator mWhiteAnimator;

    private boolean mIsUpdating = false;

    public AnimatedUpdatingView(Context context) {
        this(context, null);
    }

    public AnimatedUpdatingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimatedUpdatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mHoloBlue = mContext.getResources().getColor(android.R.color.holo_blue_bright);
        mHoloGreen = mContext.getResources().getColor(android.R.color.holo_green_light);
        mHoloOrange = mContext.getResources().getColor(android.R.color.holo_orange_light);
        mHoloRed = mContext.getResources().getColor(android.R.color.holo_red_light);
        mBackgroundAnimator = ValueAnimator.ofInt(600, 3000);
        mBackgroundAnimator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRadialRadius = (Integer)animation.getAnimatedValue();
                setAnimationValue();
            }
        });
        mBackgroundAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mBackgroundAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mBackgroundAnimator.setDuration(20000);
        mBackgroundPaint.setStyle(Paint.Style.FILL);

        mWhiteAnimator = ValueAnimator.ofInt(0, 255);
        mWhiteAnimator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int white = (Integer)animation.getAnimatedValue();
                int blue = getWhiteColor(white, mHoloBlue);
                int green = getWhiteColor(white, mHoloGreen);
                int orange = getWhiteColor(white, mHoloOrange);
                int red = getWhiteColor(white, mHoloRed);
                mLinearGradient = new LinearGradient(0, 0, mRadialRadius, 0, new int[] {
                        blue, green, orange, red
                }, null, Shader.TileMode.MIRROR);
                mBackgroundPaint.setShader(mLinearGradient);
                invalidate();
            }
        });
        mWhiteAnimator.setDuration(1000);
        mLinearGradient = new LinearGradient(0, 0, mRadialRadius, 0, new int[] {
                Color.WHITE, Color.WHITE
        }, null, Shader.TileMode.MIRROR);
        mBackgroundPaint.setShader(mLinearGradient);
    }

    private static int getWhiteColor(int progress, int currentColor) {
        int r = Color.red(currentColor);
        int g = Color.green(currentColor);
        int b = Color.blue(currentColor);
        r += progress;
        g += progress;
        b += progress;
        if (r >= 255)
            r = 255;
        if (g >= 255)
            g = 255;
        if (b >= 255)
            b = 255;
        return Color.rgb(r, g, b);
    }

    private void setAnimationValue() {
        mLinearGradient = new LinearGradient(0, 0, mRadialRadius, 0, new int[] {
                mHoloBlue, mHoloGreen, mHoloOrange, mHoloRed
        }, null, Shader.TileMode.MIRROR);
        mBackgroundPaint.setShader(mLinearGradient);
        invalidate();
    }

    public void onFinishInflate() {
        super.onFinishInflate();
    }

    public void startAnimation() {
        mBackgroundAnimator.start();
        mIsUpdating = true;
    }

    public void finishAnimation() {
        mBackgroundAnimator.end();
        mWhiteAnimator.start();
        mIsUpdating = false;
    }

    public void onDraw(Canvas canvas) {
        canvas.drawPaint(mBackgroundPaint);
        super.onDraw(canvas);
    }
}
