package com.tw.speedbrowser.manager

import com.tw.speedbrowser.datamanager.CacheManager
import com.tw.speedbrowser.model.RecentSearchModel

/**
 *
 * @author: Baron
 * @date  : 2023/2/24 20:16
 * @email : baron@niubi.im
 */
object RecentSearchManager {
    private const val MAX_COUNT = 20
    private const val KEY_RECENT_SEARCH = "key_recent_search"
    private val mList by lazy {
        ArrayList<RecentSearchModel>()
    }

    fun init() {
        CacheManager.getAsyncCache<List<RecentSearchModel>>(KEY_RECENT_SEARCH) { t ->
            t?.apply {
                mList.clear()
                mList.addAll(t)
            }
        }
    }

    fun remove(index: Int) {
        if (index >= 0 && index < mList.size) {
            mList.removeAt( mList.size - 1 - index)
            CacheManager.asyncSave(KEY_RECENT_SEARCH, mList)
        }
    }

    fun put(data: RecentSearchModel) {
        if (mList.contains(data)) {
            return
        }
        while (mList.size >= MAX_COUNT) {
            mList.removeAt(0)
        }
        mList.add(data)
        CacheManager.asyncSave(KEY_RECENT_SEARCH, mList)
    }

    fun clear() {
        mList.clear()
        CacheManager.asyncSave(KEY_RECENT_SEARCH, mList)
    }

    fun get(): List<RecentSearchModel> {
        return mList.reversed()
    }

}