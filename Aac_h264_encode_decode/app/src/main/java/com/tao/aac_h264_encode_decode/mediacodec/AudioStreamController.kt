/**
 * @ClassName:      AudioStreamController.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/7/22 5:02 PM
 */
package com.tao.aac_h264_encode_decode.mediacodec

import android.media.MediaCodec
import android.media.MediaFormat
import com.tao.common.callback.OnAudioEncodeListener
import com.tao.common.config.AudioConfiguration
import java.nio.ByteBuffer

class AudioStreamController: OnAudioEncodeListener {
    private var mPacker: IMediaCodecListener? = null
    private lateinit var mAudioController: IAudioController

    constructor(audioProcessor: IAudioController) {
        mAudioController = audioProcessor
    }

    public fun setListener(listener: IMediaCodecListener) {
        mPacker = listener
    }

    fun setAudioConfiguration(audioConfiguration: AudioConfiguration) {
        mAudioController.setAudioConfiguration(audioConfiguration)
    }

    @Synchronized
    fun start() {
        EncodeCastUtils.processNotUI(object : EncodeCastUtils.INotUIProcessor {
            override fun process() {
                mPacker?.start()
                mAudioController.setAudioEncodeListener(this@AudioStreamController)
                mAudioController.start()
            }
        })
    }

    @Synchronized
    fun stop() {
        EncodeCastUtils.processNotUI(object : EncodeCastUtils.INotUIProcessor {
            override fun process() {
                mAudioController.setAudioEncodeListener(null)
                mAudioController.stop()
                mPacker?.stop()
                mPacker = null
            }
        })
    }

    @Synchronized
    fun pause() {
        EncodeCastUtils.processNotUI(object : EncodeCastUtils.INotUIProcessor {
            override fun process() {
                mAudioController.pause()
            }
        })
    }

    @Synchronized
    fun resume() {
        EncodeCastUtils.processNotUI(object : EncodeCastUtils.INotUIProcessor {
            override fun process() {
                mAudioController.resume()
            }
        })
    }

    fun mute(mute: Boolean) {
        mAudioController.mute(mute)
    }

    fun getSessionId(): Int {
        return mAudioController.sessionId
    }

    override fun onAudioEncode(bb: ByteBuffer, bi: MediaCodec.BufferInfo) {
        mPacker?.onAudioAACData(bb, bi)
    }

    override fun onAudioOutformat(outputFormat: MediaFormat?) {

    }
}