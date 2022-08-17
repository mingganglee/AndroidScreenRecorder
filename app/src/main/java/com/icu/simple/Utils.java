package com.icu.simple;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class Utils {
    private static String baseDir = "DCIM/ScreenTools";
    private static String imageDir = "";
    private static String videoDir = "";
    private static String pngExt = "png";
    private static String mp4Ext = "mp4";

    private static Toast toast;

    public static int pendingScreenShotCount = 0;


    public static void shotUp() {
        pendingScreenShotCount++;
    }

    public static void shotDown() {
        pendingScreenShotCount--;
    }

    public static String savePng(Activity activity, Bitmap bitmap) {
        return saveImage(activity, bitmap, generatePngPath());
    }

    public static String saveImage(final Activity activity, Bitmap bitmap, String savePath) {
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

                    scanFile(activity, savePath);
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

    public static void scanFile(final Activity activity, String filePath) {
        MediaScannerConnection.scanFile(activity,
            new String[]{filePath},
            null,
            new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {
//                    Log.e("资源刷新成功", path);
                    File file = new File(path);
                    String msg = "";
                    if (file.getName().contains("mp4")) {
                        msg = String.format("保存%s", file.getName());
                    } else {
                        msg = String.format("保存%s", file.getName().split("\\.")[0]);
                    }

                    Log.e("资源刷新成功", Long.toString(pendingScreenShotCount));

                    if (pendingScreenShotCount == 0) {
                        print(activity, msg);
                    } else {
                        hidePrint(activity);
                    }

                }
            });
    }

    public static String generatePngPath() {
        return generateSavePath(imageDir, pngExt);
    }

    public static String generateVideoPath() {
        return generateSavePath(videoDir, mp4Ext);
    }

    public static String generateSavePath(String saveFolder, String ext) {
        String saveDir = generateSaveDir(saveFolder);
        String fd = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS").format(System.currentTimeMillis());
        String savePath = String.format("%s/%s.%s", saveDir, fd, ext);
        return savePath;
    }

    public static String generateSaveDir(String saveFolder) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String rootDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            if (baseDir.isEmpty()) {
                rootDir += String.format("/%s", saveFolder);
            } else {
                rootDir += String.format("/%s/%s", baseDir, saveFolder);
            }
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

    public static void print(Activity activity, String msg) {
        if (toast == null) {
            toast = Toast.makeText(activity, null, Toast.LENGTH_SHORT);
        }

        toast.setText(msg);
        toast.show();
    }

    public static void hidePrint(Activity activity) {
        if (toast == null) {
            toast = Toast.makeText(activity, null, Toast.LENGTH_SHORT);
        }

        toast.cancel();
    }

    public interface Callback {
        void Success(String msg);
        void Error(String msg);
    }
}
