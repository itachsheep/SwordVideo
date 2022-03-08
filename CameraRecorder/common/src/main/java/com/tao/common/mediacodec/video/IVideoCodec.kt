/**
 * @ClassName:      IVideoCodec.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/8/22 7:44 PM
 */
package com.tao.common.mediacodec.video

import android.media.MediaCodec
import com.tao.common.config.VideoConfiguration
import java.nio.ByteBuffer

interface IVideoCodec {

    /**
     * 初始化编码器
     */
    fun prepare(videoConfiguration: VideoConfiguration = VideoConfiguration.createDefault()){};

    /**
     * start 编码
     */
    fun start();

    /**
     * 停止编码
     */
    fun stop();

    /**
     * 返回编码好的 H264 数据
     */
    fun onVideoEncode(bb: ByteBuffer?, mBufferInfo: MediaCodec.BufferInfo)


}