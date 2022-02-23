/**
 * @ClassName:      FFmpegVideoEncoderActivity.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/21/22 8:56 PM
 */
package com.tao.ffmpeg.h264

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tao.ffmpeg.FFmpegH264Native
import com.tao.ffmpeg.LogUtils
import com.tao.ffmpeg.R
import java.io.File

class FFmpegVideoEncoderActivity:AppCompatActivity() {
    val tag = "FFmpegVideoEncoder"
    private lateinit var mInYuvPath: String
    private lateinit var mOutH264Path: String
    private lateinit var fFmpegH264Native: FFmpegH264Native
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ffmpeg_h264_encoder)
        val dir = getExternalFilesDir(null)?.absolutePath
        mInYuvPath = "$dir/akiyo_i420_176_144.yuv"
        mOutH264Path = "$dir/ffmpeg_out_h264.h264"
        LogUtils.d(tag,"onCreate mInYuvPath = $mInYuvPath")

        val inFile = File(mInYuvPath)
        if(!inFile.exists()) {
            Toast.makeText(this,"输入yuv文件不存在",Toast.LENGTH_SHORT).show()
            return
        }

        val outFile = File(mOutH264Path)
        if(outFile.exists()){
            outFile.delete();
        }

        fFmpegH264Native = FFmpegH264Native()
    }

    fun startEncode(view: View) {

        val success = fFmpegH264Native.initEncode(mInYuvPath,mOutH264Path,
            176,144,15,200);
        if(success == 1) {
            fFmpegH264Native.start()
        }
    }


    fun stopEncode(view: View) {
        if(fFmpegH264Native != null) {
            fFmpegH264Native.release()
        }
    }

}