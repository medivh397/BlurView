package com.medivh.blurview.core;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
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

        getViewTreeObserver().addOnPreDrawListener(preDrawListener);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        surfaceView.setLayoutParams(lp);
        addView(surfaceView, 0);

    }

    private final ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            Log.i("BlurLayout", "onPreDraw");
            if (preDrawLock) {
                preDrawLock = false;
            } else {
                prepareBitmapAndPost();
            }
            return true;
        }
    };

    @Override
    protected void dispatchDraw(Canvas c) {
        if (canvasDrawLock) {
            return;
        }
        prepareBitmapAndPost();
        super.dispatchDraw(c);
    }


    private void prepareBitmapAndPost() {
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
            surfaceMatrix.postTranslate(-(getLeft() + getTranslationX()) * SURFACE_VIEW_BITMAP_SCALE,
                    -(getTop() + getTranslationY()) * SURFACE_VIEW_BITMAP_SCALE);
            surfaceViewCanvas.setMatrix(surfaceMatrix);
            surfaceViewCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            activityView.draw(surfaceViewCanvas);
            canvasDrawLock = false;
        }

        Log.i("BlurLayout", (System.currentTimeMillis() - begin) + " prepared surfaceViewBitmap");
        surfaceRender.updateBackground(surfaceViewBitmap);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (surfaceRender != null) {
            surfaceRender.release();
        }
        if (preDrawListener != null) {
            getViewTreeObserver().removeOnPreDrawListener(preDrawListener);
        }
        Log.i("BlurLayout", "onDetachedFromWindow");
    }

    public void setCoverColor(int color) {
        if (surfaceRender != null) {
            surfaceRender.setCoverColor(color);
        }
    }
}
