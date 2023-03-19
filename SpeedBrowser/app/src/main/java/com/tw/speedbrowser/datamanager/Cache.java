package com.tw.speedbrowser.datamanager;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Entity(tableName = "cache")
public class Cache implements Serializable {

    @PrimaryKey
    @NotNull
    public String key;


    public byte[] data;

}
