package com.cstb.vigiphone3.data.database;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by Benjisora on 03/02/2017.
 */

@Database(name = com.cstb.vigiphone3.data.database.MyDatabase.NAME, version = com.cstb.vigiphone3.data.database.MyDatabase.VERSION)
public class MyDatabase {
    public static final String NAME = "MyDataBase";
    public static final int VERSION = 1;
}
