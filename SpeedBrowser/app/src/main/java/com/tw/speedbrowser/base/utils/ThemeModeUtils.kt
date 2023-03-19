package com.tw.speedbrowser.base.utils

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.tw.speedbrowser.datamanager.CacheManager

/**
 * Created by chico on 2021/8/12.
 */
object ThemeModeUtils {

    const val THEME_MODE = "theme_mode"
    const val MODE_NIGHT_NO = 1
    const val MODE_NIGHT_YES = 2
    const val MODE_NIGHT_FOLLOW_SYSTEM = 3

    var mCurrentUiMode = Configuration.UI_MODE_NIGHT_NO

    @JvmStatic
    fun getThemeMode(): Int {
        return CacheManager.getIntegerCache(THEME_MODE, MODE_NIGHT_NO)
    }

    @JvmStatic
    fun setThemeMode(themeMode: Int, hasSave: Boolean = true) {
        if (hasSave) {
            CacheManager.save(THEME_MODE, themeMode)
        }
        when (themeMode) {
            MODE_NIGHT_NO -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            MODE_NIGHT_YES -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            MODE_NIGHT_FOLLOW_SYSTEM -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    @JvmStatic
    fun isFollowSystem(context: Context): Boolean {
        return getThemeMode() == ThemeModeUtils.MODE_NIGHT_FOLLOW_SYSTEM
    }

    @JvmStatic
    fun isNight(context: Context): Boolean {
        return when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> {
                true
            }
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> {
                uiModeEqualNight(context)
            }
            else -> {
                false
            }
        }
    }

    private fun uiModeEqualNight(context: Context): Boolean {
        val uiMode = context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        return uiMode == Configuration.UI_MODE_NIGHT_YES
    }

    @JvmStatic
    fun initThemeMode(context: Context) {
        var mode: Int = AppCompatDelegate.MODE_NIGHT_NO
        when (getThemeMode()) {
            MODE_NIGHT_YES -> {
                mode = AppCompatDelegate.MODE_NIGHT_YES
            }
            MODE_NIGHT_FOLLOW_SYSTEM -> {
                if (uiModeEqualNight(context)) {
                    mode = AppCompatDelegate.MODE_NIGHT_YES
                }
            }
        }
        setThemeMode(mode, false)
    }

    @JvmStatic
    fun getCurrentUiMode(): Int {
        return mCurrentUiMode
    }

    /**
     * 当前应用的uimode
     */
    @JvmStatic
    fun setCurrentUiMode(context: Context) {
        mCurrentUiMode = context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
    }

}