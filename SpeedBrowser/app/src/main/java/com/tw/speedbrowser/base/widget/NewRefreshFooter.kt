package com.tw.speedbrowser.base.widget

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.scwang.smartrefresh.layout.api.RefreshFooter
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState
import com.scwang.smartrefresh.layout.internal.InternalAbstract
import com.tw.speedbrowser.R
import com.tw.speedbrowser.base.utils.ResUtils.getString

/**
 * Created by chico on 2022/09/02.
 */
class NewRefreshFooter : InternalAbstract, RefreshFooter {

    private val mTvFooter: TextView?
    private val mIvFooter: LottieAnimationView?

    constructor(context: Context?) : super(context, null, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        val view = inflate(context, R.layout.new_refresh_footer, this)
        mTvFooter = view.findViewById(R.id.mTvFooter)
        mIvFooter = view.findViewById(R.id.mIvFooter)
        mIvFooter.repeatCount = ValueAnimator.INFINITE
    }

    override fun getView(): View {
        return this
    }

    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        mIvFooter?.cancelAnimation()
        mIvFooter?.visibility = GONE
        super.onFinish(refreshLayout, success)
        return 500
    }

//    //不需要上啦就直接加载更多
//    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {
//       kernel.refreshLayout.setEnableAutoLoadMore(false)
//    }

    override fun onStateChanged(refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState) {
        if (mTvFooter == null || mIvFooter == null) {
            return
        }
        val context = mTvFooter.context ?: return
        when (newState) {
            RefreshState.PullUpToLoad -> {
                mIvFooter.setAnimation("pull_refresh.json")
                mIvFooter.visibility = VISIBLE
                mTvFooter.text = getString(context, R.string.srl_footer_pulling)
            }
            RefreshState.Loading -> {
                mIvFooter.setAnimation("loading_refresh.json")
                mIvFooter.playAnimation()
                mTvFooter.text = getString(context, R.string.srl_footer_refreshing)
            }
            RefreshState.ReleaseToLoad -> {
                mTvFooter.text = getString(context, R.string.srl_footer_release)
            }
            RefreshState.PullUpCanceled -> {
                mIvFooter.visibility = GONE
                mIvFooter.cancelAnimation()
            }
            RefreshState.LoadFinish -> {
                mIvFooter.visibility = GONE
                mIvFooter.cancelAnimation()
                mTvFooter.text = getString(context, R.string.srl_footer_finish)
            }
            else -> {}
        }
        super.onStateChanged(refreshLayout, oldState, newState)
    }

    override fun onMoving(isDragging: Boolean, percent: Float, offset: Int, height: Int, maxDragHeight: Int) {
        super.onMoving(isDragging, percent, offset, height, maxDragHeight)
        if (mIvFooter != null && !mIvFooter.isAnimating) {
            mIvFooter.progress = minOf(percent, 1f)
        }
    }

}