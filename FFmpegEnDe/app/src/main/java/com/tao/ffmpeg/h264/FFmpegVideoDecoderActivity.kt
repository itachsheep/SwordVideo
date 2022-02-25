/**
 * @ClassName:
 * @Description:
 * @author taowei
 * @version V1.0
 * @Date
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

public class FFmpegVideoDecoderActivity: AppCompatActivity(){
    private val tag = "FFmpegVideoDecoder"
    private lateinit var mInYuvPath: String
    private lateinit var mOutH264Path: String
    private lateinit var fFmpegH264Native: FFmpegH264Native

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ffmpeg_h264_decode)

        val dir = getExternalFilesDir(null)?.absolutePath
        mInYuvPath = "$dir/a128x128.h264"
        mOutH264Path =  "$dir/ffmpeg_out_128_128.yuv"
        LogUtils.d(tag,"onCreate mInYuvPath = $mInYuvPath")

        val inFile = File(mInYuvPath)
        if(!inFile.exists()) {
            Toast.makeText(this,"输入yuv文件不存在", Toast.LENGTH_SHORT).show()
            return
        }

        val outFile = File(mOutH264Path)
        if(outFile.exists()){
            outFile.delete();
        }

        fFmpegH264Native = FFmpegH264Native()
    }

    fun startH264Decode(view: View) {
        val success = fFmpegH264Native.initDecode(mInYuvPath,mOutH264Path,
//                720, 1280, 1000)
                128, 128, 200)
        if(success == 1) {
            fFmpegH264Native.start()
        }
    }

    fun stopH264Decode(view: View){
        fFmpegH264Native.release()
    }

}