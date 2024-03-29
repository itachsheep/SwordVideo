package com.tao.camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.tao.camera.sample.GLSampleActivity

class MainActivity : AppCompatActivity() {
    private val REQUEST_OK = 10001
    private val tag = "MainActivity"

    private val request_permission = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        LogUtils.d(tag, "onCreate checkPermission")
        if (!checkPermission(request_permission)) {
            ActivityCompat.requestPermissions(
                this, request_permission,REQUEST_OK
            )
        } else {
            startActivity(Intent(this, GLSampleActivity::class.java))
        }
        // Example of a call to a native method
//        findViewById<TextView>(R.id.sample_text).text = stringFromJNI()
    }

    fun bt_aac_encode(view: View) {
//        startActivity(Intent(this, FFmpegAACEncodeActivity::class.java))
        startActivity(Intent(this, GLSampleActivity::class.java))
    }

    fun bt_aac_decode(view: View) {
//        startActivity(Intent(this, FFmpegAACDecodeActivity::class.java))
    }

    fun bt_h264_encode(view: View) {
//        startActivity(Intent(this,FFmpegVideoEncoderActivity::class.java))
    }

    fun bt_h264_decode(view: View) {
//        startActivity(Intent(this,FFmpegVideoDecoderActivity::class.java))
    }

    fun bt_lame_encode_mp3(view: View) {
//        startActivity(Intent(this,LameEncoderActivity::class.java))
    }

    fun bt_mp4_muxer(view: View) {
//        startActivity(Intent(this,FFmpegMuxerActivity::class.java))
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
            Toast.makeText(this, "权限申请失败", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
//    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("use_ffmpeg")
        }
    }


}