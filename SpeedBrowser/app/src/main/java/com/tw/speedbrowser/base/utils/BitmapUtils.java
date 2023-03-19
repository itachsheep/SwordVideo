package com.tw.speedbrowser.base.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

public class BitmapUtils {

    /**
     * 截屏
     *
     * @return
     */
    public static Bitmap getViewBitmap(View view) {
        if (view.getWidth() <=0) return null;
        Bitmap bmp = Bitmap.createBitmap(view.getWidth(), 1, Bitmap.Config.ARGB_8888);
        int rowBytes = bmp.getRowBytes();
        bmp = null;

        if (rowBytes*view.getHeight()>=getAvailMemory()){
            return null;
        }
        bmp = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        view.draw(canvas);
        return bmp;
    }
    private static long getAvailMemory() {
        return Runtime.getRuntime().maxMemory();
    }


}
