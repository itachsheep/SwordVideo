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

    public external fun init(outAACPath: String, bitRate: Int, channels: Int, sampleRate: Int): Int;
    public external fun init(inAACPath: String, outPCMPath: String): Int;
    public external fun encode(byteArray: ByteArray): Int;
    public external fun release();
}