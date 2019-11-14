package com.example.myapplication;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ImageProvider extends ContentProvider {

    private Context mContext;
    DBHelper mDbHelper = null;
    SQLiteDatabase db = null;

    public static final String AUTOHORITY = "cn.drake.imageprovider";

    public static final int SINGLE_CODE = 1;
    public static final int MULTI_CODE = 2;

    private static final UriMatcher mMatcher;
    static {
        mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //初始化
        mMatcher.addURI(AUTOHORITY, "single", SINGLE_CODE);
        mMatcher.addURI(AUTOHORITY, "multi", MULTI_CODE);
    }

    @Override
    public boolean onCreate() {

        mContext = getContext();
        //ContentProvider是在主线程初始化
        mDbHelper = new DBHelper(getContext());
        db = mDbHelper.getWritableDatabase();

        //清空两个表
        db.execSQL("delete from single");
        db.execSQL("delete from multi");

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        String table = getTableName(uri);

        return db.query(table,projection,selection,selectionArgs,null,null,sortOrder,null);


    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        //根据Uri匹配Uri_code,再匹配ContentProvider对应的表
        String table = getTableName(uri);

        db.insert(table, null, values);

        // 当该URI的ContentProvider数据发生变化时，通知外界（即访问该ContentProvider数据的访问者）
        mContext.getContentResolver().notifyChange(uri, null);

        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        String table = getTableName(uri);
        //清空指定数据库
        db.execSQL("delete from " + table);

        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }


    private String getTableName(Uri uri){
        String tableName = null;
        switch (mMatcher.match(uri)){
            case SINGLE_CODE:
                tableName = DBHelper.SINGLE_TABLE_NAME;
                break;
            case MULTI_CODE:
                tableName = DBHelper.MULTI_TABLE_NAME;
                break;
        }
        return tableName;
    }

}
