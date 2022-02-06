/**
 * @ClassName:      X264EncodeActivity.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/5/22 3:11 PM
 */
package com.tao.aac_h264_encode_decode

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.lang.Exception

class X264EncodeActivity : AppCompatActivity(), View.OnClickListener {

    val tag = "X264EncodeActivity"
    lateinit var btX264Encode: Button
    lateinit var btX264Stop: Button
    lateinit var mH264PATH: String


    /**
     * 视频宽
     */
    private val mWidth = 176
    /**
     * 视频高
     */
    private val mHeight = 144
    /**
     * YUV类型
     * YUV420sp == nv21
     * YUV420p == i420
     */
    private val mYUVType = YUVType.YUV420sp.type
    //todo: yuv视频码率如何算？？？？
    private val mVideoRate: Int = 120;//2000;//(176 * 144 * (1 + 0.5f) * 25).toInt()
    private val mFrameRate = 25;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_x264_encode)
        btX264Encode = findViewById(R.id.bt_start_x264_encode)
        btX264Stop = findViewById(R.id.bt_stop_x264_encode)

        btX264Encode.setOnClickListener(this)
        btX264Stop.setOnClickListener(this)
        mH264PATH =  getExternalFilesDir(null)?.absolutePath + "/record_176_144.h264"

        LogUtils.d(tag,"mH264PATH = $mH264PATH" )

        H264Manager.enc_init(mH264PATH,mWidth,mHeight,mVideoRate,mFrameRate)
    }



    override fun onClick(v: View) {
        when(v.id) {
            R.id.bt_stop_x264_encode -> doStopEncode()
            R.id.bt_start_x264_encode -> doEncode()
        }

    }

    private fun doStopEncode() {
        H264Manager.enc_destroy()
    }

    private fun doEncode() {
        Thread(){
            val ins = resources.openRawResource(R.raw.raw_176_144)
            var byteArray = ByteArray(mWidth * mHeight * 3 / 2)
            do {
                val len = ins.read(byteArray)
                if(len > 0) {
                    H264Manager.enc_encode(byteArray,mYUVType)
                }
            } while (len > 0)
            H264Manager.enc_destroy()
            try {
                ins.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }
}