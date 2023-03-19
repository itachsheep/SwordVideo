package com.tw.speedbrowser.base.utils

import android.content.Context
import androidx.core.content.ContextCompat

class ColorSetting {

    object Setting {
//        var klineColorMode: Int = BaseCoinConstant.ColorType.GREEN_UP_RED_DOWN
    }

    object ColorSetting {

        /**
         * id: colorInt的参数
         */
        fun getTextColor(context: Context, id: Int): Int {
//            val textInternal = ResUtils.getResource(context).getResourceEntryName(id)
//            val isContainsGreenColor = textInternal.contains(BaseKtConstance.ColorName.COLOR_OF_STYLE_GREEN, true)
//            val isContainsRedColor = textInternal.contains(BaseKtConstance.ColorName.COLOR_OF_STYLE_RED, true)
            return ContextCompat.getColor(context, id)

            /**
             * originColorRes:默认设置的颜色，
             * changeColorRes：需要替换的颜色
             */
//        fun getTextColorRes(originColorRes: Int, changeColorRes: Int = 0): Int {
//
//            return if (changeColorRes != 0) {
//                if (SlTextColorSetting.Setting.klineColorMode == SlTextColorSetting.GREEN_UP_RED_DOWN) {
//                    originColorRes
//                } else {
//                    changeColorRes
//                }
//            } else {
//                originColorRes
//            }
//        }
        }

    }


    object DrawableSetting {

        fun getDrawableRes(originDrawable: Int, changeDrawable: Int = 0): Int {

            return originDrawable
        }

    }


}