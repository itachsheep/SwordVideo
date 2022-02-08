package com.tao.aac_h264_encode_decode.mediacodec.audio

import android.os.Build
import android.os.Looper

object EncodeCastUtils {
    val isOverLOLLIPOP: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

    interface INotUIProcessor {
        fun process()
    }


    fun processNotUI(processor: INotUIProcessor) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Thread(Runnable { processor.process() }).start()
        } else {
            processor.process()
        }
    }
}