package com.shen.accountbook2.ui.view.MyMenuRecyclerView;

/**
 * Created by shen on 11/20 0020.
 */
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.shen.accountbook2.config.Constant;
import com.shen.accountbook2.db.helper.DatabaseHelper;

/**
 * Created by zoom on 2016/3/30.
 */
public class AccounBookProvider extends ContentProvider {

    //主机名信息：对应清单文件的authorities属性
    private static final String AUTHORITY="com.shen.accountbook2.ui.view.MyMenuRecyclerView.AccounBookProvider";
    public static final Uri URI_ACCOUNTBOOK2_ALL=Uri.parse("content://"+AUTHORITY+"/accountbook2");

    private static UriMatcher matcher;

    private DatabaseHelper dbHelper = null;
    private SQLiteDatabase db = null;

    private static final int ACCOUNTBOOK2_ALL =0;
    private static final int ACCOUNTBOOK2_ONE =1;

    static {
        matcher=new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY,"accountbook2/", ACCOUNTBOOK2_ALL);
        matcher.addURI(AUTHORITY,"accountbook2/#", ACCOUNTBOOK2_ONE);
    }

    // 内容提供者创建时候调用
    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext(), Constant.DB_NAME, 1);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(Constant.TABLE_CONSUMPTION,projection,selection,selectionArgs,null,null,sortOrder);
        // 对获取的Cursor数据设置需要监听的URI（即，在ContentProvider的query()方法
        // 或者Loader的loadingBackground()方法中调用Cursor的setNotificationUri()方法）；
        cursor.setNotificationUri(getContext().getContentResolver(),URI_ACCOUNTBOOK2_ALL);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        db=dbHelper.getWritableDatabase();
        int count = 0;
        count = db.delete(Constant.TABLE_CONSUMPTION, selection, selectionArgs);
        notifyDataSetChanged();
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        db=dbHelper.getWritableDatabase();
        int count = 0;
        count = db.update(Constant.TABLE_CONSUMPTION, values, selection, selectionArgs);
        notifyDataSetChanged();
        return count;
    }

    /**
     * 数据刷新了，更改界面
     */
    public void notifyDataSetChanged() {
        getContext().getContentResolver().notifyChange(URI_ACCOUNTBOOK2_ALL,null);
    }

    /**
     * 打开数据库
     */
    public void openDBConnect() {
        dbHelper = new DatabaseHelper(getContext(), Constant.DB_NAME, 1);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 关闭数据库
     */
    public void closeDBConnect() {
        if (db != null) {
            db.close();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
