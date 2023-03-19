package com.tw.speedbrowser.adapter

/**
 *
 * @author: FreddyChen
 * @date  : 2022/03/18 19:41
 * @email : freddychencsc@gmail.com
 */
//fun dividerBuilder(block: (Divider.Builder) -> Unit): Divider.Builder {
//    val d = Divider.Builder()
//    block(d)
//    return d
//}

fun dividerBuilder(block: (Divider.Builder) -> Unit): Divider.Builder = Divider.Builder().apply(block)