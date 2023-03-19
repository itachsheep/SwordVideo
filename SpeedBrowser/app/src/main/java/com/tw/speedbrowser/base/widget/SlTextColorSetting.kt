package com.tw.speedbrowser.base.widget

import android.content.Context
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat


//这个类copyColorSetting
class SlTextColorSetting {


    companion object {
        //1(绿涨红跌)，2(红涨绿跌)
        const val GREEN_UP_RED_DOWN = 1
        const val RED_UP_GREEN_DWON = 2
    }

    object Setting {
        var klineColorMode: Int = GREEN_UP_RED_DOWN
    }

    object ColorSetting {

        @ColorInt
        fun getTextColor(context: Context, id: Int): Int {
            return id
        }

        @ColorInt
        fun getNoChangeColor(context: Context, id: Int, currentTextColor: Int): Int {
            //设置了normaltextcolor
            if (id != 0 && id != currentTextColor) {
                return getTextColor(context, id)
            }

           return if (currentTextColor == 0  || currentTextColor == -1) {//没设置textcolor
               getTextColor(context, id)
           }else {//设置了normaltextcolor
               currentTextColor
           }
            
        }
    }
}