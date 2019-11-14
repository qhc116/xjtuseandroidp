package com.example.myapplication;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {


    // 数据库名
    private static final String DATABASE_NAME = "Image.db";

    // 表名
    public static final String SINGLE_TABLE_NAME = "single";
    public static final String MULTI_TABLE_NAME = "multi";

    //版本
    private static final int DATABASE_VERSION = 6;


    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + MULTI_TABLE_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," + " data TEXT," + "resultJson TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + SINGLE_TABLE_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," + " data TEXT)" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
