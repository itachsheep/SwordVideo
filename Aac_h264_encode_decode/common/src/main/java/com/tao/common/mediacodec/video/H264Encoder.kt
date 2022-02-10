/**
 * @ClassName:      H264Encoder.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/8/22 7:58 PM
 */
package com.tao.common.mediacodec.video

import android.media.MediaCodec
import android.media.MediaFormat
import com.tao.common.callback.OnVideoEncodeListener
import java.nio.ByteBuffer

open class H264Encoder:BaseVideoEncoder() {
    private var mListener: OnVideoEncodeListener? = null


    override fun onVideoOutformat(outputFormat: MediaFormat?) {
        mListener?.onVideoOutformat(outputFormat)
    }

    /**
     * 视频编码完成的回调
     */
    override fun onVideoEncode(bb: ByteBuffer?, bi: MediaCodec.BufferInfo) {
        mListener?.onVideoEncode(bb!!, bi)
    }

    /**
     * 设置编码回调
     */
    fun setOnVideoEncodeListener(listener: OnVideoEncodeListener) {
        mListener = listener
    }

}