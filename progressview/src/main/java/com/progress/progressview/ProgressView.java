package com.progress.progressview;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import static com.progress.progressview.ProgressView.Direction.FROM_RIGHT;

public class ProgressView extends View {

    private static final String PROPERTY_PROGRESS = "ProgressValue";

    public enum Direction
    {
        FROM_LEFT, FROM_RIGHT
    }

    public enum Shape
    {
        ARC, CIRCLE, LINE
    }

    private float mBackgroundWidth, mProgressWidth;
    private @ColorInt int mBackgroundColor, mProgressColor;
    private float mProgress = 0f; // progress from 0 to 1
    private Direction mProgressDirection = Direction.FROM_LEFT;
    private Shape mShape = Shape.ARC;
    private int mAnimationDuration = 1500;
    private int[] mGradientColorList = null;

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

        if (mShape == Shape.ARC) {
            drawArc(canvas);
        } else if (mShape == Shape.CIRCLE) {
            drawCircle(canvas);
        } else {
            drawLine(canvas);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int min = Math.min(width, height);
        float highStroke = Math.max(mProgressWidth, mBackgroundWidth);
        mRectF.set(0 + highStroke / 2, 0 + highStroke / 2, min - highStroke / 2, min - highStroke / 2);

        if (mShape == Shape.ARC) {
            setMeasuredDimension(min, min / 2);
        } else if (mShape == Shape.CIRCLE) {
            setMeasuredDimension(min, min);
        } else {
            mRectF.set(0, 0 + highStroke / 2, min, highStroke / 2);
            setMeasuredDimension(width, (int) Math.max(highStroke, height));
        }
        updateShader();
    }

    public void applyGradient(int[] colorList) {
        if (colorList != null) {
            mGradientColorList = new int[colorList.length];
            System.arraycopy(colorList, 0, mGradientColorList, 0, colorList.length);
        } else {
            mGradientColorList = null;
        }
        if (mGradientColorList != null && mProgressDirection == FROM_RIGHT) {
            reverse(mGradientColorList);
        }
        updateShader();
    }

    public void setProgress(float progress) {
        setProgressWithAnimation(progress);
    }

    public void setBackgroundWidth(float backgroundWidth) {
        this.mBackgroundWidth = backgroundWidth;
        mBackgroundPaint.setStrokeWidth(backgroundWidth);
        invalidate();
    }

    public void setProgressWidth(float progressWidth) {
        this.mProgressWidth = progressWidth;
        mProgressPaint.setStrokeWidth(progressWidth);
        invalidate();
    }

    public void setProgressDirection(Direction progressDirection) {
        if (mGradientColorList != null && progressDirection != mProgressDirection) {
            reverse(mGradientColorList);
            updateShader();
        }
        this.mProgressDirection = progressDirection;
        invalidate();
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

    public float getProgress() {
        return mProgress;
    }

    public Direction getProgressDirection() {
        return mProgressDirection;
    }

    private void init(Context context, @Nullable AttributeSet attrs) {

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ProgressView, 0, 0);

        int direction = typedArray.getInt(R.styleable.ProgressView_pvDirection, 0);
        mProgressDirection = direction == 0 ? Direction.FROM_LEFT : FROM_RIGHT;

        int shape = typedArray.getInt(R.styleable.ProgressView_pvShape, 0);
        mShape = shape == 0 ? Shape.ARC : shape == 1 ? Shape.CIRCLE : Shape.LINE;

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

    private void updateShader() {
        if (mGradientColorList == null) {
            mProgressPaint.setShader(null);
            invalidate();
            return;
        }
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        Shader shader;
        if (mShape == Shape.ARC) {
            shader = new LinearGradient(0, width / 2, width, height, mGradientColorList, null, android.graphics.Shader.TileMode.CLAMP);
        } else  if (mShape == Shape.CIRCLE) {
            shader = new LinearGradient(0, 0, width, height, mGradientColorList, null, android.graphics.Shader.TileMode.CLAMP);
        } else {
            shader = new LinearGradient(0, 0, width, 0, mGradientColorList, null, android.graphics.Shader.TileMode.CLAMP);
        }
        mProgressPaint.setShader(shader);
        invalidate();
    }

    private void drawArc(Canvas canvas) {
        drawArc(canvas, 180);
    }

    private void drawCircle(Canvas canvas) {
        drawArc(canvas, 360);
    }

    private void drawLine(Canvas canvas) {
        float y = canvas.getHeight() / 2;
        canvas.drawLine(0, y, canvas.getWidth(), y, mBackgroundPaint);
        float progressWidth = mProgress * canvas.getWidth();
        canvas.drawLine(0, y, progressWidth, y, mProgressPaint);
    }

    private void drawArc(Canvas canvas, float sweepAngle) {
        canvas.drawArc(mRectF, 180, sweepAngle, false, mBackgroundPaint);
        float progressSweepAngle = mProgress * sweepAngle;
        float startAngle = 180;
        if (mProgressDirection == FROM_RIGHT) {
            startAngle = 0f;
            progressSweepAngle = -progressSweepAngle;
        }
        canvas.drawArc(mRectF, startAngle, progressSweepAngle, false, mProgressPaint);

    }

    private static float dpToPx(Context context, float dp){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    private static void reverse(int[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            int temp = array[i];
            array[i] = array[array.length - 1 - i];
            array[array.length - 1 - i] = temp;
        }
    }
}
