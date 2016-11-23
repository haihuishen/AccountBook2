package com.shen.accountbook2.db.helper;

import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库版本管理
 * */
public class DatabaseVersionManagement {

	/**
	 * 数据库版本升级：1 to 2 <p>
	 *
     * 第一步：将user表改名为temp_user <br>
     * 第二步：如果user表存在，删除掉 <br>
     * 第三步：如果user表不存在，创建user表 <br>
     * 第四步：将temp_user的内容插入到user中 <br>
     * 第五步：如果temp_user存在，将其删除<br>
	 * */
	public static void UpgradedVersion1To2(SQLiteDatabase db) {

		try {
			db.execSQL("alter table user rename to temp_user");

			db.execSQL("drop table if exists user");

			db.execSQL("create table if not exists user(id INTEGER PRIMARY KEY AUTOINCREMENT, name varchar(10), remark varchar(50), age varchar(10))");

			db.execSQL("insert into user select id, name, remark, 'age_lala' from temp_user");

			db.execSQL("drop table if exists temp_user");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
