package com.warm.layerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

/**
 * 作者: 51hs_android
 * 时间: 2017/6/5
 * 简介:
 */

public class LayerView extends View {
    private static final String TAG = "LayerView";
    private ViewGroup parent;
    private View showView;


    //绘制外部的画笔
    private Paint mPaint;

    private RectF contentR=new RectF();


    /**
     * 控件宽高
     */
    protected int viewWidth,viewHeight;
    /**
     * 真实可以绘制的宽高,除去padding
     */
    protected int mWidth,mHeight;



    public LayerView(Context context) {
        this(context,null);
    }

    public LayerView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mPaint=new Paint();
//        mPaint.setAlpha(155);
        mPaint.setColor(Color.argb(122,0,0,0));

    }

    public void setView(View view){
        this.showView=view;
        this.parent=findSuitableParent(view);
        Log.d(TAG, "setView: "+showView.getX());
        Rect rect=new Rect();
        showView.getGlobalVisibleRect(rect);

        if (parent!=null)
            parent.addView(this);
        invalidate();

    }
    


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(measureWidthSpec(widthMeasureSpec), measureHeightSpec(heightMeasureSpec));
    }

    private int measureWidthSpec(int spec) {
        int result = 300;
        int mode = MeasureSpec.getMode(spec);
        int size = MeasureSpec.getSize(spec);
        switch (mode) {
            case MeasureSpec.AT_MOST:
                result = Math.min(result, size);
                break;
            case MeasureSpec.EXACTLY:
                result = size;
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }
        return result;
    }

    private int measureHeightSpec(int spec) {
        int result = 300;
        int mode = MeasureSpec.getMode(spec);
        int size = MeasureSpec.getSize(spec);
        switch (mode) {
            case MeasureSpec.AT_MOST:
                result = Math.min(result, size);
                break;
            case MeasureSpec.EXACTLY:
                result = size;
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }
        return result;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth=w;
        viewHeight=h;
        mWidth=viewWidth-getPaddingLeft()-getPaddingRight();
        mHeight=viewHeight-getPaddingTop()-getPaddingBottom();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (parent!=null){
            RectF rectF=new RectF();
            rectF.left = 0;
            rectF.right = mWidth;
            rectF.top = 0;
            rectF.bottom = mHeight;
            canvas.drawRect(rectF,mPaint);
        }
    }


    private ViewGroup findSuitableParent(View view) {
        ViewGroup fallback = null;
        do {
            if (view instanceof FrameLayout) {
                if (view.getId() == android.R.id.content) {
                    // If we've hit the decor content view, then we didn't find a CoL in the
                    // hierarchy, so use it.
                    return (ViewGroup) view;
                } else {
                    // It's not the content view but we'll use it as our fallback
                    fallback = (ViewGroup) view;
                }
            }

            if (view != null) {
                // Else, we will loop and crawl up the view hierarchy and try to find a parent
                final ViewParent parent = view.getParent();
                view = parent instanceof View ? (View) parent : null;
            }
        } while (view != null);

        // If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
        return fallback;
    }



}
