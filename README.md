# LayerView
浮层引导页。
由于App风格不同可能需求差异比较大，所有不进行封装，只是提供一些关键步骤的写法。

一般在第一次安装，或者新版本更新的时候，引导用户一些新的功能，这时候，就需要一个浮层浮在上面，在关键的地方有需要显示出来，大概就像这样：

![](http://oqwcjoc6j.bkt.clouddn.com/layerView-show.gif)

因为各种App界面不同，需求也各不相同，所以，我只是提供一些关键代码，至于是要画方还是画圆，可以自己实现，关于Canvas的一些常见操作可以自己查阅Api，联系一下就行，也可以看看我的这个[小demo](https://github.com/AWarmHug/SimpleChart)，是一个简单的报表（饼状图，条形图，折线图，可以对Canvas，有一个初步的了解）。

------

**下面就开始**

其实，实现起来并不难，主要的一个点就是，如何实现一部分透明，其他不透明，这里需要了解**PorterDuffXfermode**，其效果大概就是这样的：

![](http://oqwcjoc6j.bkt.clouddn.com/xferModes.jpg)

我们代码中需要用到的就是**PorterDuff.Mode.SRC_OUT**这个属性，上图已经很明显了，就是去除其中的后画的这一块。

此时，对如何画浮层有一个大概的了解，然后就是需要制定某一块区域透明。这时候，我们就需要获取到需要透明的控件的位置。

```java
int[] location = new int[2];
cView.getLocationInWindow(location);
```

这时候**location**中就包含了View的位置，当我，然后根据view的宽高，计算一下，得到一个**rect**就可以了。但是，直接这样使用在Activity中会有一些问题，因为View在**onCreate()**方法中，并没有显示，还在**onMeasure()**的过程，所以，我们不可能知道他的位置，所以，我们需要在LayerView中实现**ViewTreeObserver.OnGlobalLayoutListener**接口，并实现**onGlobalLayout()**方法，获取需要显示的View的位置。

```java
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
```

```java
public void initshow() {
  //调用，监听View的生成状况
    cView.getViewTreeObserver().addOnGlobalLayoutListener(this);
}
```

虽然在**onCreate()**中可以实现，但是还是推荐先弹一个弹框出来询问用户是否需要引导，或者在**onWindowFocusChanged**中判断显示。

```java
  private boolean show;
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus&&!show){
            show=true;
            layerView = new LayerView.Builder(MainActivity.this)
                    .setLayerColor(Color.parseColor("#75000000"))
                    .setTextColor(Color.WHITE)
//                .setContent(rv.getLayoutManager().findViewByPosition(0))
                    .setContent(tv)
                    .build();
            layerView.initshow();
            layerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layerView.dismiss();
                }
            });  
        }
    }
```

因为在**RecycleView**中，并不能通过**rv.getLayoutManager().findViewByPosition(0)**来获取到控件的位置。

最后的操作，就是把屏幕变暗，在需要透明的位置，画一个形状。这里就涉及到一些Canvas的操作。

```java
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (parent != null) {
            //绘制有两种方法。
            drawBack1(canvas);
//            drawBack2(canvas);
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
    private void drawBack2(Canvas canvas) {
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
    private void drawBack1(Canvas canvas) {
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
```

到此，浮层基本就结束了，最后，附上[地址](https://github.com/AWarmHug/LayerView)。

