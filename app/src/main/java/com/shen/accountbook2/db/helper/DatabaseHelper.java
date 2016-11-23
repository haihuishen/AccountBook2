package com.shen.accountbook2.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库管理
 *
 * */
public class DatabaseHelper extends SQLiteOpenHelper {

    /**
     *
     * @param context 	     上下文对象
     * @param dbName 		数据库名称
     * @param factory       游标结果集工厂，如果需要使用则需要自定义结果集工厂，null值代表使用默认结果集工厂
     * @param version 	     数据库版本号，必须大于等于1
     */
	private DatabaseHelper(Context context, String dbName, CursorFactory factory, int version) {
		super(context, dbName, factory, version);
	}

    /**
     *  新建数据库
     *
     * @param context 	     上下文对象
     * @param dbName		数据库名称
     * @param version	     数据库版本号，必须大于等于1
     */
	public DatabaseHelper(Context context, String dbName, int version) {
		this(context, dbName, null, version); // this -->"私有的构造函数"

	}

	/**
	 * 数据库第一次被调用时调用该方法，
     *
     * 	private DatabaseHelper dbHelper = null;
     *  private SQLiteDatabase db = null;
     * 		dbHelper = new DatabaseHelper(mContext, Constant.DB_NAME, dbVersion);
     *      db = dbHelper.getWritableDatabase();        -----------> 这句调用时，此方法被调用
     *
     * 这里面主要进行对数据库的初始化操作
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		System.out.println("DBHelper onCreate");
        db.execSQL("create table if not exists user(_id integer primary key autoincrement," +
                "name varchar(20) not null,password varchar(20) not null," +
                "sex tinyint(1),image varchar(50),birthday date,qq varchar(20))");
        db.execSQL("create table if not exists consumption(_id integer primary key autoincrement," +
                "user varchar(20) not null,"+
                "maintype varchar(20) not null,type1 varchar(20),concreteness varchar(30)," +
                "price decimal(18,2),number integer,unitprice decimal(18,2),image varchar(50),date date not null)");
        db.execSQL("create table if not exists assets(_id integer primary key autoincrement," +
                "user varchar(20) not null,"+
                "type varchar(20) not null,changetime date not null," +
                "what varchar(30),cardnum varchar(4),asset varchar(20) not null)");
    }
    /**
     * 数据库更新的时候调用该方法
     * @param db 				当前操作的数据库对象
     * @param oldVersion 		老版本号
     * @param newVersion 		新版本号
     */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		System.out.println("DBHelper onUpgrade");

		try {
            // // 备份数据库到SD卡的/aDBTest/DBTest.db
            // CopyDBToSDCard.CopyDB(mContext);
			for (int i = oldVersion; i < newVersion; i++) {
				switch (i) {
				case 1:
					DatabaseVersionManagement.UpgradedVersion1To2(db);
					break;

				default:
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
