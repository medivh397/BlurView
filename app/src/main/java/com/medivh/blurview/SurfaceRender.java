package com.medivh.blurview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

public abstract class SurfaceRender implements SurfaceHolder.Callback {

    Context context;
    Handler blurHandler;

    int surfaceWidth;
    int surfaceHeight;
    Rect dst;

    SurfaceHolder holder;

    abstract void drawFrame(SurfaceHolder holder);

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        this.holder = holder;
        drawFrame(holder);
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

    Canvas lockCanvas() {
        Canvas canvas = null;
        if (holder == null) {
            return null;
        }
        try {
            canvas = holder.lockHardwareCanvas();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas == null) {
                canvas = holder.lockCanvas();
            }
        }
        return canvas;
    }

    void unlockCanvasAndPost(Canvas canvas) {
        try{
            holder.unlockCanvasAndPost(canvas);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void updateBackground(Bitmap background) {
        if (blurHandler != null) {
            Message msg = Message.obtain();
            msg.obj = background;
            blurHandler.sendMessage(msg);
        }
    }

    abstract void release();

}