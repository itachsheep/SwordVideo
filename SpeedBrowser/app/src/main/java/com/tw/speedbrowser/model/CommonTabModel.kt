package com.tw.speedbrowser.model

class CommonTabModel() : BaseEntity() {
    var type = 0
    var select = false
    var title = ""
    var uniqueName = ""  // 特有的字段，用于一些特殊场景，比如记录一些原始的状态  原始值
    var aliasName = "" // 别名，比如真实的名字  ETH本金   最终可用于title的拼接，eg：ETH本金(1)
    var showMsg = false
    var messageCount = 0 //动态消息未读数

    constructor(select: Boolean = false, title: String = "") : this() {
        this.select = select
        this.title = title
    }

    constructor(select: Boolean = false, title: String = "", itemType: Int) : this() {
        this.select = select
        this.title = title
        this.setItemType(itemType)
    }

    constructor(select: Boolean = false, title: String = "", showMsg: Boolean, itemType: Int) : this() {
        this.select = select
        this.title = title
        this.showMsg = showMsg
        this.setItemType(itemType)
    }
}