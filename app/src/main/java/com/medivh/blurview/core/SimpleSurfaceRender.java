package com.medivh.blurview.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

public class SimpleSurfaceRender extends SurfaceRender {

    SimpleSurfaceRender(Context context) {
        this.context = context;
    }

    public void drawFrame(final SurfaceHolder holder){
        final Paint paint = new Paint();

        HandlerThread thread = new HandlerThread("BlurThread");
        thread.start();

        blurHandler = new Handler(thread.getLooper()) {

            @Override
            public void handleMessage(@NonNull Message msg) {

                Bitmap background = (Bitmap) msg.obj;
                if (background == null) {
                    return;
                }

                long begin = System.currentTimeMillis();

                Canvas canvas = lockCanvas();
                if (canvas != null) {
                    Bitmap copy = Bitmap.createBitmap(background);
                    Bitmap blurBackground = blurRenderScript.blurBitmap(context, copy, BlurLayout.BLUR_RADIUS);
                    canvas.drawBitmap(blurBackground, null, dst, paint);

                    blurBackground.recycle();
                    copy.recycle();
                }
                unlockCanvasAndPost(canvas);

                Log.i("BlurLayout", (System.currentTimeMillis() - begin) + " draw surfaceView ");

            }
        };

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        this.surfaceWidth = width;
        this.surfaceHeight = height;
        dst = new Rect(0, 0, surfaceWidth, surfaceHeight);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    @Override
    void release() {
        if(blurHandler != null){
            blurHandler.getLooper().quit();
        }
    }
}
