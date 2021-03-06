package com.tao.aac_h264_encode_decode

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.tao.aac_h264_encode_decode.mediacodec.audio.AudioMediaCodecActivity
import com.tao.aac_h264_encode_decode.mediacodec.video.H264MediaCodecEDActivity

class MainActivity : AppCompatActivity() {
    private val REQUEST_OK = 10001
    private val tag = "MainActivity"

    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("aac_h264")
        }
    }

    private val request_permission = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        LogUtils.d(tag, "onCreate checkPermission")
        if (!checkPermission(request_permission)) {
            ActivityCompat.requestPermissions(
                this, request_permission,REQUEST_OK
            )
        }

    }

    fun bt_aac_decode(view: View) {
        startActivity(Intent(this, AacDecodeActivity::class.java))
    }

    fun bt_aac_encode(view: View) {
        startActivity(Intent(this, AacEncodeActivity::class.java))
    }

    fun bt_h264_encode(view: View) {
        startActivity(Intent(this,X264EncodeActivity::class.java))
    }

    fun bt_media_codec_encode_decode(view: View) {
        startActivity(Intent(this, AudioMediaCodecActivity::class.java))
    }

    fun bt_media_codec_video(view: View) {
        startActivity(Intent(this,H264MediaCodecEDActivity::class.java))
    }


    fun checkPermission(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission!!)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        LogUtils.d(tag, "checkPermission ok")
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        LogUtils.d(
            tag,
            "onRequestPermissionsResult: $requestCode"
        )
        if (requestCode != REQUEST_OK) {
            Toast.makeText(this, "??????????????????", Toast.LENGTH_LONG).show()
        }
    }

    external fun stringFromJNI(): String
    fun bt_basic_learn(view: View) {

        /*val str = "hello";
        var byteArray = str.toByteArray(Charsets.UTF_8);
        val modifyByteArray = AacManager.modifyByteArray(byteArray)
        LogUtils.d(tag,"bt_basic_learn modifyByteArray =" +
                String(modifyByteArray))*/

        AacManager.test_memcpy();
    }


}