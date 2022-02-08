/**
 * @ClassName:      DevYKSurfaceRenderer.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/8/22 8:14 PM
 */
package com.tao.aac_h264_encode_decode.mediacodec.video

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.opengl.GLSurfaceView
import android.text.TextPaint
import android.view.Display
import android.view.Surface
import android.view.WindowManager

class DevYKSurfaceRenderer:SurfaceRenderer {
    private var mPaint: TextPaint? = null
    private var mWidth = 0;
    private var mHeight = 0;

    private var mX = 0.0;

    private var TAG = "在线教育直播"

    constructor(context: Context, surface: Surface) : super(surface) {
        // setting some text paint
        if (mPaint == null) {
            mPaint = TextPaint()
            mPaint?.setAntiAlias(true)
            mPaint?.setColor(Color.GREEN)
            mPaint?.setTextSize(60f * context.getResources().getConfiguration().fontScale)
            mPaint?.setTextAlign(Paint.Align.CENTER)
        }
        mWidth = context.getScreenWidth()
        mHeight = context.getScreenHeight()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)
        if (mX >= 1) mX = 0.0
        mX +=0.004
        var matrix = Matrix()
        matrix.postScale(mX.toFloat(),mX.toFloat())
        canvas.setMatrix(matrix)
        mPaint?.let { canvas.drawText(TAG, (canvas.width.toFloat()/2), canvas.height.toFloat()/2, it) }
    }

    private fun Context.getDisplay(): Display? {
        val wm: WindowManager?
        if (this is Activity) {
            wm = this.windowManager
        } else {
            wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        }


        return wm?.defaultDisplay
    }

    private fun Context.getScreenWidth(): Int {
        val display = this.getDisplay() ?: return 0
        val point = Point()
        display.getSize(point)
        return point.x
    }

    private fun Context.getScreenHeight(): Int {
        val display = this.getDisplay() ?: return 0
        val point = Point()
        display.getSize(point)
        return point.y
    }

}