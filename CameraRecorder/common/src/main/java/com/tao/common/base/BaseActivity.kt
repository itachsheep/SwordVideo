package com.tao.common.base

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Chronometer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * <pre>
 *     author  : devyk on 2020-05-24 23:40
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is BaseActivity
 * </pre>
 */

abstract class BaseActivity<T> : AppCompatActivity() {
    public var TAG = javaClass.simpleName;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onContentViewBefore()

        if (getLayoutId() is Int){
            setContentView(getLayoutId() as Int)
        }else  if (getLayoutId() is View){
            setContentView(getLayoutId() as View)
        }
        checkPermission()
        init();
        initListener();
        initData();


    }

    /**
     * 在 setContentView 之前需要做的初始化
     */
    protected open fun onContentViewBefore() {

    }

    abstract fun initListener()

    abstract fun initData()

    abstract fun init()

    abstract fun getLayoutId(): T


    protected fun setNotTitleBar() {
        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(Color.TRANSPARENT)
        window.setNavigationBarColor(Color.TRANSPARENT)
        //去掉标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
    }


    /**
     * 检查权限
     */
    @SuppressLint("CheckResult")
    protected fun checkPermission() {
    }


    public fun startTime(timer: Chronometer) {
        var hour = ((SystemClock.elapsedRealtime() - timer.getBase()) / 1000 / 60).toInt();
        timer.setFormat("0${hour}:%s");
        timer.start()
    }

    public fun cleanTime(timer: Chronometer) {
        timer?.setBase(SystemClock.elapsedRealtime());
        timer?.stop()
    }


}