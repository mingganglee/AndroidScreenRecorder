package com.icu.simple;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;

import java.nio.ByteBuffer;

public class Capture {

    private int width;
    private int height;
    private int dpi;

    private MediaProjection mediaProjection;
    private MediaProjectionManager projectionManager;
    private ImageReader imageReader;

    private Activity activity;

    public boolean isInit() {
        if (mediaProjection == null) {
            return false;
        } else {
            return true;
        }
    }

    public void initParams(Activity activity) {
        this.activity = activity;
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels;
        height = metric.heightPixels;
        dpi = metric.densityDpi;
    }

    public void initCapture(MediaProjection mediaProject) {
        mediaProjection = mediaProject;
        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 1);
        mediaProjection.createVirtualDisplay("ScreenShout",
                width,height,dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.getSurface(),null,null);
    }

    public String capture() {
        if (mediaProjection == null) {
            return null;
        } else if (imageReader == null) {
            return null;
        } else {
            SystemClock.sleep(100);
            Image image = imageReader.acquireNextImage();
            if (image == null) {
                Log.e("startCapture", "image is null.");
                return null;
            }
            int width = image.getWidth();
            int height = image.getHeight();
            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer buffer = planes[0].getBuffer();
            int pixelStride = planes[0].getPixelStride();
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;
            Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);
            image.close();
            return Utils.savePng(bitmap);
        }
    }

}
