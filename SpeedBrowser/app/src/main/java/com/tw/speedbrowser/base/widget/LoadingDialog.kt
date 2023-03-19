package com.tw.speedbrowser.base.widget

import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.tw.speedbrowser.R
import kotlinx.android.synthetic.main.dialog_loading.*

/**
 * Created by chico on 2021/12/10.
 */
class LoadingDialog(context: Context) : Dialog(context, R.style.LoadDialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.dialog_loading)
        iv_load?.repeatCount = ValueAnimator.INFINITE
        iv_load?.playAnimation()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        iv_load?.let {
            if (!it.isAnimating) {
                it.playAnimation()
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        iv_load?.cancelAnimation()
    }

}