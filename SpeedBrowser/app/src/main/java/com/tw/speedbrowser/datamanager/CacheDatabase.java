package com.tw.speedbrowser.datamanager;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.tw.speedbrowser.base.BaseApplication;


@Database(entities = {Cache.class} ,version = 1)
public abstract class CacheDatabase extends RoomDatabase {

    private static CacheDatabase database;

    static {
        database = Room.databaseBuilder(BaseApplication.getApp(),CacheDatabase.class,"speed_browser_cache")
                // 是否允许在主线程查看
                .allowMainThreadQueries()
                //数据库创建和打开后的回调
                //.addCallback()
                //设置查询的线程池
                //.setQueryExecutor()
                //.openHelperFactory()
                //room的日志模式
                .setJournalMode(JournalMode.TRUNCATE)
                //数据库升级异常之后的回滚
                //.fallbackToDestructiveMigration()
                //数据库升级异常后根据指定版本进行回滚
                //.fallbackToDestructiveMigrationFrom()
                // .addMigrations(CacheDatabase.sMigration)
                .build();

    }

    public abstract CacheDao getCache() throws Exception;

    public static CacheDatabase get(){
        return database;
    }

}
