package com.shen.accountbook2.domain;

import android.content.Context;
import android.database.Cursor;


import com.shen.accountbook2.config.Constant;
import com.shen.accountbook2.db.biz.TypeEx;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shen on 9/21 0021.
 */
public class TypeModelManage {

    private Cursor cursor_mainType;     // 主类型
    private Cursor cursor_type1;        // 次类型
    private Context mContext;
    private String typePath;            // "Type.db"的路径



    /** 存放所有解析(从数据库拿到)后的数据*/
    private List<MainTypeInfo> mainTypeList;
    private TypeEx typeEx;            //

    /**
     *
     * @param context
     */
    public TypeModelManage(Context context){
        mContext = context;
        typePath = mContext.getFileStreamPath("Type.db").getPath();
        typeEx = new TypeEx(mContext);

        mainTypeList = new ArrayList<MainTypeInfo>();
        getMainType();
    }

    public List<MainTypeInfo> getMainTypeList() {
        return mainTypeList;
    }

    public void setMainTypeList(List<MainTypeInfo> mainTypeList) {
        this.mainTypeList = mainTypeList;
    }

    private void getMainType(){
        cursor_mainType = typeEx.Query(Constant.TABLE_MAINTYPE, null, null ,null ,null, null, null);
        if(cursor_mainType.getCount() >= 1) {
            while(cursor_mainType.moveToNext()){
                MainTypeInfo mainTypeInfo = new MainTypeInfo();
                mainTypeInfo.setName(cursor_mainType.getString(1));
                mainTypeInfo.setType1List(getType1List(cursor_mainType.getInt(0)));
                mainTypeList.add(mainTypeInfo);
            }
            cursor_mainType.close();
            cursor_type1.close();
        }
    }

    /**
     * 获得"type1"类型 列表
     * @param mainTypeId    主类型的id(MainType 和 Type1 表中都有对应的)
     * @return
     */
    private List<Type1Info> getType1List(int mainTypeId){
        List<Type1Info> list = new ArrayList<Type1Info>();
        int i = 0;
        cursor_type1 = typeEx.Query(Constant.TABLE_TYPE1, null, "MainID=?" ,new String[]{String.valueOf(mainTypeId)} ,null, null, null);
//        cursor_type1 = typeEx.Query(Constant.TABLE_TYPE1, null, null ,null ,null, null, null);

        if(cursor_type1.getCount() >= 1) {
            while(cursor_type1.moveToNext()){
                Type1Info type1Info = new Type1Info();
                type1Info.setName(cursor_type1.getString(2));
                list.add(type1Info);
            }
        }
        return list;
    }

    /**
     * 拿到 maintype -- String[]
     * @return
     */
    public String[] mainType(){

        String[] mainType = new String[0];
        if(mainTypeList.size()!= 0 && mainTypeList != null){
            mainType = new String[mainTypeList.size()];
            for(int i=0; i<mainTypeList.size(); i++){
                mainType[i] = mainTypeList.get(i).getName();
            }
        }
        return mainType;
    }

    /**
     * 拿到 Type1 -- String[]
     * @return
     */
    public String[] type1(int mainType){

        String[] type1 = new String[0];
        List<Type1Info> list = new ArrayList<Type1Info>();
        list = mainTypeList.get(mainType).getType1List();
        if(list.size() != 0 && list != null){
            type1 = new String[list.size()];
            for(int i=0; i<list.size(); i++){
                type1[i] = list.get(i).getName();
            }
        }
        return type1;
    }

}
