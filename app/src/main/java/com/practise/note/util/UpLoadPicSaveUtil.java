package com.practise.note.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Author: Pepper
 * Date :  2018/5/18
 * Summary:${ToDo}.
 */

public class UpLoadPicSaveUtil {
    private static String backetName = "images";

    public static String saveFile(Context context, Bitmap bitmap) {
        String filePath = null;
        filePath = context.getFilesDir().getAbsolutePath().toString() + File.separator + backetName + File.separator + System.currentTimeMillis() + ".jpg";

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("imagePath", "图片已保存在" + filePath);
        return filePath;
    }
}
