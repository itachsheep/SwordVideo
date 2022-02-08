/**
 * @ClassName:      H264Decoder.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/8/22 8:23 PM
 */
package com.tao.aac_h264_encode_decode.mediacodec.video

import android.media.MediaCodec
import com.tao.common.config.VideoConfiguration
import com.tao.common.mediacodec.video.BaseVideoDecoder
import java.nio.ByteBuffer

class H264Decoder:BaseVideoDecoder() {
    override fun onVideoEncode(bb: ByteBuffer?, mBufferInfo: MediaCodec.BufferInfo) {
        super.onVideoEncode(bb, mBufferInfo)
    }


    override fun configure(videoConfiguration: VideoConfiguration) {
        super.configure(videoConfiguration)
    }

    override fun start() {
        super.start()

    }

    override fun stop() {
        super.stop()
    }
}