package com.itstep.oleg.myapplication4;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by android on 06.09.2017.
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper
{
    final static String TAG ="-----------MySQLite";
    //название базы
    private final static String dbName = "MyDbTwo";
    //версия базы
    private final static int dbVersion = 1;

    public final static String tblNameProducts = "Products";
    public final static String colProductName = "name";
    public final static String colProductPrice = "price";
    public final static String colProductWeight = "weight";
    public final static String colId = "_id";



    public MySQLiteOpenHelper(Context context)
    {
        super(context, MySQLiteOpenHelper.dbName, null, MySQLiteOpenHelper.dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Log.d(TAG, "onCreate : " + db.getPath());

        String query = "CREATE TABLE Products(" +
                "_id integer not null primary key autoincrement, " +
                "name text, " +
                "price real, " +
                "weight integer )";

        db.execSQL(query);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.d(TAG, "onUpgrade : " + db.getPath() + "; oldVersion : " + oldVersion + "; newVersion : " + newVersion);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.d(TAG, "onDowngrade : " + db.getPath() + "; oldVersion : " + oldVersion + "; newVersion : " + newVersion);
    }

    public void onOpen(SQLiteDatabase db)
    {
        Log.d(TAG, "onOpen : " + db.getPath());
    }



}

