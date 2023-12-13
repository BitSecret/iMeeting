package com.alen.MeetingRoom.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.alen.MeetingRoom.R;


/**
 * Created by Alen on 2018/2/9.
 */

public class SlideButton extends View {
    private Context context;
    private boolean isCheck = false;

    private SlideButtonOnChangeListener onChangeListener;

    private float buttonX; //圆钮的圆心x坐标
    private float viewW;
    private float viewH;
    private float buttonRadius; //圆的半径
    private float buttonMargin = 8; //边距
    private int buttonOnColor = getResources().getColor(R.color.colorTheme);
    private int buttonOffColor = getResources().getColor(R.color.MinorTextColor);

    private Paint paint;

    public SlideButton(Context context) {
        this(context, null);
    }

    public SlideButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }
    private void init(){
        paint =new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
    }

    public interface SlideButtonOnChangeListener{
        void onChange(boolean isCheck);
    }

    public void setSlideButtonOnChangeListener(SlideButtonOnChangeListener onChangeListener){
        this.onChangeListener = onChangeListener;
    }

    public void setCheck(){
        setCheck(!isCheck);
    }

    public void setCheck(boolean isCheck){
        this.isCheck = isCheck;
        if (this.isCheck){
            buttonX = viewW - buttonMargin - buttonRadius;
        }else {
            buttonX = buttonMargin + buttonRadius;
        }
        if (onChangeListener != null){
            onChangeListener.onChange(this.isCheck);
        }
        postInvalidate();
    }

    public boolean isCheck(){
        return isCheck;
    }

    public void setCheckedWithAnimation(boolean isCheck) {
        this.isCheck = isCheck;
        if (onChangeListener != null)
            onChangeListener.onChange(this.isCheck);
        startAnimation(this.isCheck);
    }

    private void startAnimation(boolean isButtonOn) {
        float endX;
        if (isButtonOn) {
            endX = viewW - buttonMargin - buttonRadius;
        } else {
            endX = buttonMargin + buttonRadius;
        }

        // 清除动画
        clearAnimation();
        //设置动画
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "111", buttonX, endX);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(200);
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                buttonX = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSpecMode == MeasureSpec.AT_MOST ? dip2px(40) : widthSpecSize,
                heightSpecMode == MeasureSpec.AT_MOST ? dip2px(20) : heightSpecSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewW = w;
        viewH = h;

        buttonRadius =viewH/2 - buttonMargin;
        buttonX = buttonMargin + buttonRadius;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                setCheckedWithAnimation(!isCheck);
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawFrame(canvas);

        paint.setColor(buttonX > viewW/2 ? buttonOnColor : buttonOffColor);
        canvas.drawRoundRect(new RectF(buttonX - buttonRadius, viewH/2 - buttonRadius,
                buttonX + buttonRadius, viewH/2 + buttonRadius) ,buttonRadius, buttonRadius, paint);
    }

    private void drawFrame(Canvas canvas){
        paint.setColor(buttonOffColor);
        canvas.drawRoundRect(new RectF(0,0,getWidth(),getHeight()), getHeight()/2, getHeight()/2, paint);
        paint.setColor(getResources().getColor(R.color.MainColor));
        canvas.drawRoundRect(new RectF(2,2,getWidth()-2,getHeight()-2), getHeight()/2-2, getHeight()/2-2, paint);
    }

    private int dip2px(float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
