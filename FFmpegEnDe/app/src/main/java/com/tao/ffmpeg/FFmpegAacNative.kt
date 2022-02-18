/**
 * @ClassName:      FFmpegNative.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/12/22 11:12 AM
 */
package com.tao.ffmpeg

class FFmpegAacNative {

    // aac encoder 编码
    external fun init(outAACPath: String, bitRate: Int,
                      channels: Int, sampleRate: Int): Int
    external fun encode(byteArray: ByteArray): Int
    external fun release()

    // aac decoder 解码
    external fun init(inAACPath: String, outPCMPath: String): Int
    external fun releaseDecoder()

    external fun test()

}