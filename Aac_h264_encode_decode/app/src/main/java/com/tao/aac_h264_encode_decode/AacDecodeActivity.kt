/**
 * @ClassName:      AacActivity.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2022/1/24 8:47 AM
 */
package com.tao.aac_h264_encode_decode

import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream

class AacDecodeActivity : AppCompatActivity() {
    val tag = "AacActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aac_decode)

        ///data/user/0/com.tao.aac_h264_encode_decode/files
        LogUtils.d(tag, "onCreate filesDir = " + filesDir)
        ///storage/emulated/0/Android/data/com.tao.aac_h264_encode_decode/files
        LogUtils.d(tag, "onCreate filesDir = " + getExternalFilesDir(null))
        ///storage/emulated/0
        LogUtils.d(tag, "onCreate filesDir = " + Environment.getExternalStorageDirectory())

        val path  = Environment.getExternalStorageDirectory().absolutePath + "/111/vocal.pcm"
        val file = File(path)
        LogUtils.d(tag,"path = $path, file exist : " + file.exists())
    }

    fun bt_decode(view: View) {
        AacManager.initWithADTformat()
        val inputStream = resources.openRawResource(R.raw.fdkaac_encode)
        val outFile = File(getExternalFilesDir(null),"aac_decode.pcm")
        LogUtils.d(tag,"decode outFile = $outFile")
        val outputStream = FileOutputStream(outFile)
        var byteArray = ByteArray(1024)
        Thread(){
            do {
                var len = -1
                len = inputStream.read(byteArray,0,byteArray.size)
                val decodeArray = AacManager.decode(byteArray, len)

                if(len > 0 && decodeArray != null) {
                    //outputStream.write(byteArray,0,len)
                    LogUtils.d(tag,"decode ---> len = $len" +
                            ", decode len = ${decodeArray?.size}" )
                    outputStream.write(decodeArray)
                }
            } while (len > 0)
        }.start()

    }
}