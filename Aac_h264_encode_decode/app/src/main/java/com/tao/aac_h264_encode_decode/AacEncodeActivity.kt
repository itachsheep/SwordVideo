/**
 * @ClassName:      AacEncodeActivity.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/4/22 2:28 PM
 */
package com.tao.aac_h264_encode_decode

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.tao.common.audio.AudioCapture
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class AacEncodeActivity: AppCompatActivity() {
    val tag = "AacEncodeActivity"
    /**
     * 编码的采样率
     */
    private var mSampleRate = 44100;

    /**
     * 编码的通道
     */
    private var mChannels = 1;

    /**
     * 码率 bitrate/s
     */
    private var mBitRate = 128000;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aac_encode)
        //初始化音频采集
        AudioCapture.init()
        /**
         * 录音pcm文件保存起来
         * ///storage/emulated/0/Android/data/com.tao.aac_h264_encode_decode/files
         */
        val outFile = File(getExternalFilesDir(null),"my_record_tmp.pcm")
        val outputStream = FileOutputStream(outFile)
//        var byteArray = ByteArray(1024)
        LogUtils.d(tag,"onCreate outFile = ${outFile.absolutePath}")
        AudioCapture.addRecordListener(object : AudioCapture.OnRecordListener{
            override fun onStart(sampleRate: Int, channels: Int, sampleFormat: Int) {
                LogUtils.d(tag,"onStart")
                AacManager.enc_init(mBitRate,mChannels,mSampleRate)
            }

            override fun onStop() {
                LogUtils.d(tag,"onStop")
                AacManager.enc_destroy()

                try {
                    outputStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun onData(byteArray: ByteArray) {
                LogUtils.d(tag, "onData size = " + byteArray.size.toString())
                AacManager.encode(byteArray,byteArray.size)
                outputStream.write(byteArray)
            }
        })
    }

    /**
     * 编码路径: /sdcard/avsample/fdkaac_encode.aac
     * 没有需要创建一个目录
     */
    fun startEncode(view: View) {
        AudioCapture.startRecording()
    }

    fun stopEncode(view: View) {
        AudioCapture.stopRecording()

    }
}