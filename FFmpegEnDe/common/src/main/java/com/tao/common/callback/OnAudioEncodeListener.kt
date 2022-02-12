/**
 * @ClassName:      OnAudioEncodeListener.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/7/22 5:02 PM
 */
package com.tao.common.callback

import android.media.MediaCodec
import android.media.MediaFormat
import java.nio.ByteBuffer

interface OnAudioEncodeListener {
    fun onAudioEncode(bb: ByteBuffer, bi: MediaCodec.BufferInfo)
    fun onAudioOutformat(outputFormat: MediaFormat?)
}