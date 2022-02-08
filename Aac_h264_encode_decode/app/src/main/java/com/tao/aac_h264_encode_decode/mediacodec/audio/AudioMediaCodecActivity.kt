/**
 * @ClassName:      AudioMediaCodecActivity.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/7/22 4:02 PM
 */
package com.tao.aac_h264_encode_decode.mediacodec.audio

import android.media.MediaCodec
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.tao.aac_h264_encode_decode.LogUtils
import com.tao.aac_h264_encode_decode.R
import com.tao.common.callback.OnAudioDecodeListener
import com.tao.common.config.AudioConfiguration
import com.tao.common.mediacodec.AACDecoder
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

class AudioMediaCodecActivity: AppCompatActivity(), IMediaCodecListener,
    OnAudioDecodeListener, View.OnClickListener {
    val tag = "AudioMediaCodecActivity"
    lateinit var btMediaCodecEncodeDecode: Button
    lateinit var btMediaCodecStop: Button

    private var mStreamController: AudioStreamController? = null
    private var mFileOutputStream: FileOutputStream? = null
    private var mFileOutputStream_PCM: FileOutputStream? = null
    private var mDecode: AACDecoder? = null

    private lateinit var AAC_DIR: String
    private lateinit var AAC_PATH : String
    private lateinit var PCM_PATH : String
    private var mAudio: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_codec_encode_decode)

        AAC_DIR = getExternalFilesDir(null)?.absolutePath.toString()
        AAC_PATH = "${AAC_DIR}/mediacodec_44100_1_16_bit.aac"
        PCM_PATH = "${AAC_DIR}/mediacodec_44100_1_16_bit.pcm"

        btMediaCodecEncodeDecode = findViewById(R.id.bt_media_codec_encode_decode)
        btMediaCodecStop = findViewById(R.id.bt_media_codec_stop)

        btMediaCodecEncodeDecode.setOnClickListener(this)
        btMediaCodecStop.setOnClickListener(this)

        LogUtils.d(tag,"onCreate AAC_DIR = $AAC_DIR " +
                ", AAC_PATH = $AAC_PATH" )
        init()
        initData()
    }
    private fun init(){
        mStreamController = AudioStreamController(AudioControllerImpl());
        mStreamController?.setListener(this)
        if (!File(AAC_DIR).exists()) {
            File(AAC_DIR).mkdir()
        }

        if (File(AAC_PATH).exists()) {
            File(AAC_PATH).delete()
        }
        File(AAC_PATH).createNewFile()


        if (File(PCM_PATH).exists()) {
            File(PCM_PATH).delete()
        }
        File(PCM_PATH).createNewFile()
        mFileOutputStream = FileOutputStream(AAC_PATH, true)
        mFileOutputStream_PCM = FileOutputStream(PCM_PATH, true)
    }

    private fun initData() {
        var audioConfiguration = AudioConfiguration.Builder()
                .setCodecType(AudioConfiguration.CodeType.DECODE)
                .setAdts(1)//有 ADTS 数据
                .build()
        mDecode = AACDecoder(audioConfiguration)
        mDecode?.prepareCoder()
        mDecode?.setOnAudioEncodeListener(this)
    }

    override fun start() {
    }

    override fun stop() {
    }

    /**
     * 将 AAC 解码成 PCM ，解码完成的回调
     */
    override fun onAudioPCMData(bb: ByteBuffer, bi: MediaCodec.BufferInfo) {
        LogUtils.d(tag,"onAudio ---- PCM Data")
        var mAudio = ByteArray(bi.size)
        bb.position(bi.offset)
        bb.limit(bi.offset + bi.size)
        bb.get(mAudio)
        mFileOutputStream_PCM?.write(mAudio)
    }

    /**
     * 将 PCM 编码成 AAC ，编码完成的回调
     */
    override fun onAudioAACData(bb: ByteBuffer, bi: MediaCodec.BufferInfo) {
        super.onAudioAACData(bb, bi)
        LogUtils.d(tag,"onAudio --- AAC Data")
        mAudio = ByteArray(bi.size + 7)
        //添加 adts 头
        bb.position(bi.offset)
        bb.limit(bi.offset + bi.size)
        bb.get(mAudio, 7, bi.size)
        ADTSUtils.addADTStoPacket(mAudio!!, mAudio!!.size, 2, 44100, 1)
        //打印 ADTS
        LogUtils.d(tag,
                "mAudio[0]=${mAudio!![0]} mAudio[1]=${mAudio!![1]} mAudio[2]=${mAudio!![2]} mAudio[3]=${mAudio!![3]} mAudio[4]=${mAudio!![4]} mAudio[5]=${mAudio!![5]} mAudio[6]=${mAudio!![6]}"
        )
        /**
         *  aac编码完成后，再继续解码，回调到 onAudioPCMData()方法
         */
        mDecode?.enqueueCodec(mAudio!!)
        mFileOutputStream?.write(mAudio, 0, mAudio!!.size)
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.bt_media_codec_encode_decode -> startEncodeDecode()
            R.id.bt_media_codec_stop -> stopEncodeDecode()
        }
    }

    private fun startEncodeDecode() {
        init()
        mStreamController?.start()
    }

    private fun stopEncodeDecode() {
        //内部是控制编码的
        mStreamController?.stop()
        mDecode?.stop()
        //mFileOutputStream?.close()
        //mFileOutputStream_PCM?.close()
    }
}