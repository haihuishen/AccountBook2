package com.shen.accountbook2.domain;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.shen.accountbook2.config.Constant;
import com.shen.accountbook2.db.biz.MainTypeEx;
import com.shen.accountbook2.db.biz.Type1Ex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shen on 9/10 0010.
 */
public class TypeManage {

    Context context;
    private MainTypeEx mainTypeEx;
    private Type1Ex type1Ex;

    private Cursor cursor_mainType;
    private Cursor cursor_type1;
    private Cursor cursor_type;

    public ArrayList<ConsumptionMainType> list_MainType;
    //private ArrayList<ConsumptionType1> list_Type1;
    public Map<Integer,ArrayList<ConsumptionType1>> map_list_Type1;

    public List<String> list_type1_type;

    public TypeManage(Context context){
        this.context = context;
        init();
        initData();
    }

    private void init(){
        mainTypeEx = new MainTypeEx(context);
        type1Ex = new Type1Ex(context);

        cursor_mainType = null;
        cursor_type1 = null;

        list_MainType = new ArrayList<ConsumptionMainType>();
        map_list_Type1 = new HashMap<Integer,ArrayList<ConsumptionType1>>();
        list_type1_type = new ArrayList<String>();
    }

    private void initData(){
        cursor_mainType = mainTypeEx.Query(Constant.TABLE_MAINTYPE,null,null,null,null,null,"id asc");
        if(cursor_mainType.getCount() >= 1) {
            while(cursor_mainType.moveToNext()){
                ConsumptionMainType mt = new ConsumptionMainType();
                mt.setId(cursor_mainType.getInt(0));
                mt.setType(cursor_mainType.getString(1));
                list_MainType.add(mt);
            }
        }

        cursor_type1 = type1Ex.Query(Constant.TABLE_TYPE1,null,null,null,null,null,"mainId asc");
        if(cursor_type1.getCount() >= 1) {
            int i = 0;
            ArrayList<ConsumptionType1> list_Type1 = null;
            while(cursor_type1.moveToNext()){
                ConsumptionType1 t1 = new ConsumptionType1();
                t1.setMainId(cursor_type1.getInt(0));
                t1.setId(cursor_type1.getInt(1));
                t1.setType(cursor_type1.getString(2));

                if(i != cursor_type1.getInt(0)){
                    if(i != 0)
                        map_list_Type1.put(i,list_Type1);
                    i = cursor_type1.getInt(0);
                    list_Type1 = new ArrayList<ConsumptionType1>();
                }
                list_Type1.add(t1);
            }
        }


        String Path = context.getFileStreamPath("Type.db").getPath();
        SQLiteDatabase database = SQLiteDatabase.openDatabase(Path,null,SQLiteDatabase.OPEN_READONLY);
        cursor_type = database.rawQuery("select m.id,m.type,o.id,o.type from maintype as m,type1 as o " +
                "where o.MainId=m.id group by m.id,m.type,o.id,o.type order by m.id,o.id",null);


    }


    public String[] getType1(String type){
        int id = 0;
        int b = list_type1_type.size();
        list_type1_type.clear();

        cursor_mainType = mainTypeEx.Query(Constant.TABLE_MAINTYPE,new String[]{"id"},"type=?",new String[]{type},null,null,"id asc");
        if(cursor_mainType.moveToNext()) {
             id = cursor_mainType.getInt(0);
        }
        cursor_type1 = type1Ex.Query(Constant.TABLE_TYPE1,new String[]{"type"},"mainid=?",new String[]{id+""},null,null,"id asc");
        while(cursor_type1.moveToNext()){
            list_type1_type.add(cursor_type1.getString(0));
        }
        String[] s_Type = new String[list_type1_type.size()];
        list_type1_type.toArray(s_Type);              // 将 List<String> 转换成 String[]
        return s_Type;
    }

}
