package com.tw.speedbrowser.base.utils

import android.content.Context
import android.view.View

object RtlUtils {

    //是否是RTl
    fun isRtl(context: Context): Boolean {
        return context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL;
    }

}