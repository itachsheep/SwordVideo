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
import androidx.appcompat.app.AppCompatActivity

class LameEncoderActivity:AppCompatActivity() {

    private var outMp3Path = "sdcard/avsample/lame_encode_pcm_2_mp3.mp3"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
}