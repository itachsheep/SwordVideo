/**
 * @ClassName:      FFmpegMuxerActivity
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2022/3/2 8:47 上午
 */
package com.tao.ffmpeg.mp4

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tao.ffmpeg.LogUtils
import com.tao.ffmpeg.NativeMuxer
import com.tao.ffmpeg.R
import java.io.File

class FFmpegMuxerActivity: AppCompatActivity() {

    val tag = "FFmpegMuxerActivity"
    lateinit var nativeMuxer: NativeMuxer
    lateinit var outPath: String
    lateinit var inH264Path: String
    lateinit var inAAcPath: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ffmpeg_mp4)

        val dir = getExternalFilesDir(null)?.absolutePath
        outPath = "$dir/ffmpeg_muxer.mp4"

        val outfile = File(outPath)
        if(outfile.exists()) {
            outfile.delete()
            outfile.createNewFile()
        }

        inH264Path = "$dir/a128x128.h264"
        inAAcPath = "$dir/ffmpeg_aac_441.aac"
        if(!File(inAAcPath).exists() || !File(inH264Path).exists()) {
            Toast.makeText(this,"输入文件不存在",Toast.LENGTH_SHORT).show()
            return
        }
        LogUtils.d(tag,"onCreate --ok")
        nativeMuxer = NativeMuxer()
    }

    fun startMp4Encode(view: View) {
        Thread {
            nativeMuxer.aac_h264_muxerToMp4(inH264Path,
                inAAcPath,
                outPath)
        }.start()
    }

    fun stopMp4Encode(view: View) {

    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.d(tag,"onDestroy")
    }
}