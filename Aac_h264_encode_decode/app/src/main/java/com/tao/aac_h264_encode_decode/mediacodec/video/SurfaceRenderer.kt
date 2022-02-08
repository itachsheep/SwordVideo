/**
 * @ClassName:      SurfaceRenderer.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/8/22 8:14 PM
 */
package com.tao.aac_h264_encode_decode.mediacodec.video

import android.graphics.Canvas
import android.view.Surface

abstract class SurfaceRenderer(internal var mSurface: Surface) {
    internal var mRenderer: Renderer? = null

    abstract  fun onDraw(canvas: Canvas);

    fun start() {
        if (mRenderer == null) {
            mRenderer = Renderer()
            mRenderer!!.setRunning(true)
            mRenderer!!.start()
        }
    }

    fun stopAndWait() {
        if (mRenderer != null) {
            mRenderer!!.setRunning(false)
            // we want to make sure complete drawing cycle, otherwise
            // unlockCanvasAndPost() will be the one who may or may not throw
            // IllegalStateException
            try {
                mRenderer!!.join()
            } catch (ignore: InterruptedException) {
            }

            mRenderer = null
        }
    }

    internal inner class Renderer : Thread() {
        @Volatile
        var mRunning: Boolean = false

        fun setRunning(running: Boolean) {
            mRunning = running
        }

        override fun run() {
            while (mRunning) {
                val canvas = mSurface.lockCanvas(null)
                try {
                    onDraw(canvas)
                } finally {
                    mSurface.unlockCanvasAndPost(canvas)
                }
            }
        }
    }
}