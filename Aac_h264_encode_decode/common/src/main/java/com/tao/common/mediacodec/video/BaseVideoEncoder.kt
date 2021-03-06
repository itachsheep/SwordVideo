/**
 * @ClassName:      BaseVideoEncoder.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/8/22 7:59 PM
 */
package com.tao.common.mediacodec.video

import android.annotation.TargetApi
import android.media.MediaCodec
import android.media.MediaFormat
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import com.tao.common.LogHelper
import com.tao.common.config.VideoConfiguration
import java.util.concurrent.locks.ReentrantLock

abstract class BaseVideoEncoder:IVideoCodec {
    private var mMediaCodec: MediaCodec? = null
    private var mPause: Boolean = false
    private var mHandlerThread: HandlerThread? = null
    private var mEncoderHandler: Handler? = null
    protected var mConfiguration = VideoConfiguration.createDefault()
    private var mBufferInfo: MediaCodec.BufferInfo? = null
    @Volatile
    private var isStarted: Boolean = false
    private val encodeLock = ReentrantLock()
    private lateinit var mSurface: Surface
    public val TAG = this.javaClass.simpleName

    protected var mPts = 0L
    /**
     * 准备硬编码工作
     */
    override fun prepare(videoConfiguration: VideoConfiguration) {
        videoConfiguration?.run {
            mConfiguration = videoConfiguration
            mMediaCodec = VideoMediaCodec.getVideoMediaCodec(mConfiguration)
            LogHelper.e(TAG, "prepare success!")
        }
    }

    /**
     * 渲染画面销毁了 open 子类可以重写
     */
    protected open fun onSurfaceDestory(surface: Surface?) {
    }

    /**
     * 可以创建渲染画面了 open 子类可以重写
     */
    protected open fun onSurfaceCreate(surface: Surface?) {

    }

    /**
     * 创建一个输入型的 Surface
     */
    open fun getSurface(): Surface? {
        return mSurface
    }


    /**
     * 开始编码
     */
    override fun start() {
        mHandlerThread = HandlerThread("AVSample-Encode")

        mHandlerThread?.run {
            this.start()
            mEncoderHandler = Handler(getLooper())
            mBufferInfo = MediaCodec.BufferInfo()
            //必须在  mMediaCodec?.start() 之前
            /**
             * 编码的地方，将自己的surface给到 canvas 来绘制，相当于绑定了输入
             *
             *  假如想处理原始视频帧，需要将原始视频帧编码为类似于h264或者其他格式，
             *  需要调用createInputSurface()方法产生一个surface，
             *  并且必须在configure之后，这个surface上目前是空数据，
             *  然后调用start方法，当有数据注入到surface时，
             */
            mSurface = mMediaCodec!!.createInputSurface()
            LogHelper.d(TAG,"start createInputSurface surface = $mSurface")

            mMediaCodec?.start()
            mEncoderHandler?.post(swapDataRunnable)
            isStarted = true
            //必须在  mMediaCodec?.start() 之后
            onSurfaceCreate(mSurface)
        }
    }

    /**
     * 编码的线程
     */
    private val swapDataRunnable = Runnable { drainEncoder() }

    /**
     * 停止编码
     */
    override fun stop() {
        if (!isStarted) return
        isStarted = false
        mEncoderHandler?.removeCallbacks(swapDataRunnable)
        mHandlerThread?.quit()
        encodeLock.lock()
        //提交一个空的缓冲区
        mMediaCodec?.signalEndOfInputStream()
        releaseEncoder()
        encodeLock.unlock()
    }

    /**
     * 释放编码器
     */
    private fun releaseEncoder() {
        onSurfaceDestory(getSurface())
        mMediaCodec?.stop()
        mMediaCodec?.release()
        mMediaCodec = null
    }

    /**
     * 动态码率设置
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun setEncodeBps(bps: Int) {
        if (mMediaCodec == null) {
            return
        }
        LogHelper.d(TAG, "bps :" + bps * 1024)
        val bitrate = Bundle()
        bitrate.putInt(MediaCodec.PARAMETER_KEY_VIDEO_BITRATE, bps * 1024)
        mMediaCodec?.setParameters(bitrate)
    }

    /**
     * 编码函数
     */
    private fun drainEncoder() {
        LogHelper.d(TAG,"drainEncoder ---> mMediaCodec = $mMediaCodec")
        val outBuffers = mMediaCodec?.getOutputBuffers()
        if (!isStarted) {
            // if not running anymore, complete stream
            mMediaCodec?.signalEndOfInputStream()
        }
        while (isStarted) {
            encodeLock.lock()
            if (mMediaCodec != null) {

                // 从输出缓冲区队列中拿到编解码后的内容，进行相应操作后释放，供下一次使用
                val outBufferIndex = mMediaCodec?.dequeueOutputBuffer(mBufferInfo!!, 12000)

//                LogHelper.d(TAG,"drainEncoder ---> outBufferIndex = $outBufferIndex")
                if (outBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    onVideoOutformat(mMediaCodec?.outputFormat)
                }

                if (outBufferIndex!! >= 0) {
                    val bb = outBuffers!![outBufferIndex]
                    if (mPts == 0L)
                        mPts = System.nanoTime() / 1000;

                    mBufferInfo!!.presentationTimeUs = System.nanoTime()/1000-mPts;


//                    LogHelper.e(TAG,"视频时间戳：${mBufferInfo!!.presentationTimeUs/1000_000}")
                    if (!mPause) {
                        onVideoEncode(bb, mBufferInfo!!)
                    }
                    mMediaCodec?.releaseOutputBuffer(outBufferIndex, false)
                } else {
                    try {
                        // wait 10ms
                        Thread.sleep(10)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                }
                encodeLock.unlock()
            } else {
                encodeLock.unlock()
                break
            }
        }
    }

    abstract fun onVideoOutformat(outputFormat: MediaFormat?)

    /**
     * 获取输出的格式
     */
    public fun getOutputFormat(): MediaFormat? = mMediaCodec?.outputFormat

}