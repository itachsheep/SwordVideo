/**
 * @ClassName:      AudioMediaCodecActivity.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/7/22 4:02 PM
 */
package com.tao.aac_h264_encode_decode.mediacodec

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.tao.aac_h264_encode_decode.R
import com.tao.common.callback.OnAudioDecodeListener
import java.io.FileOutputStream

class AudioMediaCodecActivity: AppCompatActivity(),IMediaCodecListener,
    OnAudioDecodeListener{

    lateinit var btMediaCodecEncode: Button
    lateinit var btMediaCodecDecode: Button

    private var mStreamController: AudioStreamController? = null
    private var mFileOutputStream: FileOutputStream? = null
    private var mFileOutputStream_PCM: FileOutputStream? = null
    private var mDecode: AACDecoder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_codec_encode_decode)
        btMediaCodecEncode = findViewById(R.id.bt_media_codec_encode)
        btMediaCodecDecode = findViewById(R.id.bt_media_codec_decode)
    }
}