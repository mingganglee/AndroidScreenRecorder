package com.icu.simple;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final int SCREEN_SHOT = 0;
    private static final int SCREEN_RECORDER = 1;
    private static final int STORAGE_REQUEST_CODE = 102;
    private static final int AUDIO_REQUEST_CODE   = 103;

    MediaProjection mediaProjection;
    MediaProjectionManager projectionManager;

    Capture capture = new Capture();
    Recorder recorder = new Recorder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.RECORD_AUDIO}, AUDIO_REQUEST_CODE);
        }


        projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        capture.initParams(MainActivity.this);
        recorder.initParams(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCREEN_SHOT) {
            if (resultCode == RESULT_OK) {
                mediaProjection = projectionManager.getMediaProjection(resultCode, data);
                capture.initCapture(mediaProjection);
                String savePath = capture.capture();
                Utils.print(MainActivity.this, savePath);
            }
        } else if (requestCode == SCREEN_RECORDER) {
            mediaProjection = projectionManager.getMediaProjection(resultCode, data);
            recorder.initRecorder(mediaProjection);
            recorder.startRecord();
        }
    }

    public void StartScreenShot(View view) {
        if (!capture.isInit()) {
            if (mediaProjection == null) {
                startActivityForResult(projectionManager.createScreenCaptureIntent(), SCREEN_SHOT);
            } else {
                capture.initCapture(mediaProjection);
                String savePath = capture.capture();
                Utils.print(MainActivity.this, savePath);
            }

        } else {
            String savePath = capture.capture();
            Utils.print(MainActivity.this, savePath);
        }
    }

    public void StartRecorder(View view) {
        if (!recorder.isInit()) {
            if (mediaProjection == null) {
                startActivityForResult(projectionManager.createScreenCaptureIntent(), SCREEN_RECORDER);
            } else {
                recorder.initRecorder(mediaProjection);
                recorder.startRecord();
            }
        } else {
            recorder.startRecord();
        }
    }

    public void StopRecorder(View view) {
        if (recorder.isRunning()) {
            String savePath = recorder.stopRecord();
            Utils.print(MainActivity.this, savePath);
        }
    }
}