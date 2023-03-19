package com.tw.speedbrowser.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

/**
 * 基准类  所有的model都需要继承这个基准类
 */
public class BaseEntity implements Serializable, MultiItemEntity {

    public int itemType = -1;

    public void setItemType (int itemType) {
        this.itemType = itemType;
    }

    public int getItemTypeMethod() {
        return itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
