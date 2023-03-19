package com.tw.speedbrowser.base.ui

import android.os.Bundle
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import io.reactivex.disposables.CompositeDisposable

//基础公共类
abstract class BaseNewActivity : RxAppCompatActivity() {

    var mIsVisible: Boolean = false
    protected var mOnCreateTime = 0L
    protected var mFirstCreateForLog: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        mOnCreateTime = System.currentTimeMillis()
        mFirstCreateForLog = true
        super.onCreate(savedInstanceState)
    }

    //这个Disposable针对的在当前所有的Disposable
    protected val mCompositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }
    //crowdin 语言热更新 https://github.com/crowdin/mobile-sdk-android
//    override fun getDelegate() = BaseContextWrappingDelegate(super.getDelegate())

    override fun onDestroy() {
        super.onDestroy()
        mIsVisible = false
        if (!mCompositeDisposable.isDisposed) {
            mCompositeDisposable.dispose()
        }
    }

    override fun onStart() {
        super.onStart()
        mIsVisible = true
    }

    override fun onResume() {
        super.onResume()
        mIsVisible = true
    }

    override fun onStop() {
        super.onStop()
        mIsVisible = false
    }
}