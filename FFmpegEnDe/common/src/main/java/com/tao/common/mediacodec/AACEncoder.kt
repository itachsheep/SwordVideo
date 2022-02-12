/**
 * @ClassName:      AACEncoder.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/7/22 8:02 PM
 */
package com.tao.common.mediacodec

import android.media.MediaCodec
import android.media.MediaFormat
import com.tao.common.callback.OnAudioEncodeListener
import com.tao.common.config.AudioConfiguration
import java.nio.ByteBuffer

class AACEncoder(private val mAudioConfiguration: AudioConfiguration)
    : BaseAudioCodec(mAudioConfiguration)  {

    override fun onAudioOutformat(outputFormat: MediaFormat?) {
        mListener?.onAudioOutformat(outputFormat)
    }

    public var mListener: OnAudioEncodeListener? = null


    override fun onAudioData(bb: ByteBuffer, bi: MediaCodec.BufferInfo) {
        mListener?.onAudioEncode(bb, bi)
    }

    fun setOnAudioEncodeListener(listener: OnAudioEncodeListener?) {
        mListener = listener
    }

    override fun stop() {
        super.stop()
        mListener = null
    }
}