package com.tw.speedbrowser.manager

import com.tw.speedbrowser.datamanager.CacheManager
import com.tw.speedbrowser.model.FavorModel

/**
 *
 * @author: Baron
 * @date  : 2023/2/24 20:38
 * @email : baron@niubi.im
 */
object FavorManager {
    private const val MAX_COUNT = 100
    private const val KEY_FAVOR = "key_favor"
    private val mList by lazy {
        ArrayList<FavorModel>()
    }

    fun init() {
        CacheManager.getAsyncCache<List<FavorModel>>(KEY_FAVOR) { t ->
            t?.apply {
                mList.clear()
                mList.addAll(t)
            }
        }
    }

    fun put(data: FavorModel) {
//        if (mList.contains(data)) {
//            return
//        }
        while (mList.size >= MAX_COUNT) {
            mList.removeAt(0)
        }
        mList.add(data)
        CacheManager.asyncSave(KEY_FAVOR, mList)
    }

    fun remove(index: Int) {
        if (index >= 0 && index < mList.size) {
            mList.removeAt(mList.size - 1 - index)
            CacheManager.asyncSave(KEY_FAVOR, mList)
        }
    }

    fun get():List<FavorModel> {
        return mList.reversed()
    }
}