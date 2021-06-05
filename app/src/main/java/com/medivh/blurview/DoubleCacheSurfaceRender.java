package com.medivh.blurview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

public class DoubleCacheSurfaceRender extends SurfaceRender {

    private final Deque<Bitmap> bufferQueue = new LinkedList<>();
    private final ReentrantLock reentrantLock = new ReentrantLock();

    private boolean stopThread = false;

    public DoubleCacheSurfaceRender(Context context) {
        this.context = context;
        init();
    }

    private void init (){
        HandlerThread blurThread = new HandlerThread("BlurThread");
        blurThread.start();

        blurHandler = new Handler(blurThread.getLooper()) {

            @Override
            public void handleMessage(@NonNull Message msg) {
                long begin = System.currentTimeMillis();
                Bitmap background = (Bitmap) msg.obj;
                if (background == null) {
                    return;
                }

                Bitmap copy = Bitmap.createBitmap(background);
                Bitmap blurBackground = BlurUtil.blurBitmap(context, copy, BlurLayout.BLUR_RADIUS);
                Log.i("BlurLayout", "cost:" + (System.currentTimeMillis() - begin) +"  blur bitmap");

                reentrantLock.lock();
                if (bufferQueue.size() > 1) {
                    bufferQueue.removeFirst();
                }
                bufferQueue.offer(blurBackground);
                reentrantLock.unlock();

                copy.recycle();
            }
        };
    }
    @Override
    public void surfaceCreated(@NonNull final SurfaceHolder holder) {
        super.surfaceCreated(holder);
    }

    @Override
    void drawFrame(final SurfaceHolder holder) {

        final Paint paint = new Paint();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap blurBackground;
                while (true) {
                    if (stopThread) {
                        return;
                    }
                    if (bufferQueue.size() == 0) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    long begin = System.currentTimeMillis();

                    reentrantLock.lock();
                    blurBackground = bufferQueue.poll();
                    reentrantLock.unlock();

                    if (blurBackground == null) {
                        continue;
                    }

                    Canvas canvas = lockCanvas();

                    if (canvas != null) {
                        canvas.drawBitmap(blurBackground, null, dst, paint);
                    }

                    unlockCanvasAndPost(canvas);
                    blurBackground.recycle();

                    Log.i("BlurLayout", (System.currentTimeMillis() - begin) + " draw surfaceView ");
                }

            }
        });

        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();

    }

    @Override
    void release() {
        if (blurHandler != null) {
            blurHandler.getLooper().quit();
            stopThread = true;
        }
    }
}
