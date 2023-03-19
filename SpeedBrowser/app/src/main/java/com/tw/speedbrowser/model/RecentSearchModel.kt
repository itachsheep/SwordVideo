package com.tw.speedbrowser.model

/**
 *
 * @author: Baron
 * @date  : 2023/2/24 20:14
 * @email : baron@niubi.im
 */
class RecentSearchModel: BaseEntity() {
    var title = ""
    var netUrl = ""

    override fun equals(other: Any?): Boolean {
        return if(other is RecentSearchModel) {
            title.equals(other.title,true)
        } else {
            true
        }
    }

    override fun hashCode(): Int {
        return netUrl.hashCode()
    }
}