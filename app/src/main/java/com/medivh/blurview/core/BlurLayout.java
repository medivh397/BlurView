package com.medivh.blurview.core;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BlurLayout extends FrameLayout {

    public final static float SURFACE_VIEW_BITMAP_SCALE = 0.4f;
    public final static float BLUR_RADIUS = 10;

    private View activityView;

    private SurfaceView surfaceView;
    private SurfaceRender surfaceRender;

    private Bitmap surfaceViewBitmap;

    private final Canvas surfaceViewCanvas = new Canvas();
    private final Matrix surfaceMatrix = new Matrix();

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
            return;
        }
        updateBackground();
        super.dispatchDraw(c);
    }



    private void updateBackground() {

        long begin = System.currentTimeMillis();
        int surfaceWidth = surfaceView.getWidth();
        int surfaceHeight = surfaceView.getHeight();

        if (surfaceWidth == 0 || surfaceHeight == 0) {
            return;
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
            surfaceMatrix.reset();
            surfaceMatrix.preScale(SURFACE_VIEW_BITMAP_SCALE, SURFACE_VIEW_BITMAP_SCALE);
            surfaceMatrix.postTranslate(0, -(getTop() + getTranslationY()) * SURFACE_VIEW_BITMAP_SCALE);
            surfaceViewCanvas.setMatrix(surfaceMatrix);

            setAlpha(0f);
            activityView.draw(surfaceViewCanvas);
            setAlpha(1);
            canvasDrawLock = false;

            if (!isPauseBlurRunnablePost) {
                isPauseBlurRunnablePost = true;
                //暂停surfaceView的更新
                getHandler().postDelayed(pauseBlurRunnable, 300);
            }

        }

        Log.i("BlurLayout", (System.currentTimeMillis() - begin) + " prepared surfaceViewBitmap");

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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (surfaceRender != null) {
            surfaceRender.release();
        }
        Log.i("BlurLayout", "onDetachedFromWindow");
    }

    public void setCoverColor(int color) {
        if (surfaceRender != null) {
            surfaceRender.setCoverColor(color);
        }
    }
}
