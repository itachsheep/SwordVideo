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
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class AacActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aac)

    }

    fun bt_decode(view: View) {
        AacManager.initWithADTformat()
    }
}