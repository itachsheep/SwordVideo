/**
 * @ClassName:      IMediaCodecListener.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/7/22 4:12 PM
 */
package com.tao.aac_h264_encode_decode.mediacodec

import android.media.MediaCodec
import java.nio.ByteBuffer

interface IMediaCodecListener {
    //处理音频硬编编码器输出的数据
    fun onAudioAACData(bb: ByteBuffer, bi: MediaCodec.BufferInfo) {}

    //开始打包，一般进行打包的预处理
    fun start()

    //结束打包，一般进行打包器的状态恢复
    fun stop()
}