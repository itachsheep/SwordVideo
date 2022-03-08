/**
 * @ClassName:      IAudioCodec.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/7/22 5:12 PM
 */
package com.tao.common.mediacodec

interface IAudioCodec {
    /**
     * 准备编码
     */
    fun prepareCoder()

    /**
     * 将数据送入编解码器
     */
    fun enqueueCodec(input: ByteArray?);

    /**
     * 停止编码
     */
    fun stop();
}