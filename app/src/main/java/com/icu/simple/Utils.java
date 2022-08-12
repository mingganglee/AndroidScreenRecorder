package com.icu.simple;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class Utils {
    private static String baseDir = "ScreenRecorder";
    private static String imageDir = "Picture";
    private static String videoDir = "Video";
    private static String pngExt = "png";
    private static String mp4Ext = "mp4";



    public static String savePng(Bitmap bitmap) {
        return saveFile(bitmap, generatePngPath());
    }

    public static String saveFile(Bitmap bitmap, String savePath) {
        if (bitmap != null) {
            File fileImage = null;
            try {
                fileImage = new File(savePath);

                if (!fileImage.exists()) {
                    fileImage.createNewFile();
                }
                FileOutputStream out = new FileOutputStream(fileImage);
                if (out != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                    out.flush();
                    out.close();
                }
                Log.e("save path", fileImage.getPath());
                return savePath;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e("FileNotFoundException", e.getLocalizedMessage());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("IOException", e.getLocalizedMessage());
            }
        }
        return null;
    }

    public static String generatePngPath() {
        return generateSavePath(imageDir, pngExt);
    }

    public static String generateVideoPath() {
        return generateSavePath(videoDir, mp4Ext);
    }

    public static String generateSavePath(String saveFolder, String ext) {
        String saveDir = generateSaveDir(saveFolder);
        String savePath = String.format("%s/%d.%s", saveDir, System.currentTimeMillis(), ext);
        return savePath;
    }

    public static String generateSaveDir(String saveFolder) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String rootDir = Environment.getExternalStorageDirectory().getAbsolutePath() + String.format("/%s/%s", baseDir, saveFolder);
            File file = new File(rootDir);
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    return null;
                }
            }
            return rootDir;
        } else {
            return null;
        }
    }

    public static void print(Context context, String msg) {
        Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public interface Callback {
        void Success(String msg);
        void Error(String msg);
    }
}
