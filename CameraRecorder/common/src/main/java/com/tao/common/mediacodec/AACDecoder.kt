/**
 * @ClassName:      AACDecoder.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/7/22 5:10 PM
 */
package com.tao.common.mediacodec

import android.media.MediaCodec
import android.media.MediaFormat
import com.tao.common.callback.OnAudioDecodeListener
import com.tao.common.config.AudioConfiguration
import java.nio.ByteBuffer

class AACDecoder(private val audioConfiguration: AudioConfiguration):
        BaseAudioCodec(audioConfiguration) {

    override fun onAudioOutformat(outputFormat: MediaFormat?) {
    }

    public var mListener: OnAudioDecodeListener? = null

    fun setOnAudioEncodeListener(listener: OnAudioDecodeListener?) {
        mListener = listener
    }


    override fun onAudioData(bb: ByteBuffer, bi: MediaCodec.BufferInfo) {
        mListener?.onAudioPCMData(bb, bi)
    }
}