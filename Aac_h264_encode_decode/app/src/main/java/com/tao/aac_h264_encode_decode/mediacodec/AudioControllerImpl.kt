/**
 * @ClassName:      AudioControllerImpl.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/7/22 8:23 PM
 */
package com.tao.aac_h264_encode_decode.mediacodec

import android.annotation.TargetApi
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import com.tao.common.audio.AudioUtils
import com.tao.common.callback.OnAudioEncodeListener
import com.tao.common.config.AudioConfiguration

class AudioControllerImpl: IAudioController  {
    private var mListener: OnAudioEncodeListener? = null
    private var mAudioRecord: AudioRecord? = null
    private var mAudioProcessor: AudioProcessor? = null
    private var mMute: Boolean = false
    private var mAudioConfiguration: AudioConfiguration? = null
    private var TAG = this.javaClass.simpleName

    override val sessionId: Int
        @TargetApi(16)
        get() = if (mAudioRecord != null) {
            mAudioRecord!!.audioSessionId
        } else {
            -1
        }

    init {
        mAudioConfiguration = AudioConfiguration.createDefault()
    }

    override fun setAudioConfiguration(audioConfiguration: AudioConfiguration) {
        mAudioConfiguration = audioConfiguration
    }

    override fun setAudioEncodeListener(listener: OnAudioEncodeListener?) {
        mListener = listener
    }

    override fun start() {
        Log.d(TAG, "Audio Recording start")
        AudioUtils.initAudioRecord(MediaRecorder.AudioSource.MIC,mAudioConfiguration!!.frequency,mAudioConfiguration!!.channelCount,mAudioConfiguration!!.encoding)
        mAudioRecord = AudioUtils.getAudioRecord()
        try {
            mAudioRecord?.startRecording()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mAudioProcessor = AudioProcessor(mAudioRecord!!, mAudioConfiguration!!)
        mAudioProcessor?.setAudioHEncodeListener(mListener)
        mAudioProcessor?.start()
        mAudioProcessor?.setMute(mMute)
    }

    override fun stop() {
        Log.d(TAG, "Audio Recording stop")
        if (mAudioProcessor != null) {
            mAudioProcessor!!.stopEncode()
        }
        if (mAudioRecord != null) {
            try {
                mAudioRecord!!.stop()
                mAudioRecord!!.release()
                mAudioRecord = null
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun pause() {
        Log.d(TAG, "Audio Recording pause")
        if (mAudioRecord != null) {
            mAudioRecord!!.stop()
        }
        if (mAudioProcessor != null) {
            mAudioProcessor!!.pauseEncode(true)
        }
    }

    override fun resume() {
        Log.d(TAG, "Audio Recording resume")
        if (mAudioRecord != null) {
            mAudioRecord!!.startRecording()
        }
        if (mAudioProcessor != null) {
            mAudioProcessor!!.pauseEncode(false)
        }
    }

    override fun mute(mute: Boolean) {
        Log.d(TAG, "Audio Recording mute: $mute")
        mMute = mute
        if (mAudioProcessor != null) {
            mAudioProcessor!!.setMute(mMute)
        }
    }
}