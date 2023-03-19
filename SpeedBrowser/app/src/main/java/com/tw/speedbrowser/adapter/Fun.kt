package com.tw.speedbrowser.adapter

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.tw.speedbrowser.model.CommonTabModel

/**
 *
 * @author: Baron
 * @date  : 2023/2/24 21:35
 * @email : baron@niubi.im
 */
object Fun {

    interface TabPositionListener {
        fun position(position: Int)
        fun tabClickPosition(position: Int)
    }

    fun registerListener(
        viewPager: ViewPager,
        adapter: CommonTabAdapter,
        recyclerView: RecyclerView,
        listener: TabPositionListener? = null
    ) {
        viewPager.addOnPageChangeListener(object : OnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                adapter.updatePageSelected(position)
                adapter.data.forEachIndexed { index, homeCoinTabModel ->
                    (homeCoinTabModel as CommonTabModel).let {
                        it.select = index == position
                    }
                }
                recyclerView.smoothScrollToPosition(position)
                adapter.notifyDataSetChanged()
                listener?.position(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                adapter.updateScrollState(state)
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                adapter.updateUnderLine(viewPager.currentItem,position,positionOffset,positionOffsetPixels)
            }
        })

        adapter.setOnItemClickListener { adapter, view, position ->
            if (position < adapter.data.size) {
                adapter.data.forEachIndexed { index, coinTab ->
                    (coinTab as CommonTabModel).let {
                        it.select = index == position
                    }
                }
                adapter.notifyDataSetChanged()
                recyclerView.smoothScrollToPosition(position)
                viewPager.setCurrentItem(position,true)
//                    Logger.e("---------/--->viewpager count ${viewPager.adapter?.count}")
            }
        }
    }
}