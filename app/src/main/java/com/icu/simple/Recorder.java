package com.icu.simple;

import android.app.Activity;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.IOException;

public class Recorder {

    private MediaProjection mediaProjection;
    private MediaRecorder mediaRecorder;
    private VirtualDisplay virtualDisplay;

    private boolean running;
    private int width = 720;
    private int height = 1080;
    private int dpi;

    private Activity activity;
    private String currentSavePath;

    public boolean isInit() {
        if (mediaProjection == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void initParams(Activity activity) {
        this.activity = activity;
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels;
        height = metric.heightPixels;
        dpi = metric.densityDpi;
    }

    public void initMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        currentSavePath = Utils.generateVideoPath();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(currentSavePath);
        mediaRecorder.setVideoSize(width, height);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
        mediaRecorder.setVideoFrameRate(30);
        try {
            mediaRecorder.prepare();
            Log.e("mediaRecorder", "success");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("mediaRecorder", "error");
        }

        virtualDisplay = mediaProjection.createVirtualDisplay("MainScreen", width, height, dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mediaRecorder.getSurface(), null, null);
    }

    public void initRecorder(MediaProjection mediaProject) {
        mediaProjection = mediaProject;
    }



    public boolean startRecord() {
        if (mediaProjection == null || running) {
            return false;
        }

        initMediaRecorder();
        mediaRecorder.start();
        running = true;
        return true;
    }

    public String stopRecord() {
        if (!running) {
            return "";
        }
        running = false;
        mediaRecorder.stop();
        mediaRecorder.reset();
        virtualDisplay.release();
//    mediaProjection.stop();

        return currentSavePath;
    }
}
