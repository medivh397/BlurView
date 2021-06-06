package com.medivh.blurview.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

public abstract class SurfaceRender implements SurfaceHolder.Callback {

    Context context;
    Handler blurHandler;

    BlurRenderScript blurRenderScript = new BlurRenderScript();

    int surfaceWidth;
    int surfaceHeight;
    Rect dst;

    SurfaceHolder holder;

    int coverColor;

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
        Canvas canvas;
        if (holder == null) {
            return null;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canvas = holder.lockHardwareCanvas();
        }else{
            canvas = holder.lockCanvas();
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

    public void setCoverColor(int color){
        this.coverColor = color;
    }

    abstract void release();

}
