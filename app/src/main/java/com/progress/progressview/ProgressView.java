package com.progress.progressview;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class ProgressView extends View {

    private static final String PROPERTY_PROGRESS = "ProgressValue";

    public enum Direction
    {
        FROM_LEFT, FROM_RIGHT
    }

    private float mBackgroundWidth, mProgressWidth;
    private @ColorInt int mBackgroundColor, mProgressColor;
    private float mProgress = 0f; // progress from 0 to 1
    private Direction mProgressDirection = Direction.FROM_LEFT;
    private int mAnimationDuration = 1500;

    private Paint mBackgroundPaint, mProgressPaint;
    private RectF mRectF;

    public ProgressView(Context context) {
        super(context);
        init(context, null);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        canvas.drawArc(mRectF, 180F, 180F, false, mBackgroundPaint);
        float progressSweepAngle = mProgress * 180;
        float startAngle = 180F;
        if (mProgressDirection == Direction.FROM_RIGHT) {
            startAngle = 0f;
            progressSweepAngle = -progressSweepAngle;
        }
        canvas.drawArc(mRectF, startAngle, progressSweepAngle, false, mProgressPaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int min = Math.min(width, height);
        setMeasuredDimension(min, min / 2);
        float highStroke = Math.max(mProgressWidth, mBackgroundWidth);
        mRectF.set(0 + highStroke / 2, 0 + highStroke / 2, min - highStroke / 2, min - highStroke / 2);
    }

    public void setProgress(float progress) {
        setProgressWithAnimation(progress);
    }

    public void setBackgroundWidth(float backgroundWidth) {
        this.mBackgroundWidth = backgroundWidth;
        invalidate();
    }

    public void setProgressWidth(float progressWidth) {
        this.mProgressWidth = progressWidth;
        invalidate();
    }

    public void setProgressDirection(Direction progressDirection) {
        this.mProgressDirection = progressDirection;
    }

    public void setAnimationDuration(int animationDuration) {
        this.mAnimationDuration = animationDuration;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.mBackgroundColor = backgroundColor;
        mBackgroundPaint.setColor(backgroundColor);
        invalidate();
    }

    public void setProgressColor(int progressColor) {
        this.mProgressColor = progressColor;
        mProgressPaint.setColor(progressColor);
        invalidate();
    }

    private void init(Context context, @Nullable AttributeSet attrs) {

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ProgressView, 0, 0);

        int direction = typedArray.getInt(R.styleable.ProgressView_pvDirection, 0);
        if (direction == 0) {
            mProgressDirection = Direction.FROM_LEFT;
        } else {
            mProgressDirection = Direction.FROM_RIGHT;
        }

        mProgress = typedArray.getFloat(R.styleable.ProgressView_pvProgress, mProgress);
        mBackgroundWidth = typedArray.getDimension(R.styleable.ProgressView_pvBackgroundWidth, dpToPx(context, 2));
        mProgressWidth = typedArray.getDimension(R.styleable.ProgressView_pvProgressWidth, dpToPx(context, 10));
        mBackgroundColor = typedArray.getColor(R.styleable.ProgressView_pvBackgroundColor, Color.BLACK);
        mProgressColor = typedArray.getColor(R.styleable.ProgressView_pvProgressColor, Color.RED);
        mAnimationDuration = typedArray.getInt(R.styleable.ProgressView_pvAnimateDuration, mAnimationDuration);

        typedArray.recycle();
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(mBackgroundColor);
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setStrokeWidth(mBackgroundWidth);

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setColor(mProgressColor);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(mProgressWidth);

        mRectF = new RectF();
    }

    private void setProgressWithAnimation(float progress) {
        PropertyValuesHolder propertyRotate = PropertyValuesHolder.ofFloat(PROPERTY_PROGRESS, mProgress, progress);
        ValueAnimator animator = new ValueAnimator();
        animator.setValues(propertyRotate);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(mAnimationDuration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgress = (float) animation.getAnimatedValue(PROPERTY_PROGRESS);
                invalidate();
            }
        });
        animator.start();
    }

    private static float dpToPx(Context context, float dp){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
