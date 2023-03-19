package com.tw.speedbrowser.model

/**
 *
 * @author: Baron
 * @date  : 2023/2/24 20:15
 * @email : baron@niubi.im
 */
class FavorModel: BaseEntity() {
    var title = ""
    var netUrl = ""

    override fun equals(other: Any?): Boolean {
        return if(other is FavorModel) {
            netUrl.equals(other.netUrl,false)
        } else {
            true
        }
    }

    override fun hashCode(): Int {
        return netUrl.hashCode()
    }
}