package com.cstb.vigiphone3.data.database;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * MyDatabase class, used to create the dbFlow instance
 */
@Database(name = com.cstb.vigiphone3.data.database.MyDatabase.NAME, version = com.cstb.vigiphone3.data.database.MyDatabase.VERSION)
public class MyDatabase {
    static final String NAME = "MyDataBase";
    static final int VERSION = 1;
}
