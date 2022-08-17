package com.icu.simple;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final int SCREEN_SHOT = 0;
    private static final int SCREEN_RECORDER = 1;
    private static final int STORAGE_REQUEST_CODE = 102;
    private static final int AUDIO_REQUEST_CODE   = 103;

    MediaProjection mediaProjection;
    MediaProjectionManager projectionManager;
    AudioManager audioManager;

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
        audioManager = ((AudioManager) getSystemService(AUDIO_SERVICE));
        capture.initParams(MainActivity.this);
        recorder.initParams(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_REQUEST_CODE || requestCode == AUDIO_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCREEN_SHOT) {
            mediaProjection = projectionManager.getMediaProjection(resultCode, data);
            capture.initCapture(mediaProjection);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    capture.capture();
                }
            }, 300);
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

                capture.capture();
            }

        } else {

            capture.capture();
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
            Utils.scanFile(this, savePath);
        }
    }

    public void Mute(View view) {
        audioManager.setMicrophoneMute(true);
        Utils.print(MainActivity.this, "点击静音按钮" + audioManager.isMicrophoneMute());
    }

    public void UnMute(View view) {
        audioManager.setMicrophoneMute(false);
        Utils.print(MainActivity.this, "点击取消静音按钮" + audioManager.isMicrophoneMute());
    }
}