package com.tw.speedbrowser.base.widget

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.jakewharton.rxbinding.widget.RxTextView
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent
import com.tw.speedbrowser.R
import com.tw.speedbrowser.base.utils.ResUtils
import kotlinx.android.synthetic.main.layout_bingx_search.view.*
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * BingX的搜索view
 * https://bingxteam.larksuite.com/wiki/wikusjAZVfzb8zBHy5PU46jOV5q  使用方法
 *
 */
class BingXSearchView : FrameLayout {

    private var mCallbackListener: SearchCallbackListener? = null

    private var mContext: Context? = null
    private var keyboardStatusDetector: KeyboardStatusDetector? = null


    private var mInputHint = ""
    private var mShowCancel = false

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.layout_bingx_search, this)

        val ta = context.obtainStyledAttributes(attrs, R.styleable.BingXSearch)

        mInputHint = ta.getString(R.styleable.BingXSearch_input_hint) ?: ""

        mShowCancel = ta.getBoolean(R.styleable.BingXSearch_show_cancel, false)

        if (mShowCancel) {
            tv_cancel.visibility = View.VISIBLE
        } else {
            tv_cancel.visibility = View.GONE
        }

        if (!TextUtils.isEmpty(mInputHint)) {
            et_search.hint = mInputHint
        }

        ta.recycle()

//        RxTextView.textChangeEvents(et_search).debounce(250, TimeUnit.MILLISECONDS)
//            .skip(1)//采用skip(1)原因：跳过 第1次请求 = 初始输入框的空字符状态
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(getKeyToSearch())

        iv_clear_key.setOnClickListener {
            iv_clear_key.visibility = View.GONE
            et_search.text = null
            mCallbackListener?.searchKey("")
        }

        tv_cancel.setOnClickListener {
            mCallbackListener?.cancel()
        }
    }

    private fun getKeyToSearch(): rx.Observer<in TextViewTextChangeEvent> {
        return object : rx.Observer<TextViewTextChangeEvent> {
            override fun onCompleted() {}

            override fun onError(e: Throwable) {}

            override fun onNext(key: TextViewTextChangeEvent) {
                mCallbackListener?.searchKey(key.text().toString().trim())
                // 在这里变化下颜色
                if (key.text().toString().isNullOrEmpty()) {
                    iv_clear_key.visibility = View.GONE
                } else {
                    iv_clear_key.visibility = View.VISIBLE
                }
            }
        }
    }

    // 释放下
    fun onDestroy() {
        mCallbackListener = null
        mContext = null
        keyboardStatusDetector = null
    }

    fun setSearchCallbackListener(context: Context, listener: SearchCallbackListener) {
        this.mContext = context
        if (mContext != null) {
            keyboardListener()
        }
        mCallbackListener = listener
    }

    private fun keyboardListener() {
        keyboardStatusDetector = KeyboardStatusDetector()
        if (mContext is Activity) {
            mContext?.let {
                keyboardStatusDetector?.registerActivity(mContext as Activity)
                keyboardStatusDetector?.setmVisibilityListener {
                    sl_et_content.isSelected = it
                    if (it) {
                        iv_search.setColorFilter(ResUtils.getColor(R.color.text_1_1A1A1A))
                    } else {
                        et_search.clearFocus()
                        iv_search.setColorFilter(ResUtils.getColor(R.color.text_3_999999))
                    }
                }
            }
        }
    }

    fun getInput():String{
        return et_search?.text.toString()
    }

    fun clearInput(){
        et_search?.clearFocus()
        et_search?.text = null
    }

    interface SearchCallbackListener {
        // 取消按钮的点击
        fun cancel()

        // 搜索key的传递
        fun searchKey(searchKey: String)
    }

}