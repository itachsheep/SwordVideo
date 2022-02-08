/**
 * @ClassName:      IAudioController.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/7/22 5:03 PM
 */
package com.tao.aac_h264_encode_decode.mediacodec.audio

import com.tao.common.callback.OnAudioEncodeListener
import com.tao.common.config.AudioConfiguration

interface IAudioController {
    val sessionId: Int
    fun start()
    fun stop()
    fun pause()
    fun resume()
    fun mute(mute: Boolean)
    fun setAudioConfiguration(audioConfiguration: AudioConfiguration)
    fun setAudioEncodeListener(listener: OnAudioEncodeListener?)
}