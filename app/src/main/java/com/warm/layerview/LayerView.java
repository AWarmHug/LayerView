package com.warm.layerview;

import android.app.Activity;
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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * 作者: 51hs_android
 * 时间: 2017/6/5
 * 简介:
 */

public class LayerView extends View implements ViewTreeObserver.OnGlobalLayoutListener {
    private static final String TAG = "LayerView";


    private Context mContext;
    private Activity mActivity;
    private ViewGroup parent;
    private View cView;
    private int layerColor = Color.parseColor("#75000000");
    private int textColor = Color.WHITE;


    //绘制外部的画笔
    private Paint bgPaint;

    //绘制按钮和文字的笔
    private Paint btPaint;

    private Rect cRectF = new Rect();

    private boolean showing;

    /**
     * 控件宽高
     */
    protected int viewWidth, viewHeight;
    /**
     * 真实可以绘制的宽高,除去padding
     */
    protected int mWidth, mHeight;


    private LayerView(Context context, Builder builder) {
        this(context, null, builder);
    }

    private LayerView(Context context, AttributeSet attrs, Builder builder) {
        this(context, attrs, 0, builder);
    }

    private LayerView(Context context, AttributeSet attrs, int defStyleAttr, Builder builder) {
        super(context, attrs, defStyleAttr);
        this.cView = builder.cView;
        this.mActivity = (Activity) builder.mContext;
        this.layerColor = builder.layerColor;
        this.textColor = builder.textColor;
        if (builder.cRect != null) {
            this.cRectF = builder.cRect;
        }
        init(context, attrs, defStyleAttr);
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        btPaint = new Paint();
        btPaint.setColor(textColor);
        btPaint.setAntiAlias(true);
        btPaint.setStyle(Paint.Style.STROKE);
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
            //绘制有两种方法。
//            drawBack2(canvas);
            drawBack1(canvas);
            drawButton(canvas);
        }
    }

    /**
     * 通过设置两层，来实现
     * 这里 {@link Canvas#saveLayer(RectF, Paint, int)}来先保存浮层
     * {@link Canvas#restoreToCount(int)} 恢复浮层，也就是把两层合并。
     * 这两个操作很重要。如果没有这两个操作，颜色会发生变化，具体可以注释了自己看看。
     *
     * @param canvas
     */
    private void drawBack1(Canvas canvas) {
        //设置背景色
//            canvas.drawColor();
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        int layerId = canvas.saveLayer(0, 0, canvasWidth, canvasHeight, null, Canvas.ALL_SAVE_FLAG);
        bgPaint.setColor(layerColor);
        RectF rectF = new RectF();
        rectF.left = 0;
        rectF.top = 0;
        rectF.right = mWidth;
        rectF.bottom = mHeight;
        canvas.drawRect(rectF, bgPaint);
        bgPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        bgPaint.setColor(Color.TRANSPARENT);
        canvas.drawRect(cRectF, bgPaint);
        //最后将画笔去除Xfermode
        bgPaint.setXfermode(null);
        canvas.restoreToCount(layerId);
    }

    /**
     * 先生额外生成一个canvas,在这个canvas中绘制背景，透明块，
     * 需要生成一个bitmap,浪费内存，不推荐。
     * @param canvas
     */
    private void drawBack2(Canvas canvas) {
        bgBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        bgPaint.setColor(layerColor);
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
        mCanvas.drawRect(cRectF, cPaint);
        canvas.drawBitmap(bgBitmap, 0, 0, bgPaint);
    }
    /**
     * 绘制按钮
     *
     * @param canvas
     */
    private void drawButton(Canvas canvas) {
        int length = 100;
        int wide = 200;
        RectF rectF = new RectF((mWidth - wide) / 2, (mHeight * (2f / 3)), (mWidth + wide) / 2, (mHeight * (2f / 3)) + length);

        canvas.drawRoundRect(rectF, 10, 10, btPaint);

        btPaint.setTextSize(40);
        btPaint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText("提示操作", rectF.centerX(), cRectF.bottom + 100, btPaint);


        Paint.FontMetrics fontMetrics = btPaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
        int baseLineY = (int) (rectF.centerY() - top / 2 - bottom / 2);//基线中间点的y轴计算公式
        canvas.drawText("我知道了。", rectF.centerX(), baseLineY, btPaint);
    }


    @Override
    public void onGlobalLayout() {
        //获取View位置，根据位置，设置透明区域的RectF
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            cView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        } else {
            cView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
        int[] location = new int[2];

        cView.getLocationInWindow(location);

        cRectF.left = location[0];
        cRectF.top = location[1];
        cRectF.right = cRectF.left + cView.getWidth();
        cRectF.bottom = cRectF.top + cView.getHeight();

        //获取window中的最外侧的View。用于添加LayerView
        this.parent = (FrameLayout) mActivity.getWindow().getDecorView();

        show();
        Log.d(TAG, "onGlobalLayout: ");
    }

    public void initshow() {
        //调用，监听View的生成状况
        cView.getViewTreeObserver().addOnGlobalLayoutListener(this);

    }

    public void show() {
        if (parent != null)
            parent.addView(this);

        showing = true;

    }

    public void dismiss() {
        if (parent != null)
            parent.removeView(this);

        showing = false;
    }

    public boolean isShow() {
        return showing;

    }

    public static class Builder {
        private Context mContext;
        private int layerColor;
        private int textColor;
        private View cView;

        private Rect cRect;

        public Builder(Context mContext) {
            this.mContext = mContext;
        }

        public Builder setLayerColor(int color) {
            this.layerColor = color;
            return this;
        }

        public Builder setTextColor(int color) {
            this.textColor = color;
            return this;
        }

        public Builder setContent(View view) {
            this.cView = view;
            Log.d(TAG, "setContent: "+view.getX());
            return this;
        }

        public Builder setContent(Rect cRect) {
            this.cRect = cRect;
            return this;
        }

        public LayerView build() {
            LayerView layerView = new LayerView(mContext, this);
            layerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return layerView;
        }
    }


}
