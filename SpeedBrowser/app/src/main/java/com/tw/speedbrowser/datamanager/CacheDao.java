package com.tw.speedbrowser.datamanager;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import io.reactivex.Single;

@Dao
public interface CacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long save(Cache cache);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> asyncSave(Cache cache);


    @Query("select * from cache where `key` = :key")
    Cache getCache(String key);


    @Query("select * from cache where `key` = :key")
    Single<Cache> getAsyncCache(String key);


    @Query("delete from cache where `key` = :key")
    int delete(String key);

    @Query("delete from cache where `key` = :key")
    Single<Integer> asyncDelete(String key);

    @Delete
    int delete(Cache cache);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(Cache cache);

}
