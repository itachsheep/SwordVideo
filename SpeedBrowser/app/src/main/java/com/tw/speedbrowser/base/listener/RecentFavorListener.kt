package com.tw.speedbrowser.base.listener

import com.tw.speedbrowser.model.FavorModel
import com.tw.speedbrowser.model.RecentSearchModel

/**
 *
 * @author: Baron
 * @date  : 2023/3/2 08:36
 * @email : baron@niubi.im
 */
interface RecentFavorListener {
    fun onRecentItemClick(data: RecentSearchModel?)
    fun onFavorItemClick(data: FavorModel?)
}