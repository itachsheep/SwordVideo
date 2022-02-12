package com.tao.common.audio

import android.media.AudioFormat
import android.media.MediaRecorder
import com.tao.common.audio.AudioUtils
import com.tao.common.audio.AudioUtils.AUDIO_CHANNEL_CONFIG
import com.tao.common.audio.AudioUtils.AUDIO_FROMAT
import com.tao.common.audio.AudioUtils.SAMPLE_RATE_IN_HZ
import com.tao.common.LogHelper

/**
 * <pre>
 *     author  : devyk on 2020-05-28 20:07
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AudioCapture
 * </pre>
 */

object AudioCapture : Runnable {

    private var TAG = javaClass.simpleName

    /**
     * 是否开始录制
     */
    @Volatile
    private var isRecording = false;

    /**
     * 读取大小
     */
    private var mReadSize = 1024;

    /**
     * 录制监听
     */
    private var mRecordListener: OnRecordListener? = null


    /**
     * 线程
     */
    private var mThread: Thread? = null


    public fun init(
        audioSource: Int = MediaRecorder.AudioSource.MIC,
        sampleRateInHz: Int = 44100,
        channelConfig: Int = AudioFormat.CHANNEL_IN_MONO,
        audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT
    ) {
        if (AudioUtils.initAudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat)) {
            mReadSize = getBufferSize()
        }
    }


    fun getBufferSize(): Int = AudioUtils.getMinBufferSize()

    /**
     * 开始录制
     */
    fun startRecording() {
        if (isRecording()) return
        isRecording = true
        mThread = Thread(this);
        mThread!!.start()
        AudioUtils.startRecord()
        mRecordListener?.onStart()
    }

    fun stopRecording() {
        if (!isRecording()) return
        isRecording = false
        AudioUtils.stopRecord()
        AudioUtils.releaseRecord()
        mRecordListener?.onStop()
    }

    /**
     * 是否正在录制
     */
    public fun isRecording(): Boolean = isRecording;

    /**
     * 读取音频 PCM 数据
     */
    override fun run() {
        var byteArray = ByteArray(mReadSize);
        while (isRecording) {
            try {
                if (AudioUtils.read(mReadSize, byteArray)!! > 0) {
                    mRecordListener?.onData(byteArray)
                }
            } catch (error: InterruptedException) {
                LogHelper.e(TAG, error.message!!)
            }
        }
    }


    public fun addRecordListener(listener: OnRecordListener) {
        mRecordListener = listener
    }

    public interface OnRecordListener {
        fun onStart(
            sampleRate: Int = SAMPLE_RATE_IN_HZ,
            channels: Int = AUDIO_CHANNEL_CONFIG,
            sampleFormat: Int = AUDIO_FROMAT
        );

        fun onStop();
        fun onData(byteArray: ByteArray);
    }


}