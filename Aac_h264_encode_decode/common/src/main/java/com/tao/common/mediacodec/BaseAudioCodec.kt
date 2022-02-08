/**
 * @ClassName:      BaseAudioCodec.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/7/22 5:11 PM
 */
package com.tao.common.mediacodec

import android.media.MediaCodec
import android.media.MediaFormat
import com.tao.common.LogHelper
import com.tao.common.config.AudioConfiguration
import java.nio.ByteBuffer

abstract class BaseAudioCodec(
        private val mAudioConfiguration: AudioConfiguration)
    : IAudioCodec {
    private var mMediaCodec: MediaCodec? = null
    internal var mBufferInfo = MediaCodec.BufferInfo()
    private var TAG = javaClass.simpleName
    private var mPts = 0L
    private var prevOutputPTSUs: Long = 0

    /**
     * 编码完成的函数自己不处理，交由子类处理
     */
    abstract fun onAudioData(bb: ByteBuffer, bi: MediaCodec.BufferInfo);

    @Synchronized
    override fun prepareCoder() {
        mPts = 0;
        mMediaCodec = AudioMediaCodec.getAudioMediaCodec(mAudioConfiguration!!)
        mMediaCodec!!.start()
        LogHelper.d("encode", "--start")
    }

    /**
     * 将数据入队 java.lang.IllegalStateException
     */
    @Synchronized
    override fun enqueueCodec(input: ByteArray?) {
        if (mMediaCodec == null) {
            return
        }
        val inputBuffers = mMediaCodec!!.inputBuffers
        val outputBuffers = mMediaCodec!!.outputBuffers
        val inputBufferIndex = mMediaCodec!!.dequeueInputBuffer(12000)

        if (inputBufferIndex >= 0) {
            val inputBuffer = inputBuffers[inputBufferIndex]
            inputBuffer.clear()
            inputBuffer.put(input)
            mMediaCodec!!.queueInputBuffer(inputBufferIndex, 0, input!!.size, 0, 0)
        }

        var outputBufferIndex = mMediaCodec!!.dequeueOutputBuffer(mBufferInfo, 12000)
        if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            onAudioOutformat(mMediaCodec?.outputFormat)
        }



        while (outputBufferIndex >= 0) {
            val outputBuffer = outputBuffers[outputBufferIndex]

            if (mPts == 0L)
                mPts = System.nanoTime() / 1000;
//
            mBufferInfo!!.presentationTimeUs = System.nanoTime() / 1000 - mPts;
//            mBufferInfo!!.presentationTimeUs = getPTSUs()
           // LogHelper.e(TAG, "音频时间戳：${mBufferInfo!!.presentationTimeUs / 1000_000}")
            onAudioData(outputBuffer, mBufferInfo)
            prevOutputPTSUs = mBufferInfo.presentationTimeUs
            mMediaCodec!!.releaseOutputBuffer(outputBufferIndex, false)
            outputBufferIndex = mMediaCodec!!.dequeueOutputBuffer(mBufferInfo, 0)
        }
    }

    abstract fun onAudioOutformat(outputFormat: MediaFormat?)

    @Synchronized
    override fun stop() {
        if (mMediaCodec != null) {
            mMediaCodec!!.stop()
            mMediaCodec!!.release()
            mMediaCodec = null
        }
    }

    /**
     * 获取输出的格式
     */
    public fun getOutputFormat(): MediaFormat? = mMediaCodec?.outputFormat

    protected fun getPTSUs(): Long {
        var result = System.nanoTime() / 1000L
        // presentationTimeUs should be monotonic
        // otherwise muxer fail to write
        if (result < prevOutputPTSUs) {
            result = prevOutputPTSUs - result + result
        }
        return result
    }

}