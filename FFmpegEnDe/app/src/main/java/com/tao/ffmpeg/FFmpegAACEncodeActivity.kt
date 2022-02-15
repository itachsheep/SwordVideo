/**
 * @ClassName:      FFmpegAACDecodeActivity.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/12/22 10:42 AM
 */
package com.tao.ffmpeg

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.tao.common.audio.AudioCapture

class FFmpegAACEncodeActivity:AppCompatActivity() {
    val tag = "FFmpegAACDecodeActivity"

    private lateinit var fFmpegAacNative: FFmpegAacNative
    private lateinit var mOutAAcPath:String

    @Volatile
    private var isInit = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ffmpeg_aac_decoder)
        mOutAAcPath = getExternalFilesDir(null)?.absolutePath +
                "/ffmpeg_aac_441.aac"

        LogUtils.d(tag,"onCreate mOutAAcPath = $mOutAAcPath")
        init()
    }

    fun startDecode(view: View) {
        AudioCapture.startRecording()
    }

    fun stopDecode(view: View) {
        AudioCapture.stopRecording()
    }

    private fun init(){
        AudioCapture.init()
        fFmpegAacNative = FFmpegAacNative()
        AudioCapture.addRecordListener(object : AudioCapture.OnRecordListener {
            override fun onStart(sampleRate: Int, channels: Int, sampleFormat: Int) {
                isInit = fFmpegAacNative.init(mOutAAcPath,64*1000, 1, 44100)
                LogUtils.d(tag,"onStart isInit = $isInit")
            }

            override fun onStop() {
                fFmpegAacNative.release()
            }

            override fun onData(byteArray: ByteArray) {
                if(isInit == 0) {
                    AudioCapture.stopRecording()
                    onStop()
                    isInit = -1
                    return
                }

                if(isInit == 1) {
                    fFmpegAacNative.encode(byteArray)
                }
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        AudioCapture.stopRecording()
        fFmpegAacNative.release()

    }
}