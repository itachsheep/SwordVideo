package com.tw.speedbrowser.base.utils

import android.util.Log

object LogUtils {
    private val TAG = "SpeedBs."

    fun d(tag: String, msg: String) {
        Log.d(TAG.plus(tag),msg)
    }

    fun e(tag: String, msg: String) {
        Log.e(TAG.plus(tag),msg)
    }
}