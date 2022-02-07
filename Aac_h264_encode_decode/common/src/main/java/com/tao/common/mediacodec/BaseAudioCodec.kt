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
import android.util.Log
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
}