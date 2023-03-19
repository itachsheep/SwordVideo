package com.tw.speedbrowser.manager

import com.tw.speedbrowser.model.SearchType

/**
 *
 * @author: Baron
 * @date  : 2023/3/6 08:54
 * @email : baron@niubi.im
 */
object DataManager {

    private var mSearchType: Int = SearchType.TYPE_GOOGLE

    fun getSearchType(): Int {
        return mSearchType
    }

    fun setSearchType(type: Int) {
        mSearchType = type
    }
}