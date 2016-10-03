package org.wxc.myandroidtoolbox.ecg.cache;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by black on 2016/7/13.
 */
public class EcgScanView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "HRSurfaceView";

    private SurfaceHolder holder;
    private Canvas canvas;
    private Paint paint;
    private boolean isShowing;
    private float density;

    private int height;

    private static final float RESOLUTION = 50f;
    private static final int DATA_COUNT = 1080;
    public static final int ONCE_DRAW_COUNT = 10;

    /**
     * 一次绘制的宽度，不含扫描柱，与view的宽度有关
     */
    private float interval;

    private Rect drawRect;
    private int index;

    private int[] dataArray;

    /**
     * 用于计算单次绘制时间
     */
    private long lastTime;
    private float times = 1.0f;

    public EcgScanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        density = getResources().getDisplayMetrics().density;
        holder = getHolder();
        holder.addCallback(this);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(1.5f * density);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setTextSize(14 * density);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        height = getMeasuredHeight();
        int width = getMeasuredWidth();
//        Log.i("measure", "width=" + width);
        interval = (float) width * (float) ONCE_DRAW_COUNT / (float) DATA_COUNT;
//        Log.i("measure", "interval=" + interval);
    }

    //在surface的大小发生改变时激发
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//        Log.i(TAG, "surfaceChanged");
    }

    // 在创建时激发，一般在这里调用画图的线程。
    public void surfaceCreated(SurfaceHolder holder) {
//        Log.i(TAG, "surfaceCreated");
        isShowing = true;
    }

    //销毁时激发，一般在这里将画图的线程停止、释放。
    public void surfaceDestroyed(SurfaceHolder holder) {
//        Log.i(TAG, "surfaceDestroyed");
        isShowing = false;
    }

    private void setRectAndDraw() {

        if (isShowing) {
            drawRect = new Rect((int) (interval * index), 0, (int) (interval * (2 +
                    index)), height);
            draw();
        }
        index++;
        if (index > DATA_COUNT / ONCE_DRAW_COUNT) index = 0;
    }

    private void draw() {
        long now = System.currentTimeMillis();
//        Log.i(TAG, "draw():" + index + ",用时： " + (now - lastTime));
        lastTime = now;



        try {
            canvas = holder.lockCanvas(drawRect);
            if (canvas != null) {
                canvas.drawColor(Color.BLACK);
                if (dataArray != null) for (int i = 0; i < dataArray.length - 1; i++) {
                    float startX = (int)(interval * index + interval * i / ONCE_DRAW_COUNT);
                    float stopX = (int)(interval * index + (i + 1) * interval / ONCE_DRAW_COUNT);
                    float startY = (int)(height / 2 - dataArray[i] / RESOLUTION * times * density);
                    float stopY = (int)(height / 2 - dataArray[i + 1] / RESOLUTION * times * density);
                    canvas.drawLine(startX, startY, stopX, stopY, paint);
                }
            }

        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
        }

    }

    public void setDataArrayAndDraw(int[] dataArray) {
        this.dataArray = dataArray;
        setRectAndDraw();
    }

    public void setResolutionTimes(float times) {
        this.times = times;
    }

    public float getResolution() {
        return times;
    }
}
