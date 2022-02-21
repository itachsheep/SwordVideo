/**
 * @ClassName:      FFmpegH264Native.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/21/22 9:06 PM
 */
package com.tao.ffmpeg

class FFmpegH264Native {
    /**
     * 编码初始化
     */
    external fun initEncode(
        inYUV420spPath: String,
        outH264Path: String,
        width: Int,
        height: Int,
        fps: Int,
        videoRate: Int
    ): Int

    /**
     * 解码初始化
     */
    external fun initDecode(
        inYUV420spPath: String,
        outH264Path: String,
        width: Int,
        height: Int,
        videoRate: Int
    ): Int

    external fun start()
    external fun release()
}