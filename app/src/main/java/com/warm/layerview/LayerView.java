package com.warm.layerview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * 作者: 51hs_android
 * 时间: 2017/6/5
 * 简介:
 */

public class LayerView extends View implements ViewTreeObserver.OnGlobalLayoutListener {
    private static final String TAG = "LayerView";
    private ViewGroup parent;
    private View showView;


    //绘制外部的画笔
    private Paint bgPaint;

    //绘制按钮和文字的笔
    private Paint btPaint;


    private Rect cRectF = new Rect();


    /**
     * 控件宽高
     */
    protected int viewWidth, viewHeight;
    /**
     * 真实可以绘制的宽高,除去padding
     */
    protected int mWidth, mHeight;


    public LayerView(Context context) {
        this(context, null);
    }

    public LayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        bgPaint.setAlpha(155);
        btPaint = new Paint();
        btPaint.setColor(Color.WHITE);
        btPaint.setAntiAlias(true);
        btPaint.setStyle(Paint.Style.STROKE);

    }

    public void setView(View view) {
        this.showView = view;
        showView.getViewTreeObserver().addOnGlobalLayoutListener(this);

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
        viewWidth = w;
        viewHeight = h;
        mWidth = viewWidth - getPaddingLeft() - getPaddingRight();
        mHeight = viewHeight - getPaddingTop() - getPaddingBottom();
    }

    private Bitmap bgBitmap;
    private Canvas mCanvas;


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (parent != null) {

//            drawBack1(canvas);

            drawBack2(canvas);
            drawButton(canvas);



        }
    }

    /**
     * 绘制按钮
     *
     * @param canvas
     */
    private void drawButton(Canvas canvas) {
        int length = 100;
        int wide = 200;
        RectF rectF = new RectF((mWidth - wide) / 2,  (mHeight * (2f / 3)), (mWidth + wide) / 2,(mHeight * (2f / 3)) + length);

        canvas.drawRoundRect(rectF,10,10, btPaint);

        btPaint.setTextSize(40);
        btPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics = btPaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
        int baseLineY = (int) (rectF.centerY() - top/2 - bottom/2);//基线中间点的y轴计算公式
        canvas.drawText("朕知道了",rectF.centerX(),baseLineY,btPaint);
    }

    private void drawBack2(Canvas canvas) {
        //设置背景色
//            canvas.drawColor();
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        int layerId = canvas.saveLayer(0, 0, canvasWidth, canvasHeight, null, Canvas.ALL_SAVE_FLAG);
        //正常绘制黄色的圆形
        bgPaint.setColor(Color.parseColor("#75000000"));
        RectF rectF = new RectF();
        rectF.left = 0;
        rectF.top = 0;
        rectF.right = mWidth;
        rectF.bottom = mHeight;
        canvas.drawRect(rectF, bgPaint);
        //使用CLEAR作为PorterDuffXfermode绘制蓝色的矩形
        bgPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        bgPaint.setColor(Color.TRANSPARENT);

        canvas.drawRect(cRectF, bgPaint);
        //最后将画笔去除Xfermode
        bgPaint.setXfermode(null);
        canvas.restoreToCount(layerId);
    }

    private void drawBack1(Canvas canvas) {
        bgBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(bgBitmap);
        RectF rectF = new RectF();
        rectF.left = 0;
        rectF.top = 0;
        rectF.right = mWidth;
        rectF.bottom = mHeight;
        mCanvas.drawRect(rectF, bgPaint);
        Paint cPaint = new Paint();
        cPaint.setColor(Color.TRANSPARENT);
        cPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        cPaint.setAntiAlias(true);
        cRectF.left = 0;
        cRectF.top = 0;
        cRectF.right = 200;
        cRectF.bottom = 200;
        mCanvas.drawRect(cRectF, cPaint);

        canvas.drawBitmap(bgBitmap, 0, 0, bgPaint);
    }

    /**
     * @param view
     * @return
     */
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

    /**
     * @return statuBar高度
     */
    public int getBarHeight() {

        /**
         * 获取状态栏高度——方法1
         * */
        int statusBarHeight = 0;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        return statusBarHeight;
    }


    @Override
    public void onGlobalLayout() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            showView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        } else {
            showView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
        int[] location = new int[2];

        showView.getLocationInWindow(location);

        cRectF.left = location[0];
        cRectF.top = location[1] - getBarHeight();
        cRectF.right = cRectF.left + showView.getWidth();
        cRectF.bottom = cRectF.top + showView.getHeight();

        this.parent = findSuitableParent(showView);

        if (parent != null)
            parent.addView(this);
    }
}
