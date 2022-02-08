/**
 * @ClassName:      H264MediaCodecEDActivity.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/8/22 7:31 PM
 */
package com.tao.aac_h264_encode_decode.mediacodec.video

import android.media.MediaCodec
import android.media.MediaFormat
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.tao.aac_h264_encode_decode.R
import com.tao.common.callback.OnVideoEncodeListener
import com.tao.common.config.VideoConfiguration
import java.nio.ByteBuffer

class H264MediaCodecEDActivity:AppCompatActivity(), SurfaceHolder.Callback,
    OnVideoEncodeListener, View.OnClickListener {
    private var mH264Encoder: WriteH264? = null
    private var mH264Decoder: H264Decoder? = null
    private var mH264Buffer: ByteArray? = null

    lateinit var btMediaCodecEncodeDecode: Button
    lateinit var btMediaCodecStop: Button
    lateinit var surfaceView: SurfaceView

    lateinit var H264_OUTPUT: String
    lateinit var H264_DIR: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_codec_video)
        btMediaCodecEncodeDecode = findViewById(R.id.bt_media_codec_encode_decode)
        btMediaCodecStop = findViewById(R.id.bt_media_codec_stop)
        surfaceView = findViewById(R.id.surface)

        btMediaCodecEncodeDecode.setOnClickListener(this)
        btMediaCodecStop.setOnClickListener(this)

        H264_DIR = getExternalFilesDir(null)?.absolutePath.toString()
        H264_OUTPUT = "${H264_DIR}/mediacodec_video.h264"
        init()
    }

    private fun init(){
        mH264Encoder = WriteH264(H264_OUTPUT)
        mH264Decoder = H264Decoder()
        surfaceView.getHolder().addCallback(this)
        mH264Encoder?.prepare(applicationContext, VideoConfiguration.createDefault())
        mH264Encoder?.setOnEncodeListener(this)
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.bt_media_codec_encode_decode -> startEncodeDecode()
            R.id.bt_media_codec_stop -> stopEncodeDecode()
        }
    }

    private fun startEncodeDecode() {
        if (mH264Encoder == null) {
            init()
            mH264Decoder?.start()
        }
        //startTime(timer)
        mH264Encoder?.start()
    }

    private fun stopEncodeDecode() {
        //cleanTime(timer)
        mH264Encoder?.stop()
        mH264Decoder?.stop()
        mH264Encoder = null
        mH264Decoder = null
    }

    /**
     *
     * 编码完成的回调，
     * 注意：内部有写入文件的操作
     */
    override fun onVideoEncode(data: ByteBuffer?, info: MediaCodec.BufferInfo?) {

        if (data == null || info == null)return

        if (mH264Buffer == null || mH264Buffer?.size!! < info.size) {
            mH264Buffer = ByteArray(info.size)
        }
        data.position(info.offset)
        data.limit(info.offset + info.size)
        data.get(mH264Buffer, 0, info.size)

        if (info.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG ==
            MediaCodec.BUFFER_FLAG_CODEC_CONFIG) {//SPS,PPS
            // this is the first and only config sample, which contains information about codec
            // like H.264, that let's configure the decoder
            mH264Decoder?.configure(
                VideoConfiguration.Builder()
                    .setSurface(surfaceView.holder.surface)
                    .setSpsPpsBuffer(ByteBuffer.wrap(mH264Buffer, 0, info.size))
                    .setCodeType(VideoConfiguration.ICODEC.DECODE)
                    .build()
            )
        } else {
            //todo： 编码完成后，解码，但是并没有输出到某个文件？？？
            // pass byte[] to decoder's queue to render asap
            mH264Decoder?.enqueue(
                mH264Buffer!!,
                info.presentationTimeUs,
                info.flags
            )
        }
    }


    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        mH264Decoder?.start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mH264Decoder?.stop()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
    }

    override fun onVideoOutformat(outputFormat: MediaFormat?) {

    }

    override fun onDestroy() {
        super.onDestroy()
        mH264Decoder?.stop()
        mH264Encoder?.stop()
        mH264Encoder = null
        mH264Decoder = null
    }


}