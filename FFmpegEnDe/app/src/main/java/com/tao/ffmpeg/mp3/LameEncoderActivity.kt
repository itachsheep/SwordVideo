/**
 * @ClassName:      LameEncoderActivity.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/25/22 12:34 PM
 */
package com.tao.ffmpeg.mp3

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.tao.common.audio.AudioCapture
import com.tao.ffmpeg.LameMp3Native
import com.tao.ffmpeg.LogUtils
import com.tao.ffmpeg.R
import java.io.File

class LameEncoderActivity:AppCompatActivity() {

    val tag = "LameEncoderActivity"
    private lateinit var outMp3Path:String
    private var isInit = 0
    private lateinit var lameMp3Native: LameMp3Native
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lame_mp3_encoder)
        val dir = getExternalFilesDir(null)?.absolutePath
        outMp3Path = "$dir/lame_encode_pcm_2_mp3.mp3"
        LogUtils.d(tag,"onCreate = $outMp3Path ")

        val outFile = File(outMp3Path)
        if(outFile.exists()) {
            outFile.delete()
        }

        initListener()
    }

    private fun initListener() {
        lameMp3Native = LameMp3Native()


        AudioCapture.addRecordListener(object :AudioCapture.OnRecordListener{
            override fun onStart(sampleRate: Int, channels: Int, sampleFormat: Int) {
                isInit = lameMp3Native.init(outMp3Path,44100,1,128)
            }

            override fun onStop() {
                lameMp3Native.release()
            }

            override fun onData(byteArray: ByteArray) {
                if(isInit == 1) {
                    lameMp3Native.encode(byteArray)
                }
            }

        })
    }

    fun startLameEncode(view:View) {
        LogUtils.d(tag,"startLameEncode")
        AudioCapture.init()
        AudioCapture.startRecording()
    }

    fun stopLameEncode(view: View) {
        LogUtils.d(tag,"stopLameEncode")
        AudioCapture.stopRecording()
    }

}