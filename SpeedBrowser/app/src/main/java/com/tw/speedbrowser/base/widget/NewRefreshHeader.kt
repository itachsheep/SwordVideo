package com.tw.speedbrowser.base.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState
import com.scwang.smartrefresh.layout.internal.InternalAbstract
import com.tw.speedbrowser.R
import com.tw.speedbrowser.base.utils.ResUtils.getString


/**
 * Created by chico on 2022/08/13.
 */
class NewRefreshHeader : InternalAbstract, RefreshHeader {

    private val mTvHeader: TextView?
    private val mIvHeader: LottieAnimationView?

    constructor(context: Context?) : super(context, null, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        val view = inflate(context, R.layout.new_refresh_header, this)
        mTvHeader = view.findViewById(R.id.mTvHeader)
        mIvHeader = view.findViewById(R.id.mIvHeader)
        mIvHeader.repeatCount = ValueAnimator.INFINITE
    }

    override fun getView(): View {
        return this
    }

    fun setAccentColor(color: Int): NewRefreshHeader {
        mTvHeader?.setTextColor(color)
        val filter = SimpleColorFilter(color)
        val keyPath = KeyPath("**")
        val callback = LottieValueCallback<ColorFilter>(filter)
        mIvHeader?.addValueCallback(keyPath, LottieProperty.COLOR_FILTER, callback)
        return this
    }

    fun setAccentTextSize(size: Float): NewRefreshHeader {
        mTvHeader?.textSize = size
        return this
    }

    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        mIvHeader?.cancelAnimation()
        mIvHeader?.visibility = GONE
        super.onFinish(refreshLayout, success)
        return 500
    }

    override fun onStateChanged(refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState) {
        if (mTvHeader == null || mIvHeader == null) {
            return
        }
        val context = mTvHeader.context ?: return
        when (newState) {
            //下拉刷新
            RefreshState.PullDownToRefresh -> {
                mIvHeader.setAnimation("pull_refresh.json")
                mIvHeader.visibility = VISIBLE
                mTvHeader.text = getString(context, R.string.srl_header_pulling)
            }
            //释放刷新
            RefreshState.ReleaseToRefresh -> {
                mTvHeader.text = getString(context, R.string.srl_header_release)
            }
            //刷新中
            RefreshState.Refreshing -> {
                mIvHeader.setAnimation("loading_refresh.json")
                mIvHeader.playAnimation()
                mTvHeader.text = getString(context, R.string.srl_header_refreshing)
            }
            //刷新取消
            RefreshState.PullDownCanceled -> {
                mIvHeader.visibility = GONE
                mIvHeader.cancelAnimation()
            }
            //刷新完成
            RefreshState.RefreshFinish -> {
                mIvHeader.visibility = GONE
                mIvHeader.cancelAnimation()
                mTvHeader.text = getString(context, R.string.srl_header_finish)
            }
            else -> {}
        }
        super.onStateChanged(refreshLayout, oldState, newState)
    }

    override fun onMoving(isDragging: Boolean, percent: Float, offset: Int, height: Int, maxDragHeight: Int) {
        super.onMoving(isDragging, percent, offset, height, maxDragHeight)
        if (mIvHeader != null && !mIvHeader.isAnimating) {
            mIvHeader.progress = minOf(percent, 1f)
        }
    }

}