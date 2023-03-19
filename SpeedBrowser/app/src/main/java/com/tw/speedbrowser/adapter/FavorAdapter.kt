package com.tw.speedbrowser.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.tw.speedbrowser.R
import com.tw.speedbrowser.model.FavorModel
import com.tw.speedbrowser.model.RecentSearchModel

/**
 *
 * @author: Baron
 * @date  : 2023/2/24 20:48
 * @email : baron@niubi.im
 */
class FavorAdapter: BaseQuickAdapter<FavorModel,BaseViewHolder>(R.layout.item_favor) {

    init {
        addChildClickViewIds(R.id.mIvClear)
    }

    override fun convert(holder: BaseViewHolder, item: FavorModel) {
        val mNetTitle = holder.getView<TextView>(R.id.mNetTitle)
        val mNetUrl = holder.getView<TextView>(R.id.mNetUrl)
        mNetTitle.text = item.title
        mNetUrl.text = item.netUrl
    }

}