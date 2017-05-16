package com.yifarj.yifadinghuobao.database;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * SQLite
 *
 * @auther Czech.Yuan
 * @date 2017/5/16 17:09
 */
@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION)
public class AppDatabase {
    public static final String NAME = "YiDingHuoDatabase";

    public static final int VERSION = 1;
}
