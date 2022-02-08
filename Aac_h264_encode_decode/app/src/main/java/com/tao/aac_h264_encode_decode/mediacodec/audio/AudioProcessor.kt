/**
 * @ClassName:      AudioProcessor.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/7/22 8:24 PM
 */
package com.tao.aac_h264_encode_decode.mediacodec.audio

import android.media.AudioRecord
import android.media.MediaFormat
import com.tao.common.audio.AudioUtils
import com.tao.common.callback.OnAudioEncodeListener
import com.tao.common.config.AudioConfiguration
import com.tao.common.mediacodec.AACEncoder
import java.util.*

class AudioProcessor(
        private val mAudioRecord: AudioRecord,
        audioConfiguration: AudioConfiguration): Thread()  {

    @Volatile
    private var mPauseFlag: Boolean = false
    @Volatile
    private var mStopFlag: Boolean = false
    @Volatile
    private var mMute: Boolean = false
    private var mAudioEncoder: AACEncoder? = null
    private val mRecordBuffer: ByteArray
    private val mRecordBufferSize: Int

    /**
     * 初始化
     */
    init {
        mRecordBufferSize = AudioUtils.getMinBufferSize(audioConfiguration!!.frequency, audioConfiguration.channelCount)
        mRecordBuffer = ByteArray(mRecordBufferSize)
        mAudioEncoder = AACEncoder(audioConfiguration)
        mAudioEncoder!!.prepareCoder()
    }

    /**
     * 设置音频硬编码监听
     */
    fun setAudioHEncodeListener(listener: OnAudioEncodeListener?) {
        mAudioEncoder!!.setOnAudioEncodeListener(listener)
    }

    /**
     * 停止
     */
    fun stopEncode() {
        mStopFlag = true
        if (mAudioEncoder != null) {
            mAudioEncoder!!.stop()
            mAudioEncoder = null
        }
    }

    /**
     * 暂停
     */
    fun pauseEncode(pause: Boolean) {
        mPauseFlag = pause
    }

    /**
     * 静音
     */
    fun setMute(mute: Boolean) {
        mMute = mute
    }

    fun getOutputFormat(): MediaFormat?=mAudioEncoder?.getOutputFormat()

    override fun run() {
        while (!mStopFlag) {
            while (mPauseFlag) {
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
            val readLen = mAudioRecord?.read(mRecordBuffer, 0, mRecordBufferSize)
            if (readLen!! > 0) {
                if (mMute) {
                    val clearM: Byte = 0
                    //内部全部是 0 bit
                    Arrays.fill(mRecordBuffer, clearM)
                }
                mAudioEncoder?.enqueueCodec(mRecordBuffer)
            }
        }
    }
}