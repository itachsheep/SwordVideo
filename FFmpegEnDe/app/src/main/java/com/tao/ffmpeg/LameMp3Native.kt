/**
 * @ClassName:      LameMp3Native.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/25/22 12:37 PM
 */
package com.tao.ffmpeg

class LameMp3Native {
    external fun init(outURL: String, sampleRate: Int, channels: Int, bitRate: Int):Int;
    external fun encode(byteArray: ByteArray);
    external fun release();
}