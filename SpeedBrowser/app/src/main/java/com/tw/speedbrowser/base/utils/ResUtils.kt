package com.tw.speedbrowser.base.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.widget.TextView
import androidx.annotation.*
import androidx.core.content.ContextCompat
import com.tw.speedbrowser.base.AppManager
import com.tw.speedbrowser.base.BaseApplication

/**
 * desc:获取各种资源文件
 */
object ResUtils {
    /**
     * Return the color value
     */
    @JvmStatic
    fun getDimension(@DimenRes demenId: Int): Float {
        return try {
            getResource().getDimension(demenId)
        } catch (e: Exception) {
            e.printStackTrace()
            0f
        }
    }

    /**
     * Return the color value
     */
    @JvmStatic
    fun getDimensionPixelOffset(@DimenRes demenId: Int): Int {
        return try {
            getResource().getDimensionPixelOffset(demenId)
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    /**
     * Return the colorInt
     * params: needChangeColor是否需要变颜色，默认是true，特殊地方传false，比如交易员主页的图表
     */
    @JvmStatic
    fun getColor(context: Context, @ColorRes id: Int, needChangeColor: Boolean = true): Int {
        return try {
            if (needChangeColor) {
                ColorSetting.ColorSetting.getTextColor(context, id)
            } else {
                ContextCompat.getColor(context, id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    /**
     * Return the color value
     * params: needChangeColor是否需要变颜色，默认是true，特殊地方传false，比如交易员主页的图表
     */
    @JvmStatic
    fun getColor(@ColorRes id: Int, needChangeColor: Boolean = true): Int {
        return try {
            if (needChangeColor) {
                ColorSetting.ColorSetting.getTextColor(getContext(), id)
            } else {
                ContextCompat.getColor(getContext(), id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }


    @JvmStatic
    fun getColor(@ColorRes id: Int): Int {
        return try {
            ColorSetting.ColorSetting.getTextColor(getContext(), id)
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }


    @JvmStatic
    fun getColor(context: Context, @ColorRes id: Int): Int {
        return try {
            ColorSetting.ColorSetting.getTextColor(context, id)
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }


    /**
     * Return the colorRes
     */
//    @JvmStatic
//    fun getColorRes(@ColorRes originId: Int, @ColorRes changeId: Int = 0): Int {
//        return ColorSetting.ColorSetting.getTextColorRes(originId, changeId)
//    }


    /**
     * Return the drawable
     */
    @JvmStatic
    fun getDrawable(context: Context, @DrawableRes id: Int): Drawable? {
        return ContextCompat.getDrawable(context, id)
    }

    /**
     * Return the drawable
     */
    @JvmStatic
    fun getDrawable(@DrawableRes id: Int): Drawable? {
        return ContextCompat.getDrawable(getContext(), id)
    }

    /**
     * Return the drawableRes
     */
    @DrawableRes
    @JvmStatic
    fun getDrawableRes(@DrawableRes originId: Int, @DrawableRes changeId: Int = 0): Int {
        return ColorSetting.DrawableSetting.getDrawableRes(originId, changeId)
    }

    /**
     * Return the drawable
     */
    @JvmStatic
    fun getDrawable(@DrawableRes originId: Int, @DrawableRes changeId: Int = 0): Drawable? {
        return ContextCompat.getDrawable(getContext(), getDrawableRes(originId, changeId))
    }


    @JvmStatic
    fun getDrawable(context: Context, @DrawableRes originId: Int, @DrawableRes changeId: Int = 0): Drawable? {
        return ContextCompat.getDrawable(context, getDrawableRes(originId, changeId))
    }

    private fun getApplicationContext(): Context {
        return BaseApplication.getApp().applicationContext
    }

    /**
     * 得到Resouce对象
     */

    fun getResource(): Resources {
        try {
            val activity: Activity? = AppManager.getINSTANCE().topActivity
            if (activity != null) {
                return activity.resources
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return getContext().resources
    }

    fun getResource(context: Context): Resources {
        try {
            val activity: Activity? = AppManager.getINSTANCE().topActivity
            if (activity != null) {
                return activity.resources
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return context.resources
    }

    /**
     * 获取当前栈顶activity的上下文，如果不存在获取全局
     */
    private fun getContext(): Context {
        try {
            val activity: Activity? = AppManager.getINSTANCE().topActivity
            if (activity != null) {
                return activity
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return getApplicationContext()
    }


    /**
     * Return the string value
     */
    @JvmStatic
    fun getString(context: Context?, @StringRes id: Int): String {
        return getString(context, id, null)
    }

    /**
     * Return the string value
     */
    @JvmStatic
    fun getString(context: Context?, @StringRes id: Int, vararg formatArgs: Any?): String {
        return try {
            format(context, id, context?.resources?.getString(id), *formatArgs)
        } catch (e: Exception) {
            e.printStackTrace()
            uploadStringFormatError(getResourceNameById(context, id))
            id.toString()

        }
    }


    /**
     * Return the string value
     */
    @JvmStatic
    fun formatStr(context: Context?, @StringRes id: Int, vararg formatArgs: Any?): String {
        return try {
            format(context, id, context?.resources?.getString(id), *formatArgs)
        } catch (e: Exception) {
            e.printStackTrace()
            uploadStringFormatError(getResourceNameById(context, id))
            id.toString()
        }
    }


    /**
     * Return the string
     */
    private fun getStringArray(context: Context?, @ArrayRes id: Int): Array<String> {
        return try {
            context?.resources?.getStringArray(id) ?: arrayOf()
        } catch (e: Exception) {
            e.printStackTrace()
            arrayOf(id.toString())
        }
    }

    /**
     * Format the string.
     */
    @JvmStatic
    fun format(context: Context?, @StringRes id: Int, str: String?, vararg args: Any?): String {
        var text = str
        if (args.isNotEmpty() && str?.isNotEmpty() == true) {
            try {
                text = String.format(str, *args)
            } catch (e: Exception) {
                e.printStackTrace()
                uploadStringFormatError(getResourceNameById(context, id))
            }
        }
        return text ?: ""
    }


    /**
     * 用于抛异常日志上报获取对应的资源keyName
     */
    @JvmStatic
    fun getResourceNameById(context: Context?, @StringRes id: Int): String {
        return context?.resources?.getResourceName(id) ?: "-1"
    }


    /**
     * String异常，日志上报方法
     */
    private fun uploadStringFormatError(stringKey: String) {


    }


    /**
     * 在TextView的最后添加图片  一般用于提示的图片
     */
    @JvmStatic
    fun setImageForTextEnd(textView: TextView, content: String, drawableId: Int) {
        try {
            textView.post {
                // 获取第一行的宽度
                val lineWidth = textView.layout.getLineWidth(0)
                // 获取第一行最后一个字符的下标
                val lineEnd = textView.layout.getLineEnd(0)
                // 计算每个字符占有的宽度
                val widthPerChar = lineWidth / (lineEnd + 1)
                // 计算textView一行能放下多少字符
                var numberPerLine = Math.floor((textView.width / widthPerChar).toDouble()).toInt()
                // 在原始字符串插入一个空格

                if (numberPerLine > content.length + 1) {
                    numberPerLine = content.length + 1
                }

                val stringBuilder = StringBuilder(
                    content
                )
                    .insert(numberPerLine - 1, "  ")

                val spannableString = SpannableString("$stringBuilder ")
                val drawable = getDrawable(drawableId)
                drawable?.let {
                    drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                    val imageSpan = ImageSpan(drawable, ImageSpan.ALIGN_BASELINE)
                    spannableString.setSpan(
                        imageSpan, spannableString.length - 1, spannableString.length,
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                    )
                    textView.text = spannableString
                }
            }
        } catch (e: Exception) {

        }

    }
}