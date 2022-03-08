/**
 * @ClassName:      OnAudioDecodeListener.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/7/22 4:17 PM
 */
package com.tao.common.callback

import android.media.MediaCodec
import java.nio.ByteBuffer

interface OnAudioDecodeListener {
    fun onAudioPCMData(bb: ByteBuffer, bi: MediaCodec.BufferInfo)
}