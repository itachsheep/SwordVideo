package com.tw.speedbrowser.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.tw.speedbrowser.R
import com.tw.speedbrowser.model.RecentSearchModel

/**
 *
 * @author: Baron
 * @date  : 2023/2/24 20:48
 * @email : baron@niubi.im
 */
class RecentSearchAdapter: BaseQuickAdapter<RecentSearchModel,BaseViewHolder>(R.layout.item_recent_search) {

    init {
        addChildClickViewIds(R.id.mIvClear)
    }
    override fun convert(holder: BaseViewHolder, item: RecentSearchModel) {
        val mNetTitle = holder.getView<TextView>(R.id.mNetTitle)
        val mNetUrl = holder.getView<TextView>(R.id.mNetUrl)
        mNetTitle.text = item.title
        mNetUrl.text = item.netUrl
    }

}