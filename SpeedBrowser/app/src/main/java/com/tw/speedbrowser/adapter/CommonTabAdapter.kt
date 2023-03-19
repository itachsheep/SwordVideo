package com.tw.speedbrowser.adapter

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.tw.speedbrowser.R
import com.tw.speedbrowser.base.SpeedApplication
import com.tw.speedbrowser.base.utils.LogUtils
import com.tw.speedbrowser.base.utils.ResUtils
import com.tw.speedbrowser.base.utils.RtlUtils
import com.tw.speedbrowser.base.widget.BOLD
import com.tw.speedbrowser.base.widget.REGULAR
import com.tw.speedbrowser.base.widget.SleTextButton
import com.tw.speedbrowser.base.widget.TextStyle
import com.tw.speedbrowser.model.CommonTabModel
import kotlin.math.abs

/**
 *
 * @author: Baron
 * @date  : 2023/2/24 09:39
 * @email : baron@niubi.im
 */
class CommonTabAdapter (data: MutableList<CommonTabModel> = mutableListOf()) :
    BaseMultiItemQuickAdapter<CommonTabModel, BaseViewHolder>(data) {

    companion object {
        // 顶层的tab类型
        const val LEVEL_WEB_TYPE = 0
        const val LEVEL_MAIN_TYPE = 1

    }

    init {
        addItemType(LEVEL_WEB_TYPE, R.layout.common_tab_list_item)
        addItemType(LEVEL_MAIN_TYPE,R.layout.common_tab_main_list_item)
    }

    override fun convert(holder: BaseViewHolder, item: CommonTabModel) {
        when (item.itemType) {
            LEVEL_WEB_TYPE,LEVEL_MAIN_TYPE -> {
                val mViewSpace = holder.getView<View>(R.id.view_space)
                tabLines[holder.adapterPosition] = mViewSpace
               val mTvTitle = holder.getView<SleTextButton>(R.id.tv_title)
                mTvTitle.text = item.title
                if (item.select) {
                    mTvTitle.setTextColor(ResUtils.getColor(context,R.color.text_1_1A1A1A))
                    if (scrollState != ViewPager.SCROLL_STATE_SETTLING) {
                        mViewSpace.visibility = View.VISIBLE
                    }
                } else {
                    mTvTitle.setTextColor(ResUtils.getColor(context,R.color.text_2_555555))
                    mViewSpace.visibility = View.INVISIBLE
                }
            }
        }
    }

    private var isOver2Tab = false //跨了好几个tab点击
    private var tabLines = HashMap<Int, View>()
    private var scrollState = -1 //viewpage 滚动的状态

    fun updateScrollState(state: Int) {
//        Log.e("commonTabAdapter", "state:$state  current:$currentPosition")
        scrollState = state
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            isOver2Tab = false
            tabLines.values.forEach {
                it.translationX = 0f
            }

        }  //完成一次滑动后重置
    }

    fun updateUnderLine(currentPosition: Int, position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        this.currentPosition = currentPosition
        tabLines[currentPosition]?.visibility = View.VISIBLE
        if (isOver2Tab) {
            return
        }
        if (abs(currentPosition - position) > 1 || position > currentPosition) return


        if (currentPosition == position) {  //左滑
            tabLines[currentPosition]?.let {
                if (RtlUtils.isRtl(SpeedApplication.getApp())) {
                    it.translationX = -it.width * (1 - positionOffset)
                } else {
                    it.translationX = it.width * positionOffset
                }

            }
        }
        if (currentPosition > position) { //右话
            tabLines[currentPosition]?.let {
                if (RtlUtils.isRtl(SpeedApplication.getApp())) {
                    it.translationX = it.width * (1-positionOffset)
                } else {
                    it.translationX = -it.width * (1 - positionOffset)
                }

            }
        }
    }

    private var currentPosition = 0
    fun updatePageSelected(pos: Int) {
        isOver2Tab = abs(pos - currentPosition) > 1
    }

    override fun setOnItemClick(v: View, position: Int) {

        super.setOnItemClick(v, position)
        isOver2Tab = abs(position - currentPosition) > 1

    }
}