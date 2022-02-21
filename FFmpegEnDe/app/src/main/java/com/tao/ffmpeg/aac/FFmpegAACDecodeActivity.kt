/**
 * @ClassName:      FFmpegAACDecodeActivity.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/18/22 8:23 AM
 */
package com.tao.ffmpeg.aac

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tao.ffmpeg.FFmpegAacNative
import com.tao.ffmpeg.LogUtils
import com.tao.ffmpeg.R
import java.io.File

class FFmpegAACDecodeActivity :AppCompatActivity(){
    val tag = "FFmpegAACDecode"
    lateinit var fFmpegAacNative: FFmpegAacNative

    lateinit var mInPath: String
    lateinit var mOutPath: String

    @Volatile
    private var isInit = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ffmpeg_aac_decoder)
        init()
    }

    private fun init() {
        //初始化音频采集
        fFmpegAacNative = FFmpegAacNative()

        ///storage/emulated/0/Android/data/com.tao.ffmpeg/files
        val dir = getExternalFilesDir(null)?.absolutePath

        //1, aac -> pcm
        mInPath = "$dir/ffmpeg_aac_441.aac"
        mOutPath = "$dir/ffmpeg_aac2pcm.pcm"


        LogUtils.d(tag, "init mInPath =  $mInPath")
        LogUtils.d(tag, "init mOutPath =  $mOutPath")

        val file1 = File(mInPath)
        if(!file1.exists()) {
            Toast.makeText(this,
                "请把要解码文件放到指定目录",Toast.LENGTH_SHORT).show()
        }

        val file2 = File(mOutPath)
        if(file2.exists()) {
            file2.delete()
        }
    }

    /**
     * play pcm:
     *  ffplay -ar 44100 -channels 2 -f s16le -i ffmpeg_aac_decode_441.pcm
     */
    fun startDecode(view: View) {
        isInit = fFmpegAacNative.init(mInPath,mOutPath)
    }

    fun stopDecode(view: View) {
        fFmpegAacNative.release()
    }

    fun bt_test_code(view: View) {
        fFmpegAacNative.test()
    }

    override fun onDestroy() {
        super.onDestroy()
        fFmpegAacNative.release()
    }
}