package com.shen.accountbook2.db.biz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * User表业务操作
 *
 * */
public class TypeEx extends BaseEx {


    public TypeEx(Context context) {
        super(context);
    }

    //    SQLiteDatabase dataBase = getDataBase();
    //    ContentValues values = new ContentValues();
    //    values.put("name", "heima");						// 字段  ： 值
    //    values.put("age", 5);
    //    values.put("phone", "010-82826816");
		/*
		 * 第一个参数 table，代表要将数据插入哪家表
		 * 第二个参数nullColumnHack，字符串类型，指明如果某一字段没有值，那么会将该字段的值设为NULL
		 * ，一般给该参数传递null就行如果没有特殊要求
		 * ，在这里我传递了phone字符串，也就是说当我的ContentValues中phone字段为空的时候系统自动给其值设置为NULL
		 * 第三个参数ContentValues 类似一个Map<key,value>的数据结构，key是表中的字段，value是值
		 */
    // dataBase.insert("person", null, values);  使用这个就可以个
    // 第二个参数的使用,基本都没用
    //    dataBase.insert("person", "phone", values);
    //    返回值是  -1  失败
	@Override
	public long Add(String table, ContentValues values) {
        long i = 0;
		try {
            String Path = mContext.getFileStreamPath("Type.db").getPath();
            SQLiteDatabase database = SQLiteDatabase.openDatabase(Path,null, SQLiteDatabase.OPEN_READONLY);
            i = database.insert(table, "", values);
            System.out.println(i+"");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDBConnect();
		}
        return i;
	}

	@Override
	public int Update(String table, ContentValues values, String whereClause,
                       String[] whereArgs) {
        int num = 0;
		try {
            String Path = mContext.getFileStreamPath("Type.db").getPath();
            SQLiteDatabase database = SQLiteDatabase.openDatabase(Path,null, SQLiteDatabase.OPEN_READONLY);
            num = database.update(table, values, whereClause, whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDBConnect();
		}
        return num;
	}

	@Override
	public int Delete(String table, String whereClause, String[] whereArgs) {
        int num = 0;
		try {
            String Path = mContext.getFileStreamPath("Type.db").getPath();
            SQLiteDatabase database = SQLiteDatabase.openDatabase(Path,null, SQLiteDatabase.OPEN_READONLY);
            num = database.delete(table, whereClause, whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDBConnect();
		}
        return num;
	}

	// 该方法可以修改返回值参数为List<T>或其他自定义返回值，注意关闭数据库连接。
	@Override
	public Cursor Query(String table, String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having,
                        String orderBy) {
		Cursor cursor = null;
		try {
            String Path = mContext.getFileStreamPath("Type.db").getPath();
            SQLiteDatabase database = SQLiteDatabase.openDatabase(Path,null, SQLiteDatabase.OPEN_READONLY);
			cursor = database.query(table,columns,selection,selectionArgs,groupBy, having, orderBy);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// TODO:关闭数据库连接的动作(super.stopDBConnect())，需在Cursor使用结束之后执行。
		}

		return cursor;
	}

}
