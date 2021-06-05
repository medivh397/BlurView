package com.medivh.blurview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BlurLayout extends FrameLayout {

    public final static float ACTIVITY_BITMAP_SCALE = 0.2f;
    public final static float SURFACE_VIEW_BITMAP_SCALE = 0.4f;
    public final static float BLUR_RADIUS = 10f;

    private View activityView;

    private SurfaceView surfaceView;
    private SurfaceRender surfaceRender;

    private Bitmap activityBitmap;
    private Bitmap surfaceViewBitmap;

    private Canvas activityCanvas = new Canvas();
    private Canvas surfaceViewCanvas = new Canvas();
    private Paint paint = new Paint();

    private Rect src;
    private Rect dst;

    private boolean canvasDrawLock;
    private boolean preDrawLock;
    private boolean isPauseBlurRunnablePost;

    public BlurLayout(@NonNull Context context) {
        this(context, null);
    }

    public BlurLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BlurLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        FrameLayout decorView = (FrameLayout) ((Activity) context).getWindow().getDecorView();

        activityView = decorView.findViewById(android.R.id.content);

        surfaceView = new SurfaceView(context);
        surfaceRender = new DoubleCacheSurfaceRender(context);
        surfaceView.getHolder().addCallback(surfaceRender);

        //todo not remove
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                Log.i("BlurLayout", "onPreDraw");
                if (preDrawLock) {
                    preDrawLock = false;
                } else {
                    updateBackground();
                }
                return true;
            }
        });
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        surfaceView.setLayoutParams(lp);
        addView(surfaceView, 0);

    }


    @Override
    protected void dispatchDraw(Canvas c) {
        if (canvasDrawLock) {
//            Log.i("BlurLayout", "drawLock return");
            return;
        }
        updateBackground();
        super.dispatchDraw(c);
    }

    private void updateBackground() {

        long begin = System.currentTimeMillis();
        int activityWidth = activityView.getWidth();
        int activityHeight = activityView.getHeight();
        int surfaceWidth = surfaceView.getWidth();
        int surfaceHeight = surfaceView.getHeight();

        if (surfaceWidth == 0 || surfaceHeight == 0) {
            return;
        }

        int scaledActivityWidth = (int) (activityWidth * ACTIVITY_BITMAP_SCALE);
        int scaledActivityHeight = (int) (activityHeight * ACTIVITY_BITMAP_SCALE);

        if (activityBitmap == null || scaledActivityWidth != activityBitmap.getWidth()
                || scaledActivityHeight != activityBitmap.getHeight()) {
            activityBitmap = Bitmap.createBitmap(scaledActivityWidth, scaledActivityHeight, Bitmap.Config.RGB_565);
            activityCanvas.setBitmap(activityBitmap);

            Matrix matrix = new Matrix();
            matrix.reset();
            matrix.preScale(ACTIVITY_BITMAP_SCALE, ACTIVITY_BITMAP_SCALE);
            matrix.postTranslate(0, 0);
            activityCanvas.setMatrix(matrix);
        }

        int scaledSurfaceWidth = (int) (surfaceWidth * SURFACE_VIEW_BITMAP_SCALE);
        int scaledSurfaceHeight = (int) (surfaceHeight * SURFACE_VIEW_BITMAP_SCALE);

        if (surfaceViewBitmap == null || scaledSurfaceWidth != surfaceViewBitmap.getWidth()
                || scaledSurfaceHeight != surfaceViewBitmap.getHeight()) {
            surfaceViewBitmap = Bitmap.createBitmap(scaledSurfaceWidth, scaledSurfaceHeight, Bitmap.Config.ARGB_8888);
            surfaceViewCanvas.setBitmap(surfaceViewBitmap);
        }

        if (!canvasDrawLock) {
            canvasDrawLock = true;
            setAlpha(0f);
            activityView.draw(activityCanvas);
            setAlpha(1);
            canvasDrawLock = false;

            if (!isPauseBlurRunnablePost) {
                isPauseBlurRunnablePost = true;
                //暂停surfaceView的更新
                getHandler().postDelayed(pauseBlurRunnable, 300);
            }

        }

        Log.i("BlurLayout", (System.currentTimeMillis() - begin) + " draw activityBitmap");

        int left = (int) ((getLeft() + getTranslationX()) * ACTIVITY_BITMAP_SCALE);
        int top = (int) ((getTop() + getTranslationY()) * ACTIVITY_BITMAP_SCALE);
        int right = (int) ((getRight() + getTranslationX()) * ACTIVITY_BITMAP_SCALE);
        int bottom = (int) ((getBottom() + getTranslationY()) * ACTIVITY_BITMAP_SCALE);
        if (src == null || dst == null) {
            src = new Rect(left, top, right, bottom);
            dst = new Rect(0, 0, (int) (surfaceWidth * SURFACE_VIEW_BITMAP_SCALE), (int) (surfaceHeight * SURFACE_VIEW_BITMAP_SCALE));
        } else {
            src.set(left, top, right, bottom);
            dst.set(0, 0, scaledSurfaceWidth, scaledSurfaceHeight);
        }

        surfaceViewCanvas.drawBitmap(activityBitmap, src, dst, paint);

        Log.i("BlurLayout", (System.currentTimeMillis() - begin) + " draw surfaceViewBitmap");

        surfaceRender.updateBackground(surfaceViewBitmap);
    }

    private Runnable pauseBlurRunnable = new Runnable() {
        @Override
        public void run() {
            preDrawLock = true;
            isPauseBlurRunnablePost = false;
            Log.i("BlurLayout", "pauseBlur");
        }
    };

    //todo resumeRender, pauseRender

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (surfaceRender != null) {
            surfaceRender.release();
        }
        Log.i("BlurLayout", "onDetachedFromWindow");
    }


}
