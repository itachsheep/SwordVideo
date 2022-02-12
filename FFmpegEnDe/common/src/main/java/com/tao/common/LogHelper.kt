package com.tao.common

import android.util.Log

/**
 * <pre>
 *     author  : devyk on 2020-06-02 00:07
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is LogHelper
 * </pre>
 */
public object LogHelper {
    val mTag = "FFmpegUse."
    fun i(tag: String, info: String) {
        Log.i(mTag + tag,info)
    }

    fun e(tag: String, info: String) {
        Log.e(mTag + tag,info)
    }

    fun w(tag: String, info: String) {
        Log.w(mTag + tag,info)
    }

    fun d(tag: String, info: String) {
        Log.d(mTag + tag,info)
    }
}