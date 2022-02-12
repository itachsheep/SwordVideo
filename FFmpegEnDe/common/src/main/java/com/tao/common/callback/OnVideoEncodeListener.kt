/**
 * @ClassName:      OnVideoEncodeListener.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/8/22 7:32 PM
 */
package com.tao.common.callback

import android.media.MediaCodec
import android.media.MediaFormat
import java.nio.ByteBuffer

interface OnVideoEncodeListener {
    fun onVideoEncode(bb: ByteBuffer?, bi: MediaCodec.BufferInfo?)
    fun onVideoOutformat(outputFormat: MediaFormat?)
}